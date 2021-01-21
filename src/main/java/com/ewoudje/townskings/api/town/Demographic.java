package com.ewoudje.townskings.api.town;

import com.ewoudje.townskings.api.OfflinePlayer;
import com.ewoudje.townskings.api.UObject;
import com.ewoudje.townskings.api.wrappers.TKPlayer;

import java.util.Set;

public interface Demographic extends UObject {

    static boolean contains(Set<Demographic> allowed, TKPlayer p) {
        return allowed.stream()
                .flatMap((f) -> f.getMembers().stream())
                .noneMatch(p.getOfflinePlayer()::equals);
    }

    String getName();

    void addMember(TKPlayer player);

    Town getTown();

    Set<OfflinePlayer> getMembers();

    void dispose();
}
