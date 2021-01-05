package com.ewoudje.cities.listeners;

import com.ewoudje.cities.Cities;
import com.ewoudje.cities.CityBlock;
import com.ewoudje.cities.CityPlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class CityBlockListener implements Listener {

    private final Cities plugin;

    public CityBlockListener(Cities plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onBreak(BlockBreakEvent e) {
        if (!e.isCancelled()) {
            CityBlock block = plugin.getBlock(e.getBlock());
            if (block == null || block.getType() == null) return;

            CityPlayer player = plugin.getPlayer(e.getPlayer());

            if (!block.getType().onBreak(player.getWorld(), player, block)) {
                e.setCancelled(true);
            }
        }
    }
}
