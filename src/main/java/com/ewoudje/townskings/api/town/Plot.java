package com.ewoudje.townskings.api.town;

import com.ewoudje.townskings.api.world.BlockPosition;

public interface Plot {

    PlotSettings getSettings();

    BlockPosition getStartPosition();

    BlockPosition getEndPosition();

    boolean isInfiniteDepth();

}
