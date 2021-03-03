package com.ewoudje.townskings.api.block;

import com.ewoudje.townskings.api.Named;
import com.ewoudje.townskings.api.wrappers.TKBlock;
import com.ewoudje.townskings.api.wrappers.TKPlayer;
import com.ewoudje.townskings.api.wrappers.TKWorld;

import javax.annotation.Nullable;

public interface BlockType extends Named {

    boolean onBreak(TKWorld world, TKPlayer player, TKBlock block);

    @Nullable
    BlockData createBlockData();
}
