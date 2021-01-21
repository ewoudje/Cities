package com.ewoudje.townskings.util;

import com.ewoudje.townskings.api.town.Plot;
import com.ewoudje.townskings.api.world.BlockPosition;
import com.ewoudje.townskings.api.world.ChunkPosition;
import com.ewoudje.townskings.remote.RemoteChunk;

public class PlotApplier {

    public static void applyPlot(Plot plot) {
        BlockPosition start = plot.getStartPosition();
        BlockPosition end = plot.getEndPosition();
        for (int x = start.getX() >> 4; x <= (end.getX() >> 4); x++) {
            for (int z = start.getZ() >> 4; z <= (end.getZ() >> 4); z++) {
                new RemoteChunk(new ChunkPosition(x, z)).addPlot(plot);
            }
        }
    }

    public static void removePlot(Plot plot) {
        BlockPosition start = plot.getStartPosition();
        BlockPosition end = plot.getEndPosition();
        for (int x = start.getX() >> 4; x <= (end.getX() >> 4); x++) {
            for (int z = start.getZ() >> 4; z <= (end.getZ() >> 4); z++) {
                new RemoteChunk(new ChunkPosition(x, z)).removePlot(plot);
            }
        }
    }
}
