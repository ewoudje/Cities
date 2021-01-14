package com.ewoudje.townskings.api.wrappers;

import com.ewoudje.townskings.TK;
import com.ewoudje.townskings.api.block.BlockType;
import com.ewoudje.townskings.base.BaseTKBlock;
import com.ewoudje.townskings.datastore.RedisBlock;
import org.bukkit.block.Block;

import java.util.UUID;

public interface TKBlock {

    static TKBlock wrap(Block block) {
        String s = TK.REDIS.get("pos:" + block.getX() + ":" + block.getY() + ":" + block.getZ());
        if (s != null) {
            return new RedisBlock(UUID.fromString(s));
        } else {
            return new BaseTKBlock(block);
        }
    }

    Block getBlock();

    BlockType getType();

    void destroy();

    UUID getUID();

    int getX();

    int getY();

    int getZ();
}
