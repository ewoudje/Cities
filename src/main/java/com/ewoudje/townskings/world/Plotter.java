package com.ewoudje.townskings.world;

import com.ewoudje.townskings.api.town.Plot;
import com.ewoudje.townskings.api.world.Tile;
import com.ewoudje.townskings.api.world.TilePosition;
import org.bukkit.Bukkit;

import java.util.Map;

public class Plotter {

    public static void claim(Plot plot, final Map<TilePosition, Tile> tiles) {
        Bukkit.getLogger().info("YOU CLAIMED A PLOT!!");
        Bukkit.broadcastMessage("YOU CLAIMED A PLOT!!!!");
    }

}
