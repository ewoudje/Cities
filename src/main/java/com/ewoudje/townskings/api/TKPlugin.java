package com.ewoudje.townskings.api;

import com.ewoudje.townskings.api.wrappers.TKBlock;
import com.ewoudje.townskings.api.wrappers.TKItem;
import com.ewoudje.townskings.api.wrappers.TKPlayer;
import com.ewoudje.townskings.api.wrappers.TKWorld;
import com.ewoudje.townskings.mode.ModeHandler;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.Collection;
import java.util.logging.Logger;

public interface TKPlugin extends Plugin {

    String getVersion();


    TKWorld getWorld(World world);

    Collection<TKWorld> getWorlds();

    TKPlayer getPlayer(Player player);

    TKBlock getBlock(Block block);

    TKItem getItem(ItemStack item);


    ModeHandler getModeHandler();

    Logger getLogger();
}
