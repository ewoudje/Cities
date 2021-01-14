package com.ewoudje.townskings.base;

import com.ewoudje.townskings.api.block.BlockType;
import com.ewoudje.townskings.api.wrappers.TKBlock;
import org.bukkit.Material;
import org.bukkit.block.Block;

import java.util.UUID;

public class BaseTKBlock implements TKBlock {
    private final Block block;

    public BaseTKBlock(Block block) {
        this.block = block;
    }

    @Override
    public Block getBlock() {
        return block;
    }

    @Override
    public BlockType getType() {
        return null;
    }

    @Override
    public void destroy() {
        block.setType(Material.AIR);
    }

    @Override
    public UUID getUID() {
        return null;
    }

    @Override
    public int getX() {
        return block.getX();
    }

    @Override
    public int getY() {
        return block.getY();
    }

    @Override
    public int getZ() {
        return block.getZ();
    }
}
