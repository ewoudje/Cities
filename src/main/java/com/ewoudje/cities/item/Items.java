package com.ewoudje.cities.item;

import com.ewoudje.cities.Cities;
import com.ewoudje.cities.CityItem;
import com.ewoudje.cities.CityWorld;
import com.ewoudje.cities.block.BlockType;
import com.ewoudje.cities.block.Blocks;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.function.Consumer;

public class Items {

    private static Map<Class<? extends ItemType>, ItemType> itemsByClass = new HashMap<>();
    private static HashMap<String, ItemType> itemsByName = new HashMap<>();

    public static void register(Cities plugin, HashMap<String, ItemType> items) {
        itemsByName = items;

        Consumer<ItemType> r = (i) -> {
            itemsByName.put(i.getName(), i);
            itemsByClass.put(i.getClass(), i);
        };

        //REGISTER

        //END REGISTER

        for (BlockType type : Blocks.getTypes()) {
            if (type instanceof ItemType)
                r.accept((ItemType) type);
        }
    }

    public static CityItem createItem(CityWorld world, NamespacedKey key, int amount) {
        if (key.getNamespace().equals("minecraft")) {
            return new CityItem(new ItemStack(Material.getMaterial(key.getKey().toUpperCase(Locale.ROOT)), amount), null);
        } else if (key.getNamespace().equals("cities")) {
            ItemType type = itemsByName.get(key.getKey());
            CityItem item = new CityItem(new ItemStack(type.getMaterial(), amount), type);
            item.getNBT().setString("type", type.getName());

            type.create(world, item);

            return item;
        } else return null;
    }

    public static CityItem createItem(CityWorld world, Class<? extends ItemType> type) {
        return createItem(world, type, 1);
    }

    public static CityItem createItem(CityWorld world, Class<? extends ItemType> type, int amount) {
        ItemType t = itemsByClass.get(type);
        if (t == null) return null;

        CityItem item = new CityItem(new ItemStack(t.getMaterial(), amount), t);
        item.getNBT().setString("type", t.getName());

        t.create(world, item);

        return item;
    }
}
