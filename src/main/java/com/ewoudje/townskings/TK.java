package com.ewoudje.townskings;

import com.ewoudje.townskings.api.TKPlugin;
import com.ewoudje.townskings.block.Blocks;
import com.ewoudje.townskings.item.Items;
import com.ewoudje.townskings.item.RecipesHandler;
import com.ewoudje.townskings.listeners.TKBlockListener;
import com.ewoudje.townskings.listeners.TKItemListener;
import com.ewoudje.townskings.listeners.TKPlayerListener;
import com.ewoudje.townskings.mode.ModeHandler;
import com.ewoudje.townskings.remote.faktory.FaktoryWrapper;
import com.ewoudje.townskings.remote.response.BlockModifyMessage;
import com.ewoudje.townskings.remote.response.ChatMessage;
import com.ewoudje.townskings.remote.response.RemoteResponseHandler;
import com.ewoudje.townskings.user.commands.*;
import com.jonahseguin.drink.CommandService;
import com.jonahseguin.drink.Drink;
import io.sentry.Sentry;
import me.wiefferink.interactivemessenger.source.LanguageManager;
import org.bukkit.plugin.java.JavaPlugin;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.Collections;
import java.util.HashMap;

public final class TK extends JavaPlugin implements TKPlugin {

    private CommandService drink;
    private LanguageManager languageManager;
    private RecipesHandler recipesHandler;
    private ModeHandler modeHandler;
    public static Jedis REDIS;
    public static FaktoryWrapper FAKTORY;
    private static JedisPool pool;

    @Override
    public void onEnable() {
        try {
             pool = new JedisPool(new JedisPoolConfig(),
                    getConfig().getString("redis-host"), getConfig().getInt("redis-port", 7379), 1000,
                    getConfig().getString("redis-pass"));
            REDIS = pool.getResource();

            FAKTORY = new FaktoryWrapper(this);
            RemoteResponseHandler handler = new RemoteResponseHandler(this);
            handler.register(new ChatMessage());
            handler.register(new BlockModifyMessage());



            drink = Drink.get(this);
            languageManager = new LanguageManager(
                    this,                                  // The plugin (used to get the languages bundled in the jar file)
                    "languages",                           // Folder where the languages are stored
                    getConfig().getString("languages"),     // The language to use indicated by the plugin user
                    "EN",                                  // The default language, expected to be shipped with the plugin and should be complete, fills in gaps in the user-selected language
                    Collections.singletonList("[Towns&Kings] ") // Chat prefix to use with Message#prefix(), could of course come from the config file
            );

            new TKPlayerSenderProvider(this).bind(drink);
            new TKPlayerProvider(this).bind(drink);
            new TownProvider(this).bind(drink);
            new PlotSettingsProvider().bind(drink);

            drink.register(new TKCommands(this), "tk", "townskings");

            drink.registerCommands();

            Blocks.register(this, new HashMap<>());
            Items.register(this, new HashMap<>());
            recipesHandler = new RecipesHandler(this);
            modeHandler = new ModeHandler(this);

            getServer().getPluginManager().registerEvents(new TKPlayerListener(this), this);
            getServer().getPluginManager().registerEvents(new TKItemListener(this), this);
            getServer().getPluginManager().registerEvents(new TKBlockListener(this), this);

            this.getServer().getWorlds().forEach((w) -> {
                REDIS.sadd("worlds", w.getUID().toString());
                REDIS.hset("world:" + w.getUID().toString() + ":self", "name", w.getName());
            });
        } catch (Exception e) {
            Sentry.captureException(e);
            e.printStackTrace();
        }
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        try {
            recipesHandler.removeRecipes();
            modeHandler.disable();
            REDIS.close();
            pool.close();
        } catch (Exception e) {
            Sentry.captureException(e);
            e.printStackTrace();
        }
    }

    public String getVersion() {
        return getConfig().getString("version");
    }

    public ModeHandler getModeHandler() {
        return modeHandler;
    }
}
