package com.ewoudje.townskings.api.world;

import de.tr7zw.nbtapi.NBTCompound;

import java.util.Objects;

public class TilePosition {

    private int x, z;

    public TilePosition(int x, int z) {
        this.x = x;
        this.z = z;
    }

    public TilePosition(NBTCompound c) {
        this(c.getInteger("x"),  c.getInteger("z"));
    }

    public static TilePosition fromBlockPos(BlockPosition position) {
        return new TilePosition(Tile.fromBlockPos(position.getX()), Tile.fromBlockPos(position.getZ()));
    }

    public int getX() {
        return x;
    }

    public int getZ() {
        return z;
    }

    public void save(NBTCompound c) {
        c.setInteger("x", x);
        c.setInteger("z", z);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TilePosition that = (TilePosition) o;
        return getX() == that.getX() && getZ() == that.getZ();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getX(), getZ());
    }

    public ChunkPosition toChunkPos() {
        return new ChunkPosition(x << 2, z << 2);
    }
}
