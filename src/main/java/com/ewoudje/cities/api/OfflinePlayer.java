package com.ewoudje.cities.api;

import com.ewoudje.cities.BaseOfflinePlayer;
import com.ewoudje.cities.NonePlayer;
import de.tr7zw.nbtapi.NBTCompound;
import org.bukkit.entity.AnimalTamer;
import org.bukkit.entity.Player;

public interface OfflinePlayer extends AnimalTamer {

    void save(NBTCompound c);

    static OfflinePlayer fromPlayer(Player p) {
        return new BaseOfflinePlayer(p.getUniqueId(), p.getName());
    }

    static OfflinePlayer fromCompound(NBTCompound c) {
        if (NonePlayer.is(c.getUUID("id"))) return new NonePlayer();
        return new BaseOfflinePlayer(c);
    }
}
