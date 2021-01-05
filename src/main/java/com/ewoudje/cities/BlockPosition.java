package com.ewoudje.cities;

import de.tr7zw.nbtapi.NBTCompound;
import org.bukkit.block.Block;

import java.util.Objects;

public class BlockPosition {

    private final int x, y, z;

    public BlockPosition(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }


    public BlockPosition(NBTCompound c) {
        this(c.getInteger("x"), c.getInteger("y"), c.getInteger("z"));
    }

    public BlockPosition(Block b) {
        this(b.getX(), b.getY(), b.getZ());
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BlockPosition that = (BlockPosition) o;
        return getX() == that.getX() && getY() == that.getY() && getZ() == that.getZ();
    }

    @Override
    public int hashCode() {
        return getX() | (getY() << 10) | (getZ() << 20);
    }
}
