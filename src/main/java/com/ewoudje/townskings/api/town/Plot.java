package com.ewoudje.townskings.api.town;

import com.ewoudje.townskings.api.UObject;
import com.ewoudje.townskings.api.world.BlockPosition;

public interface Plot extends UObject {

    PlotCategory getSettings();

    BlockPosition getStartPosition();

    BlockPosition getEndPosition();

    boolean isInfiniteDepth();

    double getPriority();

    void dispose();
}
