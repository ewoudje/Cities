package com.ewoudje.townskings.user.mode;

import com.ewoudje.townskings.api.world.BlockPosition;
import com.ewoudje.townskings.api.wrappers.TKBlock;
import com.ewoudje.townskings.api.wrappers.TKItem;
import com.ewoudje.townskings.api.wrappers.TKPlayer;
import com.ewoudje.townskings.item.InventoryDesigner;
import com.ewoudje.townskings.item.ItemIcon;
import com.ewoudje.townskings.item.ItemNone;
import com.ewoudje.townskings.item.Items;
import com.ewoudje.townskings.api.mode.Mode;
import com.ewoudje.townskings.mode.ModeHandler;
import com.ewoudje.townskings.mode.ModeSetting;
import com.ewoudje.townskings.mode.ModeStatus;
import com.ewoudje.townskings.world.FillBlockChange;
import com.ewoudje.townskings.world.HollowBlockChange;
import me.wiefferink.interactivemessenger.processing.Message;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class ClaimPlotMode implements Mode {
    private final InventoryDesigner inventoryDesigner = new InventoryDesigner(Material.AIR);
    private boolean depth;
    private boolean wasSlot1;
    private BlockPosition start;
    private BlockPosition end;

    public ClaimPlotMode() {
        TKItem item = Items.createItem(null, ItemNone.class);
        for (int i = 36; i <= 44; i++)
            inventoryDesigner.set(i, item);

        inventoryDesigner.set(36, Items.createItem(null, ItemIcon.TWO_POINT));
        inventoryDesigner.set(37, Items.createItem(null, ItemIcon.SHOW_CLAIM));

        depth(true);
    }

    private void toggleDepth() {
        depth(!depth);
    }

    private void depth(boolean depth) {
        this.depth = depth;
        if (depth)
            inventoryDesigner.set(39, Items.createItem(null, ItemIcon.DEPTH_ON));
        else
            inventoryDesigner.set(39, Items.createItem(null, ItemIcon.DEPTH_OFF));
    }

    @Override
    public void onLeftClick(int slot, @Nonnull ModeHandler modeHandler, @Nonnull TKPlayer player, @Nullable TKBlock block) {
        switch (slot) {
            case 0:
                start = new BlockPosition(block.getBlock());
                player.send(Message.fromKey("select-block1"));
                if (start.getY() > end.getY()) {
                    BlockPosition tmp = end;
                    end = start;
                    start = tmp;
                }
                break;
        }
    }

    @Override
    public void onRightClick(int slot, @Nonnull ModeHandler modeHandler, @Nonnull TKPlayer player, @Nullable TKBlock block) {
        switch (slot) {
            case 0:
                end = new BlockPosition(block.getBlock());
                player.send(Message.fromKey("select-block2"));
                if (start.getY() > end.getY()) {
                    BlockPosition tmp = end;
                    end = start;
                    start = tmp;
                }
                break;
            case 3:
                toggleDepth();
                modeHandler.updateInventory(player);
                break;
        }
    }

    @Override
    public void onSlotChange(int slot, @Nonnull ModeHandler modeHandler, @Nonnull TKPlayer player) {
        if (slot == 1 && end != null && start != null) {
            modeHandler.showBlocks(player, new HollowBlockChange(
                    new BlockPosition(start.getX(), depth ? start.getY() - 20 : start.getY(), start.getZ()),
                    new BlockPosition(end.getX(), depth ? end.getY() + 20 : end.getY(), end.getZ())
                    , Material.GLASS, player.getWorld(), !depth));
            wasSlot1 = true;
        } else if (slot != 1 && wasSlot1) {
            modeHandler.updateChunks(player);
            wasSlot1 = false;
        }
    }

    @Override
    public void init(TKPlayer player) {

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
