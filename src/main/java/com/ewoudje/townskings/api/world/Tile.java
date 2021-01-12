package com.ewoudje.townskings.api.world;

import com.ewoudje.townskings.api.town.Plot;
import com.ewoudje.townskings.api.wrappers.TKChunk;

import java.util.List;

public interface Tile {

    static final int CHUNKS_SIZE = 4;

    static int fromChunkPos(int chunk) {
        return chunk >> 2;
    }

    static int fromBlockPos(int block) {
        return block >> 6;
    }

    void addPlot(Plot plot, int x, int y, int z, int xS, int yS, int zS);

    Plot getPlotAt(int x, int y, int z);

    List<Plot> getPlots();

    TilePosition getPosition();

    TKChunk getChunk(ChunkPosition position);
}
