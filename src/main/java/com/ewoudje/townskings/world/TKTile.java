package com.ewoudje.townskings.world;

import com.ewoudje.townskings.api.town.Plot;
import com.ewoudje.townskings.api.world.Tile;
import com.ewoudje.townskings.api.world.TilePosition;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TKTile implements Tile {

    private final TilePosition pos;
    private final List<PlotEntry> plots = new ArrayList<>();

    public TKTile(TilePosition pos) {
        this.pos = pos;
    }

    @Override
    public void addPlot(Plot plot, int x, int y, int z, int xS, int yS, int zS) {
        plots.add(new PlotEntry(plot, x, y, z, xS, yS, zS));
    }

    @Override
    public Plot getPlotAt(int x, int y, int z) {
        for (PlotEntry p : plots) {
            if (x > p.x && x < p.xE &&
                z > p.z && z < p.zE) {

                if (p.plot.isInfiniteDepth() || (y > p.y && y < p.yE)) {
                    return p.plot;
                }
            }
        }

        return null;
    }

    @Override
    public List<Plot> getPlots() {
        return plots.stream().map((p) -> p.plot).collect(Collectors.toList());
    }

    @Override
    public TilePosition getPosition() {
        return pos;
    }

    private static class PlotEntry {
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
    }
}
