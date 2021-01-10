package com.ewoudje.townskings.item;

import com.ewoudje.townskings.api.wrappers.TKBlock;
import com.ewoudje.townskings.api.wrappers.TKItem;
import com.ewoudje.townskings.api.wrappers.TKPlayer;
import com.ewoudje.townskings.api.wrappers.TKWorld;
import com.ewoudje.townskings.api.item.ItemType;
import org.bukkit.Material;

import javax.annotation.Nullable;

public class ItemNone implements ItemType {

    @Override
    public boolean onBuild(TKWorld world, TKPlayer player, TKItem item, TKBlock b) {
        return false;
    }

    @Override
    public void onInteract(TKWorld world, TKItem item) {

    }

    @Override
    public void create(@Nullable TKWorld world, TKItem item) {
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
