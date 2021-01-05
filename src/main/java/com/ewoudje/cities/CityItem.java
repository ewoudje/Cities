package com.ewoudje.cities;

import com.ewoudje.cities.item.ItemType;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import de.tr7zw.nbtapi.NBTCompound;
import de.tr7zw.nbtapi.NBTItem;
import me.wiefferink.interactivemessenger.generators.TellrawGenerator;
import me.wiefferink.interactivemessenger.parsers.YamlParser;
import me.wiefferink.interactivemessenger.processing.Message;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class CityItem {
    private final ItemStack item;
    private final ItemType type;

    public CityItem(@Nonnull ItemStack item, ItemType type) {
        this.item = item;
        this.type = type;
    }

    @Nonnull
    public ItemStack getItem() {
        return item;
    }

    @Nullable
    public ItemType getType() {
        return type;
    }

    public NBTCompound getItemNBT() {
        return new NBTItem(item, true);
    }

    public NBTCompound getNBT() {
        return getItemNBT().getOrCreateCompound("city-item");
    }

    public NBTCompound getDisplayNBT() {
        return getItemNBT().getOrCreateCompound("display");
    }

    public void setName(Message message) {
        JsonObject json = new JsonParser().parse(String.join("", TellrawGenerator.generate(YamlParser.parse(message.getRaw())))).getAsJsonObject();
        json.add("italic", new JsonPrimitive(false));
        getDisplayNBT().setString("Name", json.toString());
    }


}
