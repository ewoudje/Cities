package com.ewoudje.cities;

import com.ewoudje.cities.block.Blocks;
import com.ewoudje.cities.commands.CitiesCommands;
import com.ewoudje.cities.commands.CityPlayerProvider;
import com.ewoudje.cities.commands.CityPlayerSenderProvider;
import com.ewoudje.cities.commands.CityProvider;
import com.ewoudje.cities.item.ItemType;
import com.ewoudje.cities.item.Items;
import com.ewoudje.cities.item.RecipesHandler;
import com.ewoudje.cities.listeners.CityBlockListener;
import com.ewoudje.cities.listeners.CityItemListener;
import com.ewoudje.cities.listeners.CityPlayerListener;
import com.google.gson.Gson;
import com.jonahseguin.drink.CommandService;
import com.jonahseguin.drink.Drink;
import de.tr7zw.nbtapi.NBTItem;
import me.wiefferink.interactivemessenger.source.LanguageManager;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;

public final class Cities extends JavaPlugin {

    private CommandService drink;
    private LanguageManager languageManager;
    private Gson gson;
    private RecipesHandler recipesHandler;

    private HashMap<World, CityWorld> worlds = new HashMap<>();
    private HashMap<Player, CityPlayer> players = new HashMap<>();
    private HashMap<String, ItemType> items = new HashMap<>();

    @Override
    public void onEnable() {

        gson = new Gson();

        drink = Drink.get(this);
        languageManager = new LanguageManager(
                this,                                  // The plugin (used to get the languages bundled in the jar file)
                "languages",                           // Folder where the languages are stored
                getConfig().getString("languages"),     // The language to use indicated by the plugin user
                "EN",                                  // The default language, expected to be shipped with the plugin and should be complete, fills in gaps in the user-selected language
                Collections.singletonList("[Cities] ") // Chat prefix to use with Message#prefix(), could of course come from the config file
        );

        new CityPlayerSenderProvider(this).bind(drink);
        new CityPlayerProvider(this).bind(drink);
        new CityProvider(this).bind(drink);

        drink.register(new CitiesCommands(this), "cities", "ct");

        drink.registerCommands();

        Blocks.register(this, new HashMap<>());
        Items.register(this, items);
        recipesHandler = new RecipesHandler(this);

        getServer().getPluginManager().registerEvents(new CityPlayerListener(this, players), this);
        getServer().getPluginManager().registerEvents(new CityItemListener(this), this);
        getServer().getPluginManager().registerEvents(new CityBlockListener(this), this);

        getServer().getWorlds().forEach((w) -> worlds.put(w, new CityWorld(w, this)));
        getServer().getOnlinePlayers().forEach((p) -> players.put(p, new CityPlayer(p, getWorld(p.getWorld()))));

        getServer().getScheduler().runTaskTimerAsynchronously(this, this::save, 200, getConfig().getInt("save-interval"));
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        recipesHandler.removeRecipes();
        save();
    }

    public String getVersion() {
        return getConfig().getString("version");
    }

    private void save() {
        worlds.forEach((w, ww) -> ww.save());
    }

    public CityWorld getWorld(World world) {
        return worlds.get(world);
    }

    public Collection<CityWorld> getWorlds() {
        return worlds.values();
    }

    public CityPlayer getPlayer(Player player) {
        return players.get(player);
    }

    public CityBlock getBlock(Block block) {
        return getWorld(block.getWorld()).getBlock(block);
    }

    public CityItem getItem(ItemStack item) {
        if (item.getAmount() == 0) return null;
        NBTItem nbt = new NBTItem(item);
        if (nbt.getCompound("city-item") == null) return null;
        ItemType type = items.get(nbt.getCompound("city-item").getString("type"));
        if (type == null) return null;
        return new CityItem(item, type);
    }

    public Gson getGson() {
        return gson;
    }


}
