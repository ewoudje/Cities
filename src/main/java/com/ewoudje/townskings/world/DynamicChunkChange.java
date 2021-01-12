package com.ewoudje.townskings.world;

import com.comphenix.protocol.wrappers.ChunkCoordIntPair;
import com.comphenix.protocol.wrappers.ChunkPosition;
import com.comphenix.protocol.wrappers.MultiBlockChangeInfo;
import com.comphenix.protocol.wrappers.WrappedBlockData;
import com.ewoudje.townskings.api.world.BlockPosition;
import com.ewoudje.townskings.api.world.ChunkChange;
import org.bukkit.Material;

import java.util.HashMap;
import java.util.Map;

public class DynamicChunkChange {

    private final BlockPosition chunk;
    private HashMap<BlockPosition, Material> map = new HashMap<>();

    public DynamicChunkChange(BlockPosition chunk) {
        this.chunk = chunk;
    }

    public ChunkChange build() {

        short[] positions = new short[map.size()];
        WrappedBlockData[] data = new WrappedBlockData[map.size()];

        int i = 0;

        for (Map.Entry<BlockPosition, Material> entry : map.entrySet()) {
            positions[i] = entry.getKey().intoChunkPos();
            data[i] = WrappedBlockData.createData(entry.getValue());
            i++;
        }

        return new ChunkChange(
                new com.comphenix.protocol.wrappers.BlockPosition(chunk.getX(), chunk.getY(), chunk.getZ()),
                data, positions);
    }

    public void put(int x, int y, int z, Material material) {
        map.put(new BlockPosition(x, y, z), material);
    }
}
