package com.ewoudje.townskings.api.town;

import com.ewoudje.townskings.api.OfflinePlayer;
import com.ewoudje.townskings.api.wrappers.TKPlayer;

import java.util.Set;
import java.util.UUID;

public interface Town {
    String getName();

    Set<OfflinePlayer> getMembers();

    boolean isInvited(TKPlayer player);

    void invite(TKPlayer player);

    void join(TKPlayer player);

    void leave(TKPlayer player);

    UUID getUID();

    void setFounder(OfflinePlayer offlinePlayer);

    OfflinePlayer getFounder();

    Set<Plot> getPlots();

    Set<Demographic> getDemographics();

    void disband();

    Set<PlotSettings> getPlotSettings();
}