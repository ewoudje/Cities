package com.ewoudje.townskings.api.town;

import com.ewoudje.townskings.api.OfflinePlayer;
import com.ewoudje.townskings.api.wrappers.TKPlayer;

import java.util.Set;
import java.util.UUID;

public interface Demographic {

    String getName();

    void addMember(TKPlayer player);

    Town getTown();

    Set<OfflinePlayer> getMembers();

    UUID getUID();

    void dispose();
}
