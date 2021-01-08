package com.ewoudje.cities.item;

import com.ewoudje.cities.CityBlock;
import com.ewoudje.cities.CityItem;
import com.ewoudje.cities.CityPlayer;
import com.ewoudje.cities.CityWorld;
import me.wiefferink.interactivemessenger.processing.Message;
import org.bukkit.Material;

import javax.annotation.Nullable;

public enum ItemIcon implements ItemType {
    PLUS(3),
    MINUS(4),
    ROAD(5, "road"),
    CLAIM(6, "claim"),
    TWO_POINT(7, "2point"),
    SHOW_CLAIM(8, "show-claim"),
    DEPTH_OFF(9, "depth-off"),
    DEPTH_ON(10, "depth-on")
    ;

    private String displayKey;
    private int id;

    ItemIcon(int id) {
        this(id, null);

    }

    ItemIcon(int id, String displayKey) {
        this.id = id;
        this.displayKey = displayKey;
    }

    @Override
    public boolean onBuild(CityWorld world, CityPlayer player, CityItem item, CityBlock b) {
        return false;
    }

    @Override
    public void onInteract(CityWorld world, CityItem item) {

    }

    @Override
    public void create(@Nullable CityWorld world, CityItem item) {
        if (displayKey != null)
            item.setName(Message.fromKey("icon-" + displayKey));
        else
            item.getDisplayNBT().setString("Name", "{\"text\": \"\"}");
        item.getItemNBT().setInteger("CustomModelData", id);
    }

    @Override
    public String getName() {
        return name();
    }

    @Override
    public Material getMaterial() {
        return Material.SUGAR;
    }
}
