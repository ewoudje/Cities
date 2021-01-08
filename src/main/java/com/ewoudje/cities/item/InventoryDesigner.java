package com.ewoudje.cities.item;

import com.ewoudje.cities.CityItem;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InventoryDesigner {

    private HashMap<Integer, ItemStack> items = new HashMap<>();
    private ItemStack def;

    public InventoryDesigner() {
        this(Material.AIR);
    }


    public InventoryDesigner(Material def) {
        this.def = new ItemStack(def);
        if (def != Material.AIR) {
            set(5, Material.AIR);
            set(6, Material.AIR);
            set(7, Material.AIR);
            set(8, Material.AIR);
            set(45, Material.AIR);
        }
    }

    public InventoryDesigner(ItemStack item) {
        this.def = item;
    }

    public InventoryDesigner(CityItem item) {
        this.def = item.getItem();
    }

    public void set(int slot, CityItem item) {
        items.put(slot, item.getItem());
    }

    public void set(int slot, ItemStack item) {
        items.put(slot, item);
    }

    public void set(int slot, Material item) {
        items.put(slot, new ItemStack(item));
    }

    public List<ItemStack> getPlayerFullItemList() {
        List<ItemStack> result = new ArrayList<>(46);

        for (int i = 0; i < 46; i++) {
            ItemStack item = items.get(i);

            if (item == null) item = new ItemStack(def);

            result.add(item);
        }

        return result;
    }

}
