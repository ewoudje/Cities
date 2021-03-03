package com.ewoudje.townskings;

import com.ewoudje.townskings.api.OfflinePlayer;
import com.ewoudje.townskings.api.wrappers.TKPlayer;

import javax.annotation.Nonnull;
import java.util.Optional;
import java.util.UUID;

public class NonePlayer implements OfflinePlayer {
    private static final UUID uuid = new UUID(0, 0);
    private static final String name = "None";

    public static boolean is(UUID id) {
        return id.equals(uuid);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean isOnline() {
        return false;
    }

    @Override
    public Optional<TKPlayer> getOnline() {
        return Optional.empty();
    }

    @Nonnull
    @Override
    public UUID getUID() {
        return uuid;
    }
}
