package com.ewoudje.townskings;

import com.ewoudje.townskings.api.TKPlugin;
import com.ewoudje.townskings.api.wrappers.TKBlock;
import com.ewoudje.townskings.api.wrappers.TKItem;
import com.ewoudje.townskings.api.wrappers.TKPlayer;
import com.ewoudje.townskings.api.wrappers.TKWorld;
import com.ewoudje.townskings.block.Blocks;
import com.ewoudje.townskings.user.commands.TKCommands;
import com.ewoudje.townskings.user.commands.TKPlayerProvider;
import com.ewoudje.townskings.user.commands.TKPlayerSenderProvider;
import com.ewoudje.townskings.user.commands.TKProvider;
import com.ewoudje.townskings.api.item.ItemType;
import com.ewoudje.townskings.item.Items;
import com.ewoudje.townskings.item.RecipesHandler;
import com.ewoudje.townskings.listeners.TKBlockListener;
import com.ewoudje.townskings.listeners.TKItemListener;
import com.ewoudje.townskings.listeners.TKPlayerListener;
import com.ewoudje.townskings.mode.ModeHandler;
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

public final class TK extends JavaPlugin implements TKPlugin {

    private CommandService drink;
    private LanguageManager languageManager;
    private Gson gson;
    private RecipesHandler recipesHandler;
    private ModeHandler modeHandler;

    private HashMap<World, TKWorld> worlds = new HashMap<>();
    private HashMap<Player, TKPlayer> players = new HashMap<>();
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
                Collections.singletonList("[TKPlugin] ") // Chat prefix to use with Message#prefix(), could of course come from the config file
        );

        new TKPlayerSenderProvider(this).bind(drink);
        new TKPlayerProvider(this).bind(drink);
        new TKProvider(this).bind(drink);

        drink.register(new TKCommands(this), "tk", "townskings");

        drink.registerCommands();

        Blocks.register(this, new HashMap<>());
        Items.register(this, items);
        recipesHandler = new RecipesHandler(this);
        modeHandler = new ModeHandler(this);

        getServer().getPluginManager().registerEvents(new TKPlayerListener(this, players), this);
        getServer().getPluginManager().registerEvents(new TKItemListener(this), this);
        getServer().getPluginManager().registerEvents(new TKBlockListener(this), this);

        getServer().getWorlds().forEach((w) -> worlds.put(w, new TKWorld(w, this)));
        getServer().getOnlinePlayers().forEach((p) -> players.put(p, new TKPlayer(p, getWorld(p.getWorld()))));

        getServer().getScheduler().runTaskTimerAsynchronously(this, this::save, 200, getConfig().getInt("save-interval"));
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        recipesHandler.removeRecipes();
        modeHandler.disable();
        save();
    }

    public String getVersion() {
        return getConfig().getString("version");
    }

    private void save() {
        worlds.forEach((w, ww) -> ww.save());
    }

    public TKWorld getWorld(World world) {
        return worlds.get(world);
    }

    public Collection<TKWorld> getWorlds() {
        return worlds.values();
    }

    public TKPlayer getPlayer(Player player) {
        return players.get(player);
    }

    public TKBlock getBlock(Block block) {
        return getWorld(block.getWorld()).getBlock(block);
    }

    public TKItem getItem(ItemStack item) {
        if (item.getAmount() == 0) return null;
        NBTItem nbt = new NBTItem(item);
        if (nbt.getCompound("town-item") == null) return null;
        ItemType type = items.get(nbt.getCompound("town-item").getString("type"));
        if (type == null) return null;
        return new TKItem(item, type);
    }

    public Gson getGson() {
        return gson;
    }


    public ModeHandler getModeHandler() {
        return modeHandler;
    }
}
