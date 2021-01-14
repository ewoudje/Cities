package com.ewoudje.townskings.api.world;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.BlockPosition;
import com.comphenix.protocol.wrappers.WrappedBlockData;
import com.comphenix.protocol.wrappers.nbt.NbtFactory;
import com.ewoudje.townskings.api.wrappers.TKWorld;
import com.ewoudje.townskings.version.v1_16_R3.ChunkUtil;
import org.bukkit.Chunk;

public class ChunkChange {

    private final BlockPosition chunk;
    private final WrappedBlockData[] blocks;
    private final short[] positions;

    public ChunkChange(BlockPosition chunk, WrappedBlockData[] blocks, short[] positions) {
        this.chunk = chunk;
        this.blocks = blocks;
        this.positions = positions;
    }

    public PacketContainer makeMultiPacket() {
        PacketContainer container = new PacketContainer(PacketType.Play.Server.MULTI_BLOCK_CHANGE);

        //Thx https://www.spigotmc.org/threads/new-1-16-2-multiblockchange-guide-protocollib.461832/

        container.getBlockDataArrays().write(0, blocks);
        container.getSectionPositions().write(0, chunk);
        container.getShortArrays().write(0, positions);

        return container;
    }

    public PacketContainer makeChunkPacket(TKWorld world) {
        PacketContainer container = ChunkUtil.chunkMake(getChunk(world), 1 << chunk.getY());
        container.getIntegers().write(0, chunk.getX());
        container.getIntegers().write(1, chunk.getZ());
        container.getBooleans().write(0, false);
        container.getIntegers().write(2, 1 << chunk.getY());
        container.getNbtModifier().write(0, NbtFactory.ofCompound("MOTION_BLOCKING")
                .put("MOTION_BLOCKING", NbtFactory.ofList("MOTION_BLOCKING")));
        container.getByteArrays().write(0, ChunkUtil.modifySection(container.getByteArrays().read(0),
                chunk.getY(), this));

        return container;
    }

    public Chunk getChunk(TKWorld world) {
        return world.getWorld().getChunkAt(this.chunk.getX(), this.chunk.getZ());
    }

    public ChangesList getChanges() {
        return new ChangesList(this);
    }

    public static class ChangesList {
        private int i = 0;
        private ChunkChange chunkChange;

        public ChangesList(ChunkChange chunkChange) {
            this.chunkChange = chunkChange;
        }

        public boolean next() {
            i++;
            return i > chunkChange.blocks.length;
        }


        public BlockPosition absolute() {
            int x = chunkChange.chunk.getX() << 4 + chunkChange.positions[i] & 0x00F;
            int y = chunkChange.chunk.getY() << 4 + (chunkChange.positions[i] & 0x0F0 >> 4);
            int z = chunkChange.chunk.getZ() << 4 + (chunkChange.positions[i] & 0xF00 >> 8);
            return new BlockPosition(x, y, z);
        }

        public WrappedBlockData blockData() {
            return chunkChange.blocks[i];
        }

        public BlockPosition relative() {
            int x = chunkChange.positions[i] & 0x00F;
            int y = chunkChange.positions[i] & 0x0F0 >> 4;
            int z = chunkChange.positions[i] & 0xF00 >> 8;
            return new BlockPosition(x, y, z);
        }
    }
}
