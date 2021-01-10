package com.ewoudje.townskings.town.plot;

import com.ewoudje.townskings.api.town.Plot;
import com.ewoudje.townskings.api.town.PlotSettings;

public class TKPlot implements Plot {

    @Override
    public PlotSettings getSettings() {
        return null;
    }

    @Override
    public boolean isInfiniteDepth() {
        return false;
    }
}
