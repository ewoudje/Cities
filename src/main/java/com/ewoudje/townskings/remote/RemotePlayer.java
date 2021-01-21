package com.ewoudje.townskings.remote;

import com.ewoudje.townskings.api.OfflinePlayer;
import com.ewoudje.townskings.api.UReference;
import com.ewoudje.townskings.api.town.Town;
import com.ewoudje.townskings.api.wrappers.TKPlayer;
import com.ewoudje.townskings.api.wrappers.TKWorld;
import com.ewoudje.townskings.remote.faktory.FaktoryPriority;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;
import java.util.UUID;

public class RemotePlayer implements TKPlayer, UReference {
    public final static RemoteHelper R = new RemoteHelper("Player", FaktoryPriority.MC);

    private final Player player;
    private final TKWorld world;

    private RemotePlayer(Player player, TKWorld world) {
        this.player = player;
        this.world = world;
    }

    public static TKPlayer read(Player pla) {
        if (pla == null) return null;
        return new RemotePlayer(pla, new RemoteWorld(pla.getWorld()));
    }

    public void setTown(Town town) {
        if (town == null) {
            R.rem(getUID(), "town");
        } else {
            R.set(getUID(), "town", town.getUID().toString());
        }
    }

    @Nullable
    public Town getTown() {
        return R.get(getUID(), "town", RemoteTown.class);
    }

    @Nonnull
    public Player getPlayer() {
        return player;
    }

    @Nonnull
    public TKWorld getWorld() {
        return world;
    }

    @Nonnull
    public OfflinePlayer getOfflinePlayer() {
        return new RemoteOfflinePlayer(player.getUniqueId());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;

        OfflinePlayer that;

        if (getClass() == o.getClass()) {
            that = ((com.ewoudje.townskings.api.wrappers.TKPlayer) o).getOfflinePlayer();
        } else if (OfflinePlayer.class == o.getClass()) {
            that = (OfflinePlayer) o;
        } else return false;

        return Objects.equals(getOfflinePlayer(), that);
    }

    @Override
    public int hashCode() {
        return Objects.hash(player);
    }

    public UUID getUID() {
        return player.getUniqueId();
    }

    @Override
    public boolean is(OfflinePlayer player) {
        return player.getUID().equals(this.getUID());
    }

    public static void updateUsername(UUID uuid, String name) {
        R.set(uuid, "name", name);
    }
}
