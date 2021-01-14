package com.ewoudje.townskings.api.wrappers;

import com.ewoudje.townskings.api.town.Plot;

import java.util.List;

public interface TKChunk {

    void addPlot(Plot plot);

    List<Plot> getPlots();
}
