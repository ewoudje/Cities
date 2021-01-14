package com.ewoudje.townskings.datastore;

import com.ewoudje.townskings.TK;
import com.ewoudje.townskings.api.block.BlockType;
import com.ewoudje.townskings.api.wrappers.TKBlock;
import com.ewoudje.townskings.block.Blocks;
import com.ewoudje.townskings.block.FoundingBlock;
import io.sentry.Breadcrumb;
import io.sentry.Sentry;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;

import java.util.UUID;

public class RedisBlock implements TKBlock {
    private final UUID uuid;
    private final Block block;

    public RedisBlock(UUID uuid) {
        this.uuid = uuid;
        block = Bukkit.getWorld(UUID.fromString(TK.REDIS.hget("block:" + uuid.toString(), "world")))
                .getBlockAt(
                        Integer.parseInt(TK.REDIS.hget("block:" + uuid.toString(), "x")),
                        Integer.parseInt(TK.REDIS.hget("block:" + uuid.toString(), "y")),
                        Integer.parseInt(TK.REDIS.hget("block:" + uuid.toString(), "z"))
                );
    }

    @Override
    public Block getBlock() {
        return block;
    }

    @Override
    public BlockType getType() {
        return Blocks.getBlockType(TK.REDIS.hget("block:" + uuid.toString(), "type"));
    }

    @Override
    public void destroy() {
        block.setType(Material.AIR);
        TK.REDIS.del("block:" + uuid.toString());
        TK.REDIS.del("pos:" + block.getX() + ":" + block.getY() + ":" + block.getZ());
    }

    @Override
    public UUID getUID() {
        return uuid;
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

    public static TKBlock createBlock(TKBlock block, Class<? extends BlockType> clazz) {
        UUID uuid = UUID.randomUUID();
        BlockType type = Blocks.getBlockType(clazz);

        TK.REDIS.hset("block:" + uuid.toString(), "world", block.getBlock().getWorld().getUID().toString());
        TK.REDIS.hset("block:" + uuid.toString(), "x", String.valueOf(block.getX()));
        TK.REDIS.hset("block:" + uuid.toString(), "y", String.valueOf(block.getY()));
        TK.REDIS.hset("block:" + uuid.toString(), "z", String.valueOf(block.getZ()));
        TK.REDIS.hset("block:" + uuid.toString(), "type", type.getName());

        //REVERSE MAPPING
        TK.REDIS.set("pos:" + block.getX() + ":" + block.getY() + ":" + block.getZ(), uuid.toString());

        Sentry.addBreadcrumb("Created block of type: " + type.getName());

        return new RedisBlock(uuid);
    }
}
