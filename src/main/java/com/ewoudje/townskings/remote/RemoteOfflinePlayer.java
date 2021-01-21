package com.ewoudje.townskings.remote;

import com.ewoudje.townskings.api.OfflinePlayer;
import com.ewoudje.townskings.api.UReference;

import java.util.Objects;
import java.util.UUID;

public class RemoteOfflinePlayer implements OfflinePlayer, UReference {
    public final static RemoteHelper R = RemotePlayer.R;
    private final UUID uuid;

    public RemoteOfflinePlayer(UUID uuid) {
        this.uuid = uuid;
    }

    @Override
    public String getName() {
        return R.get(uuid, "name");
    }

    @Override
    public UUID getUID() {
        return uuid;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RemoteOfflinePlayer that = (RemoteOfflinePlayer) o;
        return Objects.equals(uuid, that.uuid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid);
    }
}
