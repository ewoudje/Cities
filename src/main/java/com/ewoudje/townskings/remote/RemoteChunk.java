package com.ewoudje.townskings.remote;

import com.ewoudje.townskings.TK;
import com.ewoudje.townskings.api.town.Plot;
import com.ewoudje.townskings.api.world.ChunkPosition;
import com.ewoudje.townskings.api.wrappers.TKChunk;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class RemoteChunk implements TKChunk {
    private final ChunkPosition position;


    public RemoteChunk(ChunkPosition position) {
        this.position = position;
    }

    @Override
    public void addPlot(Plot plot) {
        TK.REDIS.zadd("chunk:" + position.getX() + ":" + position.getZ() + ":plots", plot.getPriority(), plot.getUID().toString());
    }

    @Override
    public List<Plot> getPlots() {
        return TK.REDIS.zrange("chunk:" + position.getX() + ":" + position.getZ() + ":plots", 0, -1).stream()
                .map(UUID::fromString)
                .map(RemotePlot::new)
                .collect(Collectors.toList());
    }

    @Override
    public void removePlot(Plot plot) {
        TK.REDIS.zrem("chunk:" + position.getX() + ":" + position.getZ() + ":plots", plot.getUID().toString());
    }
}
