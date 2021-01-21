package com.ewoudje.townskings.remote.response;

import com.ewoudje.townskings.TK;
import org.bukkit.Bukkit;
import org.bukkit.Material;

import java.util.UUID;

public class BlockModifyMessage implements RemoteResponseType<BlockModifyMessage.BlockModifyValues> {

    @Override
    public String getName() {
        return "block";
    }

    @Override
    public boolean sync() {
        return true;
    }

    @Override
    public BlockModifyValues requestValues(String loc) {
        BlockModifyValues values = new BlockModifyValues();

        values.x = Integer.parseInt(TK.REDIS.hget(loc, "x"));
        values.y = Integer.parseInt(TK.REDIS.hget(loc, "y"));
        values.z = Integer.parseInt(TK.REDIS.hget(loc, "z"));

        values.material = Material.getMaterial(TK.REDIS.hget(loc, "material").toUpperCase());
        values.world = UUID.fromString(TK.REDIS.hget(loc, "world"));


        return values;
    }

    @Override
    public void execute(BlockModifyValues input) {
        Bukkit.getWorld(input.world).getBlockAt(input.x, input.y, input.z).setType(input.material);
    }

    public static class BlockModifyValues {
        UUID world;
        int x, y, z;
        Material material;
    }
}
