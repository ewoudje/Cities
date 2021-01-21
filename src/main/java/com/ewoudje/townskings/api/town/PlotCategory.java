package com.ewoudje.townskings.api.town;

import com.ewoudje.townskings.api.UObject;
import com.ewoudje.townskings.api.wrappers.TKPlayer;

public interface PlotCategory extends UObject {

    String getName();

    boolean isAllowed(TKPlayer player, Permission permission);

    Town getTown();

    double getPriority();

    void dispose();
}
