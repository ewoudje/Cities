package com.ewoudje.townskings.api.block;

import de.tr7zw.nbtapi.NBTCompound;

public interface BlockData {

    void save(NBTCompound compound);

    void load(NBTCompound compound);

}
