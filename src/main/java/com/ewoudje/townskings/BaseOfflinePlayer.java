package com.ewoudje.townskings;

import com.ewoudje.townskings.api.OfflinePlayer;
import com.ewoudje.townskings.api.wrappers.TKPlayer;
import de.tr7zw.nbtapi.NBTCompound;

import java.util.Objects;
import java.util.UUID;

public class BaseOfflinePlayer implements com.ewoudje.townskings.api.OfflinePlayer {
    private final UUID id;
    private final String name;

    public BaseOfflinePlayer(UUID id, String name) {
        this.id = id;
        this.name = name;
    }

    public BaseOfflinePlayer(NBTCompound c) {
        this(c.getUUID("id"), c.getString("name"));
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public UUID getUniqueId() {
        return id;
    }

    public void save(NBTCompound c) {
        c.setUUID("id", getUniqueId());
        c.setString("name", getName());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        if (getClass() != o.getClass()) {
            if (o.getClass() == TKPlayer.class) {
                return id.equals(((TKPlayer) o).getPlayer().getUniqueId());
            } else return false;
        } else {
            OfflinePlayer that = (OfflinePlayer) o;
            return id.equals(that.getUniqueId());
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
