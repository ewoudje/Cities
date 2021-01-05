package com.ewoudje.cities.listeners;

import com.ewoudje.cities.Cities;
import com.ewoudje.cities.CityPlayer;
import com.ewoudje.cities.CityItem;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

public class CityItemListener implements Listener {

    private final Cities plugin;

    public CityItemListener(Cities plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent e) {
        if (e.canBuild()) {
            CityItem item = plugin.getItem(e.getItemInHand());
            if (item == null) return;

            CityPlayer player = plugin.getPlayer(e.getPlayer());

            if (!item.getType().onBuild(player.getWorld(), player, item, player.getWorld().getBlock(e.getBlockPlaced()))) {
                e.setBuild(false);
            }
        }
    }

}
