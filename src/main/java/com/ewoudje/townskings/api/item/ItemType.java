package com.ewoudje.townskings.api.item;

import com.ewoudje.townskings.api.Named;
import com.ewoudje.townskings.api.wrappers.TKBlock;
import com.ewoudje.townskings.api.wrappers.TKItem;
import com.ewoudje.townskings.api.wrappers.TKPlayer;
import com.ewoudje.townskings.api.wrappers.TKWorld;
import org.bukkit.Material;

import javax.annotation.Nullable;

public interface ItemType extends Named {

    boolean onBuild(TKWorld world, TKPlayer player, TKItem item, TKBlock b);

    void onInteract(TKWorld world, TKItem item);

    void create(@Nullable TKWorld world, TKItem item);

    default boolean is(Class<? extends ItemType> type) {
        return this.getClass() == type;
    }

    Material getMaterial();
}
