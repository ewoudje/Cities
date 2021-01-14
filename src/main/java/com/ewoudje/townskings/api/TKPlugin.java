package com.ewoudje.townskings.api;

import com.ewoudje.townskings.mode.ModeHandler;
import org.bukkit.plugin.Plugin;

import java.util.logging.Logger;

public interface TKPlugin extends Plugin {

    String getVersion();

    ModeHandler getModeHandler();

    Logger getLogger();
}
