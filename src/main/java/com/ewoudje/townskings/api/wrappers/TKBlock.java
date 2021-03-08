package com.ewoudje.townskings.api.wrappers;

import com.ewoudje.townskings.TK;
import com.ewoudje.townskings.api.UObject;
import com.ewoudje.townskings.api.block.BlockType;
import com.ewoudje.townskings.api.world.BlockPosition;
import com.ewoudje.townskings.base.BaseTKBlock;
import com.ewoudje.townskings.remote.RemoteBlock;
import org.bukkit.block.Block;

import java.util.UUID;

public interface TKBlock extends UObject {

    static TKBlock wrap(Block block) {
        String s = TK.REDIS.get("pos:" + block.getX() + ":" + block.getY() + ":" + block.getZ());
        if (s != null) {
            return new RemoteBlock(UUID.fromString(s));
        } else {
            return new BaseTKBlock(block);
        }
    }

    Block getBlock();

    BlockType getType();

    void destroy();

    int getX();

    int getY();

    int getZ();

    default BlockPosition getPosition() {
        return new BlockPosition(getX(), getY(), getZ());
    }
}
