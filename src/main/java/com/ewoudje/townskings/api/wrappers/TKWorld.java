package com.ewoudje.townskings.api.wrappers;

import com.ewoudje.townskings.api.town.Town;
import org.bukkit.World;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface TKWorld {

    Optional<Town> getTown(String name);

    Set<Town> getTowns();

    World getWorld();

    String getName();

    UUID getUID();
}
