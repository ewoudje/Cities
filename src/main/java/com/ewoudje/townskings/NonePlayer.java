package com.ewoudje.townskings;

import com.ewoudje.townskings.api.OfflinePlayer;
import de.tr7zw.nbtapi.NBTCompound;

import javax.annotation.Nonnull;
import java.util.UUID;

public class NonePlayer implements OfflinePlayer {
    private static final UUID uuid = new UUID(0, 0);
    private static final String name = "None";

    public static boolean is(UUID id) {
        return id.equals(uuid);
    }

    @Override
    public void save(NBTCompound c) {
        c.setUUID("id", uuid);
        c.setString("name", name);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    @Nonnull
    public UUID getUniqueId() {
        return uuid;
    }
}
