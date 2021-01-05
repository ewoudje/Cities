package com.ewoudje.cities.block;

import de.tr7zw.nbtapi.NBTCompound;

public interface BlockData {

    void save(NBTCompound compound);

    void load(NBTCompound compound);

}
