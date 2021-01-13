package com.ewoudje.townskings.world;

import com.ewoudje.townskings.api.town.Plot;
import com.ewoudje.townskings.api.world.ChunkPosition;
import com.ewoudje.townskings.api.world.Tile;
import com.ewoudje.townskings.api.world.TilePosition;
import com.ewoudje.townskings.api.wrappers.TKChunk;
import com.ewoudje.townskings.api.wrappers.TKWorld;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.stream.Collectors;

public class DynamicTKTile implements Tile {

    private ChunkPosition pos;
    private final TKChunk[] chunks = new TKChunk[16];
    private final PlotEntryIterator plots = new PlotEntryIterator();

    public void setPos(@Nonnull ChunkPosition pos, @Nonnull TKWorld world) {
        if (pos.equals(this.pos)) return;

        this.pos = pos;
        update(world);
    }

    private void update(TKWorld world) {
        for (int y = 0; y < 4; y++) {
            for (int x = 0; x < 4; x++) {
                chunks[y * Tile.CHUNKS_SIZE + x] =
                        world.getChunk(new ChunkPosition(pos.getX() + x, pos.getZ() + y));
            }
        }

        plots.update();
    }

    @Override
    public void addPlot(Plot plot, int x, int y, int z, int xS, int yS, int zS) {
        throw new NullPointerException();
    }

    @Override
    public Plot getPlotAt(int x, int y, int z) {
        plots.reset();
        while (plots.hasNext()) {
            TKChunk.PlotEntry p = plots.next();
            if (x > p.getX() && x < p.getXE() &&
                    z > p.getZ() && z < p.getZE()) {

                if (p.getPlot().isInfiniteDepth() || (y > p.getY() && y < p.getYE())) {
                    return p.getPlot();
                }
            }
        }

        return null;
    }

    @Override
    public List<Plot> getPlots() {
        return plots.getList().stream().map(TKChunk.PlotEntry::getPlot).collect(Collectors.toList());
    }


    //THIS HASN'T A TILE POSITION
    @Override
    public TilePosition getPosition() {
        return null;
    }

    @Override
    public TKChunk getChunk(ChunkPosition position) {
        int x = position.getX() - pos.getX();
        int y = position.getZ() - pos.getZ();
        return chunks[y * Tile.CHUNKS_SIZE + x];
    }

    private class PlotEntryIterator implements Iterator<TKChunk.PlotEntry> {
        private List<List<TKChunk.PlotEntry>> list;
        private int i;
        private int i2;

        public void update() {
            list = Arrays.stream(chunks)
                    .map(TKChunk::getEntries)
                    .collect(Collectors.toList());
        }

        @Override
        public boolean hasNext() {
            return i < (list.size() - 1) && i2 < (list.get(list.size() - 1).size() - 1);
        }

        @Override
        public TKChunk.PlotEntry next() {
            if (i2 == list.get(i).size() - 1) {
                i++;
                i2 = 0;
            }
            return list.get(i).get(i2++);
        }

        public List<TKChunk.PlotEntry> getList() {
            return list.stream().flatMap(Collection::stream).collect(Collectors.toList());
        }

        public void reset() {
            i = 0;
            i2 = 0;
        }
    }
}
