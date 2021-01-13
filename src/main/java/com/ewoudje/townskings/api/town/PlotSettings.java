package com.ewoudje.townskings.api.town;

import com.ewoudje.townskings.api.PlotOwner;
import com.ewoudje.townskings.town.Town;

import java.util.UUID;

public interface PlotSettings {

    String getName();

    PlotOwner getOwner();

    Town getTown();

    UUID getId();

}
