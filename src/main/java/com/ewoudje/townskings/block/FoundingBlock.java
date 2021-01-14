package com.ewoudje.townskings.block;

import com.ewoudje.townskings.api.TKPlugin;
import com.ewoudje.townskings.api.block.BlockData;
import com.ewoudje.townskings.api.block.BlockType;
import com.ewoudje.townskings.api.item.ItemType;
import com.ewoudje.townskings.api.wrappers.TKBlock;
import com.ewoudje.townskings.api.wrappers.TKItem;
import com.ewoudje.townskings.api.wrappers.TKPlayer;
import com.ewoudje.townskings.api.wrappers.TKWorld;
import com.ewoudje.townskings.datastore.RedisBlock;
import com.ewoudje.townskings.datastore.RedisTown;
import com.ewoudje.townskings.util.SendUtil;
import de.tr7zw.nbtapi.NBTCompound;
import me.wiefferink.interactivemessenger.processing.Message;
import org.bukkit.Material;

import javax.annotation.Nonnull;

public class FoundingBlock implements ItemType, BlockType {

    private float minDistance;

    public FoundingBlock(TKPlugin plugin) {
        double tmp = plugin.getConfig().getDouble("minimum-distance-towns");
        minDistance = (float) (tmp * tmp);
    }

    @Override
    public boolean onBuild(TKWorld world, TKPlayer player, TKItem item, TKBlock b) {
        NBTCompound compound = item.getNBT();

        if (player.getTown() != null) {
            SendUtil.send(player, Message.fromKey("already-in-town").replacements(player.getTown().getName()));
            return false;
        }

        if (!compound.getBoolean("ready")) {
            SendUtil.send(player, Message.fromKey("founding-not-ready"));
            return false;
        }


        //TODO SPATIAL
        if (false) { //world.getTowns().stream().flatMap((p) -> p.getClaimPoints().stream())
                     //.anyMatch((l) -> b.getBlock().getLocation().distanceSquared(l) < minDistance)) {
            SendUtil.send(player, Message.fromKey("too-close-town"));
            return false;
        }

        String name = compound.getString("town-name");

        if (player.getWorld().getTown(name).isPresent()) {
            SendUtil.send(player, Message.fromKey("town-exists").replacements(name));
            return false;
        }

        TKBlock fBlock = RedisBlock.createBlock(b, this.getClass());

        RedisTown.create(name, player, fBlock, world);

        SendUtil.broadcast(Message.fromKey("broadcast-create-town")
                .replacements(player.getPlayer().getName(), name));

        SendUtil.send(player, Message.fromKey("create-town").replacements(name));

        return true;
    }

    @Override
    public void onInteract(TKWorld world, TKItem item) {

    }

    @Override
    public void create(TKWorld world, TKItem item) {
        NBTCompound compound = item.getNBT();
        compound.setBoolean("ready", false);
        item.setName(Message.fromKey("founding-block"));
    }

    @Override
    public boolean onBreak(TKWorld world, TKPlayer player, TKBlock block) {
        return false;
    }

    @Override
    public BlockData createBlockData() {
        return null;
    }

    @Override
    public @Nonnull String getName() {
        return "founding_block";
    }

    @Override
    public Material getMaterial() {
        return Material.LECTERN;
    }

    public static void makeReady(TKItem item, String name) {
        NBTCompound compound = item.getNBT();
        compound.setBoolean("ready", true);
        compound.setString("town-name", name);
    }
}
