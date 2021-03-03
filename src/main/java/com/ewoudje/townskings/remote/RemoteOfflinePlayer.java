package com.ewoudje.townskings.remote;

import com.ewoudje.townskings.api.OfflinePlayer;
import com.ewoudje.townskings.api.UReference;
import com.ewoudje.townskings.api.wrappers.TKPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Objects;
import java.util.Optional;
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
    public boolean isOnline() {
        return Bukkit.getPlayer(uuid) != null;
    }

    @Override
    public Optional<TKPlayer> getOnline()  {
        Player player = Bukkit.getPlayer(this.getUID());

        if (player == null) return Optional.empty();

        return Optional.of(TKPlayer.wrap(player));
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
