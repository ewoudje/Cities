package com.ewoudje.townskings.remote;

import com.ewoudje.townskings.TK;
import com.ewoudje.townskings.api.UObject;
import com.ewoudje.townskings.api.UReference;
import com.ewoudje.townskings.util.UUIDUtil;
import com.github.sikandar.faktory.FaktoryJob;
import com.github.sikandar.faktory.JobQueue;
import com.github.sikandar.faktory.JobType;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.function.IntPredicate;
import java.util.stream.Stream;

public class RemoteHelper {

    private final String name;
    private final String faktoryPrefix;
    private final String redisPrefix;
    private final HashSet<String> sub;
    private final JobQueue def;

    public RemoteHelper(String name, JobQueue def) {
        this.name = name;
        redisPrefix = name.toLowerCase() + ":";
        faktoryPrefix = "TownsKings.Repo." + name + ".";
        sub = new HashSet<>();
        this.def = def;
    }

    public String get(UUID uuid, String name) {
        return TK.REDIS.hget(redisPrefix + uuid.toString() + ":self", name);
    }

    public void set(UUID uuid, String name, String value) {
        TK.REDIS.hset(redisPrefix + uuid.toString() + ":self", name, value);
    }

    public void set(UUID uuid, String name, UObject value) {
        set(uuid, name, value.getUID().toString());
    }

    public Set<String> getSet(UUID uuid, String name) {
        sub.add(name);
        return TK.REDIS.smembers(redisPrefix + uuid.toString() + ":" + name);
    }

    public void addSet(UUID uuid, String name, String value) {
        sub.add(name);
        TK.REDIS.sadd(redisPrefix + uuid.toString() + ":" + name, value);
    }

    public void addSet(UUID uuid, String name, UObject value) {
        addSet(uuid, name, value.getUID().toString());
    }

    public void delete(UUID uuid) {
        TK.REDIS.del(redisPrefix + uuid.toString());
        sub.forEach((s) -> TK.REDIS.del(redisPrefix + uuid.toString() + ":" + s));
    }

    @Deprecated
    public void execute(UUID uuid, String method, Object... param) {
        execute(uuid, method, (i) -> false, param);
    }

    public void execute(UUID uuid, String method, IntPredicate umask, Object... param) {

        String[] sending = new String[param.length + 1];
        for (int i = 0; i < param.length; i++) {
            Object value = param[i];
            if (value instanceof UObject)
                sending[i + 1] = ((UObject) value).getUID().toString();
            else
                sending[i + 1] = value.toString();
        }
        sending[0] = uuid.toString();

        TK.FAKTORY.push(new FaktoryJob(JobType.of(faktoryPrefix + method), def, umask, (Object[]) sending));
    }

    public void rem(UUID uuid, String name) {
        TK.REDIS.hdel(redisPrefix + uuid.toString() + ":self", name);
    }

    public <T extends UReference> T get(UUID uuid, String name, Class<T> type) {
        return UUIDUtil.fromString(get(uuid, name)).map((s) -> UReference.create(s, type)).orElse(null);
    }

    public <T extends UReference> Stream<T> getSet(UUID uuid, String key, Class<T> type) {
        return getSet(uuid, key).stream().map((s) -> UReference.create(UUID.fromString(s), type));
    }

    public boolean contains(UUID uuid, String key, String value) {
        return TK.REDIS.sismember(redisPrefix + uuid + ":" + key, value);
    }

    public boolean contains(UUID uuid, String key, UObject object) {
        return contains(uuid, key, object.getUID().toString());
    }

    public void remSet(UUID uuid, String key, String s) {
        TK.REDIS.srem(redisPrefix + uuid + ":" + key, s);
    }

    public void remSet(UUID uuid, String key, UObject object) {
        remSet(uuid, key, object.getUID().toString());
    }

    public List<String> hValues(UUID uuid, String key) {
        return TK.REDIS.hvals(redisPrefix + uuid + ":" + key);
    }

    public <T extends UReference> Stream<T> hValues(UUID uuid, String key, Class<T> type) {
        return TK.REDIS.hvals(redisPrefix + uuid + ":" + key).stream().map((s) -> UReference.create(UUID.fromString(s), type));
    }

    public void hSet(UUID uuid, String key, String name, String value) {
        TK.REDIS.hset(redisPrefix + uuid + ":" + key, name, value);
    }

    public void hSet(UUID uuid, String key, String name, UObject value) {
        hSet(uuid, key, name, value.getUID().toString());
    }

    public String hGet(UUID uuid, String key, String name) {
        return TK.REDIS.hget(redisPrefix + uuid + ":" + key, name);
    }

    public <T extends UReference> T hGet(UUID uuid, String key, String name, Class<T> type) {
        return UUIDUtil.fromString(hGet(uuid, key, name)).map((s) -> UReference.create(s, type)).orElse(null);
    }

    public void hRem(UUID uuid, String key, String name) {
        TK.REDIS.hdel(redisPrefix + uuid + ":" + key, name);
    }
}
