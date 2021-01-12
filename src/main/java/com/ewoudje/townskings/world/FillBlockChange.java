package com.ewoudje.townskings.world;

import com.comphenix.protocol.wrappers.ChunkCoordIntPair;
import com.comphenix.protocol.wrappers.ChunkPosition;
import com.ewoudje.townskings.api.world.BlockPosition;
import com.ewoudje.townskings.api.world.ChangeBlockList;
import com.ewoudje.townskings.api.world.ChunkChange;
import org.apache.logging.log4j.util.TriConsumer;
import org.bukkit.Material;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class FillBlockChange extends PerBlockChange {

    private final BlockPosition start;
    private final BlockPosition end;
    private final Material material;

    public FillBlockChange(BlockPosition start, BlockPosition end, Material material) {

        this.start = start;
        this.end = end;
            

        this.material = material;
    }


    @Override
    protected void forEach(ForEachBlock forEach) {
        int rX = end.getX() < start.getX() ? -1 : 1;
        int rY = end.getY() < start.getY() ? -1 : 1;
        int rZ = end.getZ() < start.getZ() ? -1 : 1;

        for (int x = start.getX(); x != (end.getX() + rX); x += rX) {
            for (int y = start.getY(); y != (end.getY() + rY); y += rY) {
                for (int z = start.getZ(); z != (end.getZ() + rZ); z += rZ) {
                    forEach.accept(x, y, z, material);
                }
            }
        }
    }
}
