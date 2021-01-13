package com.ewoudje.townskings.listeners;

import com.ewoudje.townskings.api.TKPlugin;
import com.ewoudje.townskings.api.wrappers.TKBlock;
import com.ewoudje.townskings.api.wrappers.TKPlayer;
import io.sentry.Sentry;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class TKBlockListener implements Listener {

    private final TKPlugin plugin;

    public TKBlockListener(TKPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onBreak(BlockBreakEvent e) {
        try {
            if (!e.isCancelled()) {
                TKBlock block = plugin.getBlock(e.getBlock());
                if (block == null || block.getType() == null) return;

                TKPlayer player = plugin.getPlayer(e.getPlayer());

                if (!block.getType().onBreak(player.getWorld(), player, block)) {
                    e.setCancelled(true);
                }
            }
        } catch (Exception ex) {
            Sentry.captureException(ex);
        }
    }
}
