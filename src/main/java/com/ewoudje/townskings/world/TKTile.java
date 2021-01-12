package com.ewoudje.townskings.world;

import com.ewoudje.townskings.api.town.Plot;
import com.ewoudje.townskings.api.world.ChunkPosition;
import com.ewoudje.townskings.api.world.Tile;
import com.ewoudje.townskings.api.world.TilePosition;
import com.ewoudje.townskings.api.wrappers.TKChunk;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class TKTile implements Tile {

    private final TilePosition pos;
    private final TKChunk[] chunks = new TKChunk[16];
    private final List<TKChunk.PlotEntry> entries = new ArrayList<>();

    public TKTile(TilePosition pos) {
        this.pos = pos;

    }

    @Override
    public void addPlot(Plot plot, int x, int y, int z, int xS, int yS, int zS) {
        int i = ((z >> 4 - pos.getZ()) * Tile.CHUNKS_SIZE) + (x >> 4 - pos.getX());
        int i2 = ((zS >> 4 - pos.getZ()) * Tile.CHUNKS_SIZE) + (xS >> 4 - pos.getX());

        if (i != i2) {
            //TODO fix plot over multiple chunks
        } else {
            entries.add(chunks[i].addPlot(plot, x, y, z, xS, yS, zS));
        }
    }

    @Override
    public Plot getPlotAt(int x, int y, int z) {
        for (TKChunk.PlotEntry p : entries) {
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
        return entries.stream().map(TKChunk.PlotEntry::getPlot).collect(Collectors.toList());
    }

    @Override
    public TilePosition getPosition() {
        return pos;
    }

    @Override
    public TKChunk getChunk(ChunkPosition position) {
        int x = position.getX() - (pos.getX() << 2);
        int y = position.getZ() - (pos.getZ() << 2);
        return Objects.requireNonNullElseGet(chunks[y * Tile.CHUNKS_SIZE + x],
                () -> chunks[y * Tile.CHUNKS_SIZE + x] = new TKChunk(position));
    }

}
