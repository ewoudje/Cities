package com.ewoudje.cities;

import com.ewoudje.cities.block.BlockData;
import com.ewoudje.cities.block.BlockType;
import de.tr7zw.nbtapi.NBTListCompound;
import org.bukkit.block.Block;

import java.util.UUID;

public class CityBlock {

    private final CityWorld world;
    private final Block block;
    private final BlockType type;
    private final BlockData data;
    private final UUID id;

    public CityBlock(CityWorld world, Block block, BlockType type, BlockData data, UUID id) {
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

    public CityWorld getWorld() {
        return world;
    }

    public void remove() {
        world.remove(this);
    }

    public UUID getId() {
        return id;
    }
}
