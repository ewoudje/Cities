package com.ewoudje.townskings.api.wrappers;

import com.ewoudje.townskings.api.OfflinePlayer;
import com.ewoudje.townskings.api.UObject;
import com.ewoudje.townskings.api.town.Town;
import com.ewoudje.townskings.remote.RemotePlayer;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface TKPlayer extends UObject {

    static TKPlayer wrap(Player player) {
        return RemotePlayer.read(player); //TODO hmm?
    }

    @Nullable
    Town getTown();

    @Nonnull
    Player getPlayer();

    @Nonnull
    TKWorld getWorld();

    @Nonnull
    OfflinePlayer getOfflinePlayer();

    boolean is(OfflinePlayer player);
}
