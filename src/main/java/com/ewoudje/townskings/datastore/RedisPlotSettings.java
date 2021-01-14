package com.ewoudje.townskings.datastore;

import com.ewoudje.townskings.TK;
import com.ewoudje.townskings.api.town.PlotSettings;
import com.ewoudje.townskings.api.town.Town;
import com.ewoudje.townskings.util.UUIDUtil;

import java.util.UUID;

public class RedisPlotSettings implements PlotSettings {
    private final UUID uuid;

    public RedisPlotSettings(UUID uuid) {
        this.uuid = uuid;
    }

    @Override
    public String getName() {
        return TK.REDIS.hget("plot-settings:" + uuid.toString(), "name");
    }

    @Override
    public Town getTown() {
        return UUIDUtil.fromString(TK.REDIS.hget("plot-settings:" + uuid.toString(), "town"))
                .map(RedisTown::new).orElse(null);
    }

    @Override
    public UUID getUID() {
        return uuid;
    }

    @Override
    public double getPriority() {
        return Double.parseDouble(TK.REDIS.hget("plot-settings:" + uuid.toString(), "priority"));
    }

    @Override
    public void dispose() {
        TK.REDIS.del("plot-settings:" + uuid.toString());
    }

    public static RedisPlotSettings createPlotSettings(String name, double priority, Town town) {
        UUID uuid = UUID.randomUUID();

        TK.REDIS.hset("plot-settings:" + uuid.toString(), "name", name);
        TK.REDIS.hset("plot-settings:" + uuid.toString(), "priority", String.valueOf(priority));
        TK.REDIS.hset("plot-settings:" + uuid.toString(), "town", town.getUID().toString());

        return new RedisPlotSettings(uuid);
    }
}
