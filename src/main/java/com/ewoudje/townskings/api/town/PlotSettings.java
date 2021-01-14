package com.ewoudje.townskings.api.town;

import com.ewoudje.townskings.api.PlotOwner;

import java.util.UUID;

public interface PlotSettings {

    String getName();

    //PlotOwner getOwner(); //TODO ownership system

    Town getTown();

    UUID getUID();

    double getPriority();

    void dispose();
}
