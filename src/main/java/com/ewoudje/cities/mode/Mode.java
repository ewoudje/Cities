package com.ewoudje.cities.mode;

import com.ewoudje.cities.CityBlock;
import com.ewoudje.cities.CityPlayer;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public interface Mode {

    void onLeftClick(int slot, @Nonnull ModeHandler modeHandler, @Nonnull CityPlayer player, @Nullable CityBlock block);

    void onRightClick(int slot, @Nonnull ModeHandler modeHandler, @Nonnull CityPlayer player, @Nullable CityBlock block);

    void init(CityPlayer player);

    @Nullable
    ModeStatus getStatus();

    @Nonnull
    List<ModeSetting> getSettings();

    @Nonnull
    List<ItemStack> getItemList();
}
