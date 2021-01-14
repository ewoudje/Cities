package com.ewoudje.townskings.datastore;

import com.ewoudje.townskings.TK;
import com.ewoudje.townskings.api.OfflinePlayer;

import java.util.UUID;

public class RedisOfflinePlayer implements OfflinePlayer {
    private final UUID uuid;

    public RedisOfflinePlayer(UUID uuid) {
        this.uuid = uuid;
    }

    @Override
    public String getName() {
        return TK.REDIS.get("players:" + uuid.toString() + ":name");
    } //TODO name is not set? should we do this?

    @Override
    public UUID getUniqueId() {
        return uuid;
    }
}
