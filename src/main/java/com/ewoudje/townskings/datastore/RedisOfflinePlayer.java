package com.ewoudje.townskings.datastore;

import com.ewoudje.townskings.TK;
import com.ewoudje.townskings.api.OfflinePlayer;

import java.util.Objects;
import java.util.UUID;

public class RedisOfflinePlayer implements OfflinePlayer {
    private final UUID uuid;

    public RedisOfflinePlayer(UUID uuid) {
        this.uuid = uuid;
    }

    @Override
    public String getName() {
        return TK.REDIS.hget("player:" + uuid.toString(), "name");
    }

    @Override
    public UUID getUniqueId() {
        return uuid;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RedisOfflinePlayer that = (RedisOfflinePlayer) o;
        return Objects.equals(uuid, that.uuid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid);
    }
}
