package com.ewoudje.townskings.api.wrappers;

import com.ewoudje.townskings.api.town.Plot;
import com.ewoudje.townskings.api.world.ChunkPosition;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class TKChunk {

    private final ChunkPosition pos;
    private final List<PlotEntry> plots = new ArrayList<>();

    public TKChunk(ChunkPosition pos) {
        this.pos = pos;
    }

    public PlotEntry addPlot(Plot plot, int x, int y, int z, int xS, int yS, int zS) {
        PlotEntry result = new TKChunk.PlotEntry(plot, x, y, z, xS, yS, zS);
        plots.add(result);
        return result;
    }

    public List<PlotEntry> getEntries() {
        return plots;
    }

    public static class PlotEntry {
        private final Plot plot;
        private final int x, y, z;
        private final int xE, yE, zE;

        public PlotEntry(Plot plot, int x, int y, int z, int xS, int yS, int zS) {
            this.plot = plot;
            this.x = x;
            this.y = y;
            this.z = z;
            this.xE = x + xS;
            this.yE = y + yS;
            this.zE = z + zS;
        }

        public Plot getPlot() {
            return plot;
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }

        public int getZ() {
            return z;
        }

        public int getXE() {
            return xE;
        }

        public int getYE() {
            return yE;
        }

        public int getZE() {
            return zE;
        }
    }
}
