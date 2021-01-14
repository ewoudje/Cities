package com.ewoudje.townskings.api.town;

import com.ewoudje.townskings.api.OfflinePlayer;
import com.ewoudje.townskings.api.wrappers.TKPlayer;

import java.util.Set;
import java.util.UUID;

public interface Demographic {

    static boolean contains(Set<Demographic> allowed, TKPlayer p) {
        return allowed.stream()
                .flatMap((f) -> f.getMembers().stream())
                .noneMatch(p.getOfflinePlayer()::equals);
    }

    String getName();

    void addMember(TKPlayer player);

    Town getTown();

    Set<OfflinePlayer> getMembers();

    UUID getUID();

    void dispose();
}
