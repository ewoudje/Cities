package com.ewoudje.cities.item;

import com.ewoudje.cities.CityBlock;
import com.ewoudje.cities.CityItem;
import com.ewoudje.cities.CityPlayer;
import com.ewoudje.cities.CityWorld;
import org.bukkit.Material;

import javax.annotation.Nullable;

public interface ItemType {

    boolean onBuild(CityWorld world, CityPlayer player, CityItem item, CityBlock b);

    void onInteract(CityWorld world, CityItem item);

    void create(@Nullable CityWorld world, CityItem item);

    String getName();

    default boolean is(Class<? extends ItemType> type) {
        return this.getClass() == type;
    }

    Material getMaterial();
}
