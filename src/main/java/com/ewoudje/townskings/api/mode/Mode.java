package com.ewoudje.townskings.api.mode;

import com.ewoudje.townskings.api.wrappers.TKBlock;
import com.ewoudje.townskings.api.wrappers.TKPlayer;
import com.ewoudje.townskings.mode.ModeHandler;
import com.ewoudje.townskings.mode.ModeSetting;
import com.ewoudje.townskings.mode.ModeStatus;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public interface Mode {

    void onLeftClick(int slot, @Nonnull ModeHandler modeHandler, @Nonnull TKPlayer player, @Nullable TKBlock block);

    void onRightClick(int slot, @Nonnull ModeHandler modeHandler, @Nonnull TKPlayer player, @Nullable TKBlock block);

    void onSlotChange(int slot, @Nonnull ModeHandler modeHandler, @Nonnull TKPlayer player);

    void init(TKPlayer player);

    @Nullable
    ModeStatus getStatus();

    @Nonnull
    List<ModeSetting> getSettings();

    @Nonnull
    List<ItemStack> getItemList();
}
