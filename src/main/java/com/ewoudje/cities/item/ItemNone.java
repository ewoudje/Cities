package com.ewoudje.cities.item;

import com.ewoudje.cities.CityBlock;
import com.ewoudje.cities.CityItem;
import com.ewoudje.cities.CityPlayer;
import com.ewoudje.cities.CityWorld;
import me.wiefferink.interactivemessenger.processing.Message;
import org.bukkit.Material;

import javax.annotation.Nullable;

public class ItemNone implements ItemType {

    @Override
    public boolean onBuild(CityWorld world, CityPlayer player, CityItem item, CityBlock b) {
        return false;
    }

    @Override
    public void onInteract(CityWorld world, CityItem item) {

    }

    @Override
    public void create(@Nullable CityWorld world, CityItem item) {
        item.getDisplayNBT().setString("Name", "{\"text\": \"\"}");
        item.getItemNBT().setInteger("CustomModelData", 63356);
    }

    @Override
    public String getName() {
        return "none";
    }

    @Override
    public Material getMaterial() {
        return Material.SUGAR;
    }
}
