package com.ewoudje.cities.mode;

import com.ewoudje.cities.CityPlayer;
import me.wiefferink.interactivemessenger.processing.Message;
import org.bukkit.entity.Player;

public class ModeStatus {
    private Integer xp; //TODO xp
    private Message actionbar;

    public void setXp(Integer xp) {
        this.xp = xp;
    }

    public void setActionbar(Message actionbar) {
        this.actionbar = actionbar;
    }

    public void send(CityPlayer player) {
        if (actionbar != null)
            player.actionBar(actionbar);
    }
}
