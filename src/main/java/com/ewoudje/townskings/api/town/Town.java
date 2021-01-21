package com.ewoudje.townskings.api.town;

import com.ewoudje.townskings.api.OfflinePlayer;
import com.ewoudje.townskings.api.UObject;
import com.ewoudje.townskings.api.wrappers.TKPlayer;

import java.util.Set;

public interface Town extends UObject {
    String getName();

    Set<OfflinePlayer> getMembers();

    boolean isInvited(TKPlayer player);

    void invite(TKPlayer player);

    void join(TKPlayer player);

    void leave(TKPlayer player);

    void setFounder(OfflinePlayer offlinePlayer);

    OfflinePlayer getFounder();

    Set<Plot> getPlots();

    Plot getPlot(String name);

    Set<PlotCategory> getPlotCategories();

    PlotCategory getPlotCategory(String name);

    void destroy();
}
