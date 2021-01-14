package com.ewoudje.townskings.api.town;

import com.ewoudje.townskings.api.world.BlockPosition;
import de.tr7zw.nbtapi.NBTCompound;
import de.tr7zw.nbtapi.NBTListCompound;

import java.util.UUID;

public interface Plot {

    PlotSettings getSettings();

    BlockPosition getStartPosition();

    BlockPosition getEndPosition();

    boolean isInfiniteDepth();

    UUID getUID();

    double getPriority();

    void dispose();
}
