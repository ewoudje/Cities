package com.ewoudje.townskings.world;

import com.ewoudje.townskings.api.town.Plot;
import com.ewoudje.townskings.api.world.BlockPosition;
import com.ewoudje.townskings.api.world.ChunkPosition;
import com.ewoudje.townskings.api.world.Tile;
import com.ewoudje.townskings.api.world.TilePosition;
import com.ewoudje.townskings.api.wrappers.TKWorld;

import java.util.Map;

public class Plotter {

    /*
    public static boolean claim(Plot plot, TKWorld world, final Map<TilePosition, Tile> tiles) {
        BlockPosition start = plot.getStartPosition();
        BlockPosition end = plot.getEndPosition();

        if (world.getPlotAt(end) != null)
            return false;

        if (world.getPlotAt(start) != null)
            return false;

        boolean bX, bY, bZ;
        bX = start.getX() > end.getX();
        bY = start.getY() > end.getY();
        bZ = start.getZ() > end.getZ();

        ChunkPosition chunkDistance = end.getChunkPos().minus(start.getChunkPos());
        if (chunkDistance.getX() == 0 && chunkDistance.getZ() == 0)
            world.getTile(plot.getStartPosition().getTilePos()).addPlot(plot,
                    //-1 is important for making include the edges
                    (bX ? end.getX() : start.getX()) - 1,
                    (bY ? end.getY() : start.getY()) - 1,
                    (bZ ? end.getZ() : start.getZ()) - 1,
                    //+2 is important for making include the edges
                    Math.abs(start.getX() - end.getX()) + 2,
                    Math.abs(start.getY() - end.getY()) + 2,
                    Math.abs(start.getZ() - end.getZ()) + 2);
        else
            return false; //TODO

        return true;
    }
    */

}
