package com.ewoudje.townskings.api.town;

import com.ewoudje.townskings.api.world.BlockPosition;
import de.tr7zw.nbtapi.NBTCompound;
import de.tr7zw.nbtapi.NBTListCompound;

public interface Plot {

    PlotSettings getSettings();

    BlockPosition getStartPosition();

    BlockPosition getEndPosition();

    boolean isInfiniteDepth();

    void save(NBTCompound compound);
}
