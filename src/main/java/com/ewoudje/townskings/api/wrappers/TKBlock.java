package com.ewoudje.townskings.api.wrappers;

import com.ewoudje.townskings.api.block.BlockData;
import com.ewoudje.townskings.api.block.BlockType;
import de.tr7zw.nbtapi.NBTListCompound;
import org.bukkit.block.Block;

import java.util.UUID;

public class TKBlock {

    private final TKWorld world;
    private final Block block;
    private final BlockType type;
    private final BlockData data;
    private final UUID id;

    public TKBlock(TKWorld world, Block block, BlockType type, BlockData data, UUID id) {
        this.world = world;
        this.block = block;
        this.type = type;
        this.data = data;
        this.id = id;
    }

    public void save(NBTListCompound compound) {
        compound.setString("type", type.getName());
        compound.setInteger("x", block.getX());
        compound.setInteger("y", block.getY());
        compound.setInteger("z", block.getZ());
        compound.setUUID("id", id);
        if (data != null)
            data.save(compound);
    }

    public Block getBlock() {
        return block;
    }

    public BlockType getType() {
        return type;
    }

    public BlockData getData() {
        return data;
    }

    public TKWorld getWorld() {
        return world;
    }

    public void remove() {
        world.remove(this);
    }

    public UUID getId() {
        return id;
    }
}
