package com.ewoudje.townskings.town.plot;

import com.ewoudje.townskings.api.town.Plot;
import com.ewoudje.townskings.api.town.PlotSettings;
import com.ewoudje.townskings.api.world.BlockPosition;

public class TKPlot implements Plot {
    private final BlockPosition start, end;
    private final boolean depth;
    private final PlotSettings settings;

    public TKPlot(BlockPosition start, BlockPosition end, boolean depth, PlotSettings settings) {
        this.start = start;
        this.end = end;
        this.depth = depth;
        this.settings = settings;
    }

    @Override
    public PlotSettings getSettings() {
        return settings;
    }

    @Override
    public BlockPosition getStartPosition() {
        return start;
    }

    @Override
    public BlockPosition getEndPosition() {
        return end;
    }

    @Override
    public boolean isInfiniteDepth() {
        return depth;
    }
}
