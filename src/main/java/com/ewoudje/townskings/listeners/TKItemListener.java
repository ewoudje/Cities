package com.ewoudje.townskings.listeners;

import com.ewoudje.townskings.api.TKPlugin;
import com.ewoudje.townskings.api.wrappers.TKBlock;
import com.ewoudje.townskings.api.wrappers.TKPlayer;
import com.ewoudje.townskings.api.wrappers.TKItem;
import com.ewoudje.townskings.item.Items;
import io.sentry.Sentry;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

public class TKItemListener implements Listener {

    private final TKPlugin plugin;

    public TKItemListener(TKPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent e) {
        try {
            if (e.canBuild()) {
                TKItem item = Items.getItem(e.getItemInHand());
                if (item == null) return;

                TKPlayer player = TKPlayer.wrap(e.getPlayer());

                if (!item.getType().onBuild(player.getWorld(), player, item, TKBlock.wrap(e.getBlockPlaced()))) {
                    e.setBuild(false);
                }
            }
        } catch (Exception ex) {
            Sentry.captureException(ex);
            ex.printStackTrace();
        }
    }

}
