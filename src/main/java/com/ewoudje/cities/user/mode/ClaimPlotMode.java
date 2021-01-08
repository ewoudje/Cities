package com.ewoudje.cities.user.mode;

import com.ewoudje.cities.CityBlock;
import com.ewoudje.cities.CityItem;
import com.ewoudje.cities.CityPlayer;
import com.ewoudje.cities.CityWorld;
import com.ewoudje.cities.item.InventoryDesigner;
import com.ewoudje.cities.item.ItemIcon;
import com.ewoudje.cities.item.ItemNone;
import com.ewoudje.cities.item.Items;
import com.ewoudje.cities.mode.Mode;
import com.ewoudje.cities.mode.ModeHandler;
import com.ewoudje.cities.mode.ModeSetting;
import com.ewoudje.cities.mode.ModeStatus;
import me.wiefferink.interactivemessenger.processing.Message;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class ClaimPlotMode implements Mode {
    private final InventoryDesigner inventoryDesigner = new InventoryDesigner(Material.AIR);
    private boolean depth;

    public ClaimPlotMode() {
        CityItem item = Items.createItem(null, ItemNone.class);
        for (int i = 36; i <= 44; i++)
            inventoryDesigner.set(i, item);

        inventoryDesigner.set(36, Items.createItem(null, ItemIcon.TWO_POINT));

        depth(true);
    }

    private void toggleDepth() {
        depth(!depth);
    }

    private void depth(boolean depth) {
        this.depth = depth;
        if (depth)
            inventoryDesigner.set(38, Items.createItem(null, ItemIcon.DEPTH_ON));
        else
            inventoryDesigner.set(38, Items.createItem(null, ItemIcon.DEPTH_OFF));
    }

    @Override
    public void onLeftClick(int slot, @Nonnull ModeHandler modeHandler, @Nonnull CityPlayer player, @Nullable CityBlock block) {
        switch (slot) {
            case 0:
                player.send(Message.fromKey("not-implemented"));
                break;
        }
    }

    @Override
    public void onRightClick(int slot, @Nonnull ModeHandler modeHandler, @Nonnull CityPlayer player, @Nullable CityBlock block) {
        switch (slot) {
            case 0:
                player.send(Message.fromKey("not-implemented"));
                break;
            case 2:
                toggleDepth();
                modeHandler.updateInventory(player);
                break;
        }
    }

    @Override
    public void init(CityPlayer player) {

    }

    @Nullable
    @Override
    public ModeStatus getStatus() {
        return null;
    }

    @Nonnull
    @Override
    public List<ModeSetting> getSettings() {
        return List.of(ModeSetting.FLY, ModeSetting.CLIENTMODE, ModeSetting.INVULNERABLE, ModeSetting.ADVENTURE_MODE);
    }

    @Nonnull
    @Override
    public List<ItemStack> getItemList() {
        return inventoryDesigner.getPlayerFullItemList();
    }
}
