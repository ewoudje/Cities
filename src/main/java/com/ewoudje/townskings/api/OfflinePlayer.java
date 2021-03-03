package com.ewoudje.townskings.api;

import com.ewoudje.townskings.NonePlayer;
import com.ewoudje.townskings.api.wrappers.TKPlayer;
import com.ewoudje.townskings.remote.RemoteOfflinePlayer;

import java.util.Optional;
import java.util.UUID;

public interface OfflinePlayer extends UObject {

    static OfflinePlayer getFromUUID(UUID uuid) {
        if (NonePlayer.is(uuid)) return new NonePlayer();

        return new RemoteOfflinePlayer(uuid);
    }

    String getName();

    boolean isOnline();

    Optional<TKPlayer> getOnline();
}
