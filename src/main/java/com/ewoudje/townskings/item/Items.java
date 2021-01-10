package com.ewoudje.townskings.item;

import com.ewoudje.townskings.api.TKPlugin;
import com.ewoudje.townskings.api.wrappers.TKItem;
import com.ewoudje.townskings.api.wrappers.TKWorld;
import com.ewoudje.townskings.api.block.BlockType;
import com.ewoudje.townskings.api.item.ItemType;
import com.ewoudje.townskings.block.Blocks;
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

    public static void register(TKPlugin plugin, HashMap<String, ItemType> items) {
        itemsByName = items;

        Consumer<ItemType> r = (i) -> {
            itemsByName.put(i.getName(), i);
            itemsByClass.put(i.getClass(), i);
        };

        //REGISTER
        r.accept(new ItemNone());
        //END REGISTER

        //ICONS
        for (ItemIcon icon : ItemIcon.values())
            r.accept(icon);

        for (BlockType type : Blocks.getTypes()) {
            if (type instanceof ItemType)
                r.accept((ItemType) type);
        }
    }

    public static TKItem createItem(TKWorld world, NamespacedKey key, int amount) {
        if (key.getNamespace().equals("minecraft")) {
            return new TKItem(new ItemStack(Material.getMaterial(key.getKey().toUpperCase(Locale.ROOT)), amount), null);
        } else if (key.getNamespace().equals("tk")) {
            ItemType type = itemsByName.get(key.getKey());
            TKItem item = new TKItem(new ItemStack(type.getMaterial(), amount), type);
            item.getNBT().setString("type", type.getName());

            type.create(world, item);

            return item;
        } else return null;
    }

    public static TKItem createItem(TKWorld world, Class<? extends ItemType> type) {
        return createItem(world, type, 1);
    }

    public static TKItem createItem(TKWorld world, Class<? extends ItemType> type, int amount) {
        ItemType t = itemsByClass.get(type);
        return createItem(world, t);
    }

    public static TKItem createItem(TKWorld world, ItemType t) {
        return createItem(world, t, 1);
    }

    public static TKItem createItem(TKWorld world, ItemType t, int amount) {
        if (t == null) return null;

        TKItem item = new TKItem(new ItemStack(t.getMaterial(), amount), t);
        item.getNBT().setString("type", t.getName());

        t.create(world, item);
        return item;
    }
}
