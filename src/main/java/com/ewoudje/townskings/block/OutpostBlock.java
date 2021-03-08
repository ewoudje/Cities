package com.ewoudje.townskings.block;

import com.ewoudje.townskings.api.TKPlugin;
import com.ewoudje.townskings.api.block.BlockData;
import com.ewoudje.townskings.api.block.BlockType;
import com.ewoudje.townskings.api.item.ItemType;
import com.ewoudje.townskings.api.town.Town;
import com.ewoudje.townskings.api.wrappers.TKBlock;
import com.ewoudje.townskings.api.wrappers.TKItem;
import com.ewoudje.townskings.api.wrappers.TKPlayer;
import com.ewoudje.townskings.api.wrappers.TKWorld;
import com.ewoudje.townskings.remote.RemotePlot;
import com.ewoudje.townskings.util.SendUtil;
import de.tr7zw.nbtapi.NBTCompound;
import me.wiefferink.interactivemessenger.processing.Message;
import org.bukkit.Material;

import javax.annotation.Nullable;
import java.util.UUID;

public class OutpostBlock implements ItemType, BlockType {

    private final TKPlugin plugin;

    public OutpostBlock(TKPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getName() {
        return "outpost_block";
    }

    @Override
    public boolean onBreak(TKWorld world, TKPlayer player, TKBlock block) {
        return false;
    }

    @Nullable
    @Override
    public BlockData createBlockData() {
        return null;
    }

    @Override
    public boolean onBuild(TKWorld world, TKPlayer player, TKItem item, TKBlock b) {
        Town town = player.getTown();
        if (town == null) {
            SendUtil.send(player, Message.fromKey("have-no-town"));
            return false;
        }

        SendUtil.broadcast(Message.fromKey("town-new-outpost").replacements(town.getName()));

        b.getBlock().setType(Material.LECTERN);
        RemotePlot.createPlot(world, UUID.randomUUID().toString(),
                b.getPosition().add(-50, 0, -50),
                b.getPosition().add(50, 0, 50),
                town.getPlotCategory("default"),
                false);

        return true;
    }

    @Override
    public void onInteract(TKWorld world, TKItem item) {

    }

    @Override
    public void create(@Nullable TKWorld world, TKItem item) {
        NBTCompound compound = item.getNBT();
        compound.setBoolean("ready", false);
        item.setName(Message.fromKey("outpost-block"));
    }

    @Override
    public Material getMaterial() {
        return Material.IRON_TRAPDOOR;
    }
}
