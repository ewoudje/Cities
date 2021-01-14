package com.ewoudje.townskings.datastore;

import com.ewoudje.townskings.TK;
import com.ewoudje.townskings.api.town.Plot;
import com.ewoudje.townskings.api.world.ChunkPosition;
import com.ewoudje.townskings.api.wrappers.TKChunk;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class RedisChunk implements TKChunk {
    private final ChunkPosition position;


    public RedisChunk(ChunkPosition position) {
        this.position = position;
    }

    @Override
    public void addPlot(Plot plot) {
        TK.REDIS.zadd("chunk:" + position.getX() + ":" + position.getZ() + ":claims", plot.getPriority(), plot.getUID().toString());
    }

    @Override
    public List<Plot> getPlots() {
        return TK.REDIS.zrange("chunk:" + position.getX() + ":" + position.getZ() + ":claims", 0, -1).stream()
                .map(UUID::fromString)
                .map(RedisPlot::new)
                .collect(Collectors.toList());
    }

    @Override
    public void removePlot(Plot plot) {
        TK.REDIS.zrem("chunk:" + position.getX() + ":" + position.getZ() + ":claims", plot.getUID().toString());
    }
}
