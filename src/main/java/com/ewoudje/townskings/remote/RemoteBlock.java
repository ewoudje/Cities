package com.ewoudje.townskings.remote;

import com.ewoudje.townskings.api.UReference;
import com.ewoudje.townskings.api.block.BlockType;
import com.ewoudje.townskings.api.wrappers.TKBlock;
import com.ewoudje.townskings.block.Blocks;
import com.ewoudje.townskings.remote.faktory.FaktoryPriority;
import io.sentry.Sentry;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;

import java.nio.ByteBuffer;
import java.util.UUID;

public class RemoteBlock implements TKBlock, UReference {
    public final static RemoteHelper R = new RemoteHelper("Block", FaktoryPriority.MC);

    private final UUID uuid;
    private final Block block;

    public RemoteBlock(UUID uuid) {
        this.uuid = uuid;
        block = Bukkit.getWorld(UUID.fromString(R.get(uuid, "world")))
                .getBlockAt(
                        Integer.parseInt(R.get(uuid, "x")),
                        Integer.parseInt(R.get(uuid, "y")),
                        Integer.parseInt(R.get(uuid, "z"))
                );
    }

    public RemoteBlock(UUID uuid, Block block) {
        this.uuid = uuid;
        this.block = block;
    }

    @Override
    public Block getBlock() {
        return block;
    }

    @Override
    public BlockType getType() {
        return Blocks.getBlockType(R.get(uuid, "type"));
    }

    @Override
    public void destroy() {
        block.setType(Material.AIR);
        R.execute(uuid, "Destroy", (i) -> true);
        //R.delete(uuid);
        //TK.REDIS.del("pos:" + block.getX() + ":" + block.getY() + ":" + block.getZ());
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

    /**
     * @apiNote DO NOT USE always try to use with existing UUID's
     * @param block original block
     * @param clazz class of new block
     * @return NEW BLOCK
     */
    public static TKBlock createBlock(TKBlock block, Class<? extends BlockType> clazz) {
        ByteBuffer buffer = ByteBuffer.allocate(4 * 3);

        buffer.putInt(block.getX());
        buffer.putInt(block.getY());
        buffer.putInt(block.getZ());

        UUID uuid = UUID.nameUUIDFromBytes(buffer.array());
        return createBlock(uuid, block, clazz);
    }

    public static TKBlock createBlock(UUID uuid, TKBlock block, Class<? extends BlockType> clazz) {
        BlockType type = Blocks.getBlockType(clazz);

        R.execute(uuid, "Create", (i) -> i == 0,
                block.getBlock().getWorld().getUID().toString(),
                type.getName(),
                String.valueOf(block.getX()),
                String.valueOf(block.getY()),
                String.valueOf(block.getZ())
        );
        //REVERSE MAPPING
        //TK.REDIS.set("pos:" + block.getX() + ":" + block.getY() + ":" + block.getZ(), uuid.toString());

        Sentry.addBreadcrumb("Created block of type: " + type.getName());

        return new RemoteBlock(uuid, block.getBlock());
    }
}
