package com.ewoudje.townskings.api.town;

import com.ewoudje.townskings.api.PlotOwner;

import java.util.Set;
import java.util.UUID;

public interface PlotSettings {

    String getName();

    Set<Demographic> allowedManage();

    Set<Demographic> allowedBuild();

    Town getTown();

    UUID getUID();

    double getPriority();

    void dispose();
}
