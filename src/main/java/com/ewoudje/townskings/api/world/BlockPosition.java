package com.ewoudje.townskings.api.world;

import de.tr7zw.nbtapi.NBTCompound;
import org.bukkit.Location;
import org.bukkit.block.Block;

public class BlockPosition {

    protected int x, y, z;

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

    public BlockPosition(Location l) {
        this((int) l.getX(), (int) l.getY(), (int) l.getZ());
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

    public short intoChunkPos() {
        return (short) ((x & 0xF) << 8 | (z & 0xF) << 4 | (y & 0xF));
    }

    public TilePosition getTilePos() {
        return new TilePosition(Tile.fromBlockPos(x), Tile.fromBlockPos(z));
    }

    public ChunkPosition getChunkPos() {
        return new ChunkPosition(x >> 4, z >> 4);
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

    @Override
    public String toString() {
        return "BlockPosition{" +
                "x=" + x +
                ", y=" + y +
                ", z=" + z +
                '}';
    }
}
