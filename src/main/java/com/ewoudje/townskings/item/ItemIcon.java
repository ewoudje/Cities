package com.ewoudje.townskings.item;

import com.ewoudje.townskings.api.wrappers.TKBlock;
import com.ewoudje.townskings.api.wrappers.TKItem;
import com.ewoudje.townskings.api.wrappers.TKPlayer;
import com.ewoudje.townskings.api.item.ItemType;
import com.ewoudje.townskings.api.wrappers.TKWorld;
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
    DEPTH_ON(10, "depth-on"),
    APPLY(11, "apply")
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
    public boolean onBuild(TKWorld world, TKPlayer player, TKItem item, TKBlock b) {
        return false;
    }

    @Override
    public void onInteract(TKWorld world, TKItem item) {

    }

    @Override
    public void create(@Nullable TKWorld world, TKItem item) {
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
