package com.ewoudje.townskings.api.wrappers;

import com.ewoudje.townskings.api.Named;
import com.ewoudje.townskings.api.UObject;
import com.ewoudje.townskings.api.town.Town;
import org.bukkit.World;

import java.util.Optional;
import java.util.Set;

public interface TKWorld extends UObject, Named {

    Optional<Town> getTown(String name);

    Set<Town> getTowns();

    World getWorld();
}
