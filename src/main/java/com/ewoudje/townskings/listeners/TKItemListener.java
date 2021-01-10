package com.ewoudje.townskings.listeners;

import com.ewoudje.townskings.api.TKPlugin;
import com.ewoudje.townskings.api.wrappers.TKPlayer;
import com.ewoudje.townskings.api.wrappers.TKItem;
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
        if (e.canBuild()) {
            TKItem item = plugin.getItem(e.getItemInHand());
            if (item == null) return;

            TKPlayer player = plugin.getPlayer(e.getPlayer());

            if (!item.getType().onBuild(player.getWorld(), player, item, player.getWorld().getBlock(e.getBlockPlaced()))) {
                e.setBuild(false);
            }
        }
    }

}
