package com.ewoudje.townskings.api.world;

import de.tr7zw.nbtapi.NBTCompound;
import org.bukkit.Chunk;

import java.util.Objects;

public class ChunkPosition {

    private int x, z;

    public ChunkPosition(int x, int z) {
        this.x = x;
        this.z = z;
    }

    public ChunkPosition(Chunk c) {
        this(c.getX(), c.getZ());
    }

    public ChunkPosition(NBTCompound c) {
        this(c.getInteger("x"),  c.getInteger("z"));
    }

    public static ChunkPosition fromBlockPos(BlockPosition position) {
        return new ChunkPosition(position.getX() >> 4, position.getZ() >> 4);
    }

    public static ChunkPosition fromTilePos(BlockPosition position) {
        return new ChunkPosition(position.getX() << 2, position.getZ() << 2);
    }

    public int getX() {
        return x;
    }

    public int getZ() {
        return z;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChunkPosition that = (ChunkPosition) o;
        return getX() == that.getX() && getZ() == that.getZ();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getX(), getZ());
    }

    public ChunkPosition minus(ChunkPosition chunkPos) {
        return new ChunkPosition(getX() - chunkPos.getX(), getZ() - chunkPos.getZ());
    }
}
