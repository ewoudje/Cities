package com.ewoudje.townskings.api;

import com.ewoudje.townskings.NonePlayer;
import com.ewoudje.townskings.datastore.RedisOfflinePlayer;
import org.bukkit.entity.AnimalTamer;

import java.util.UUID;

public interface OfflinePlayer extends AnimalTamer {

    static OfflinePlayer getFromUUID(UUID uuid) {
        if (NonePlayer.is(uuid)) return new NonePlayer();

        return new RedisOfflinePlayer(uuid);
    }

}
