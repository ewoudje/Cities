package com.ewoudje.townskings.mode;

import com.ewoudje.townskings.api.wrappers.TKPlayer;
import me.wiefferink.interactivemessenger.processing.Message;

public class ModeStatus {
    private Integer xp; //TODO xp
    private Message actionbar;

    public void setXp(Integer xp) {
        this.xp = xp;
    }

    public void setActionbar(Message actionbar) {
        this.actionbar = actionbar;
    }

    public void send(TKPlayer player) {
        if (actionbar != null)
            player.actionBar(actionbar);
    }
}
