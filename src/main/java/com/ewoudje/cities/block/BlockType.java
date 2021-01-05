package com.ewoudje.cities.block;

import com.ewoudje.cities.CityBlock;
import com.ewoudje.cities.CityPlayer;
import com.ewoudje.cities.CityWorld;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface BlockType {

    boolean onBreak(CityWorld world, CityPlayer player, CityBlock block);

    @Nullable
    BlockData createBlockData();

    @Nonnull
    String getName();
}
