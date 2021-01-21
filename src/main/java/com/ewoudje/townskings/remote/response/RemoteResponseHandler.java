package com.ewoudje.townskings.remote.response;

import com.ewoudje.townskings.TK;
import com.ewoudje.townskings.api.TKPlugin;
import com.ewoudje.townskings.util.UUIDUtil;
import io.sentry.Sentry;

import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;

public class RemoteResponseHandler {

    private final TKPlugin plugin;
    private int tick_max_fast = 100;
    private int tick_max_med = 30;
    private int tick_max_slow = 5;
    //private int timeout;
    private HashMap<String, RemoteResponseType> types = new HashMap<>();

    public RemoteResponseHandler(TKPlugin plugin) {
        this.plugin = plugin;
        plugin.getServer().getScheduler().runTaskTimerAsynchronously(plugin, this::run, 1, 1);
        //timeout = (1000 / 20) / 4 - 10; // TIMEOUT 1/4th of tick - 10 ms
    }

    public void register(RemoteResponseType type) {
        types.put(type.getName(), type);
    }

    private void run() {
        queue("mcqueue:fast", tick_max_fast);
        queue("mcqueue:med", tick_max_med);
        queue("mcqueue:slow", tick_max_slow);
    }

    private void queue(String name, int max) {
        for (int i = 0; i < max; i++) {
            Optional<UUID> uuid = UUIDUtil.fromString(TK.REDIS.lpop(name));
            if (uuid.isPresent()) {
                String key = "queue:" + uuid.get().toString();
                RemoteResponseType type = types.get(TK.REDIS.hget(key, "type"));
                final Object r;
                try {
                    r = type.requestValues(key);
                } catch (Exception e) {
                    Sentry.captureException(e);
                    e.printStackTrace();
                    return;
                }
                TK.REDIS.del(key);

                Runnable exe = () -> {
                    try {
                        type.execute(r);
                    } catch (Exception e) {
                        Sentry.captureException(e);
                        e.printStackTrace();
                        return;
                    }
                };

                if (type.sync()) {
                    plugin.getServer().getScheduler().runTask(plugin, exe);
                } else {
                    plugin.getServer().getScheduler().runTaskAsynchronously(plugin, exe);
                }
            } else {
                break;
            }
        }
    }

}
