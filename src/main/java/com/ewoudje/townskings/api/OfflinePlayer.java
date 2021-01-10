package com.ewoudje.townskings.api;

import com.ewoudje.townskings.BaseOfflinePlayer;
import com.ewoudje.townskings.NonePlayer;
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
