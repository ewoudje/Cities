package com.ewoudje.cities.block;

import com.ewoudje.cities.CityBlock;
import com.ewoudje.cities.CityPlayer;
import com.ewoudje.cities.CityWorld;
import com.ewoudje.cities.CityItem;
import com.ewoudje.cities.item.ItemType;
import de.tr7zw.nbtapi.NBTCompound;
import me.wiefferink.interactivemessenger.processing.Message;
import org.bukkit.Material;

import javax.annotation.Nonnull;

public class FoundingBlock implements ItemType, BlockType {

    @Override
    public boolean onBuild(CityWorld world, CityPlayer player, CityItem item, CityBlock b) {
        NBTCompound compound = item.getNBT();

        if (player.getCity() != null) {
            player.send(Message.fromKey("already-in-city").replacements(player.getCity().getName()));
            return false;
        }

        if (!compound.getBoolean("ready")) {
            player.send(Message.fromKey("founding-not-ready"));
            return false;
        }

        String name = compound.getString("city-name");

        CityBlock fBlock = world.createBlock(b, this.getClass());

        world.createCity(name, player, fBlock);

        world.broadcast(Message.fromKey("broadcast-create-city")
                .replacements(player.getPlayer().getName(), name));

        player.send(Message.fromKey("create-city").replacements(name));

        return true;
    }

    @Override
    public void onInteract(CityWorld world, CityItem item) {

    }

    @Override
    public void create(CityWorld world, CityItem item) {
        NBTCompound compound = item.getNBT();
        compound.setBoolean("ready", false);
        item.setName(Message.fromKey("founding-block"));
    }

    @Override
    public boolean onBreak(CityWorld world, CityPlayer player, CityBlock block) {
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

    public static void makeReady(CityItem item, String name) {
        NBTCompound compound = item.getNBT();
        compound.setBoolean("ready", true);
        compound.setString("city-name", name);
    }
}
