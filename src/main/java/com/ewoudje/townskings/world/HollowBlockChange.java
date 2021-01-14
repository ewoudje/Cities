package com.ewoudje.townskings.world;

import com.ewoudje.townskings.api.world.BlockPosition;
import com.ewoudje.townskings.api.wrappers.TKWorld;
import org.bukkit.Material;

public class HollowBlockChange extends PerBlockChange {

    private final BlockPosition start;
    private final BlockPosition end;
    private final Material material;
    private final TKWorld world;
    private final boolean floor;

    public HollowBlockChange(BlockPosition start, BlockPosition end, Material material, TKWorld world, boolean floor) {

        this.start = start;
        this.end = end;


        this.material = material;
        this.world = world;
        this.floor = floor;
    }

    @Override
    protected void forEach(ForEachBlock forEach) {
        int rX = end.getX() < start.getX() ? -1 : 1;
        int rY = end.getY() < start.getY() ? -1 : 1;
        int rZ = end.getZ() < start.getZ() ? -1 : 1;

        if (floor) {
            //BOTTOM
            for (int x = start.getX(); x != (end.getX() + rX); x += rX) {
                for (int z = start.getZ(); z != (end.getZ() + rZ); z += rZ) {
                    forEach.accept(x, start.getY(), z, material);
                }
            }

            //TOP
            for (int x = start.getX(); x != (end.getX() + rX); x += rX) {
                for (int z = start.getZ(); z != (end.getZ() + rZ); z += rZ) {
                    forEach.accept(x, end.getY(), z, material);
                }
            }
        }

        //LEFT
        for (int x = start.getX(); x != (end.getX() + rX); x += rX) {
            for (int y = start.getY(); y != (end.getY() + rY); y += rY) {
                forEach.accept(x, y, start.getZ(), material);
            }
        }

        //RIGHT
        for (int x = start.getX(); x != (end.getX() + rX); x += rX) {
            for (int y = start.getY(); y != (end.getY() + rY); y += rY) {
                forEach.accept(x, y, end.getZ(), material);
            }
        }

        //FRONT
        for (int z = start.getZ(); z != (end.getZ() + rZ); z += rZ) {
            for (int y = start.getY(); y != (end.getY() + rY); y += rY) {
                forEach.accept(start.getX(), y, z, material);
            }
        }

        //BACK
        for (int z = start.getZ(); z != (end.getZ() + rZ); z += rZ) {
            for (int y = start.getY(); y != (end.getY() + rY); y += rY) {
                forEach.accept(end.getX(), y, z, material);
            }
        }
    }
}
