package com.ewoudje.townskings.datastore;

import com.ewoudje.townskings.TK;
import com.ewoudje.townskings.api.town.Plot;
import com.ewoudje.townskings.api.town.PlotSettings;
import com.ewoudje.townskings.api.world.BlockPosition;
import com.ewoudje.townskings.util.UUIDUtil;

import java.util.UUID;

public class RedisPlot implements Plot {
    private final UUID uuid;

    public RedisPlot(UUID uuid) {
        this.uuid = uuid;
    }


    @Override
    public PlotSettings getSettings() {
        return UUIDUtil.fromString(TK.REDIS.hget("plot:" + uuid.toString(), "settings"))
                .map(RedisPlotSettings::new).orElse(null);
    }

    @Override
    public BlockPosition getStartPosition() {
        return new BlockPosition(
                Integer.parseInt(TK.REDIS.hget("plot:" + uuid.toString(), "xS")),
                Integer.parseInt(TK.REDIS.hget("plot:" + uuid.toString(), "yS")),
                Integer.parseInt(TK.REDIS.hget("plot:" + uuid.toString(), "zS"))
        );
    }

    @Override
    public BlockPosition getEndPosition() {
        return new BlockPosition(
                Integer.parseInt(TK.REDIS.hget("plot:" + uuid.toString(), "xE")),
                Integer.parseInt(TK.REDIS.hget("plot:" + uuid.toString(), "yE")),
                Integer.parseInt(TK.REDIS.hget("plot:" + uuid.toString(), "zE"))
        );
    }

    @Override
    public boolean isInfiniteDepth() {
        return Boolean.parseBoolean(TK.REDIS.hget("plot:" + uuid.toString(), "depth"));
    }

    @Override
    public UUID getUID() {
        return uuid;
    }

    @Override
    public double getPriority() {
        return getSettings().getPriority();
    }

    @Override
    public void dispose() {
        TK.REDIS.del("plot:" + uuid.toString());
    }

    public static RedisPlot createPlot(BlockPosition start, BlockPosition end, RedisPlotSettings settings, boolean depth) {
        UUID uuid = UUID.randomUUID();

        int xS = Math.min(start.getX(), end.getX());
        int yS = Math.min(start.getY(), end.getY());
        int zS = Math.min(start.getZ(), end.getZ());

        int xE = Math.max(start.getX(), end.getX());
        int yE = Math.max(start.getY(), end.getY());
        int zE = Math.max(start.getZ(), end.getZ());

        TK.REDIS.hset("plot:" + uuid.toString(), "settings", settings.getUID().toString());
        TK.REDIS.hset("plot:" + uuid.toString(), "xS", String.valueOf(xS));
        TK.REDIS.hset("plot:" + uuid.toString(), "yS", String.valueOf(yS));
        TK.REDIS.hset("plot:" + uuid.toString(), "zS", String.valueOf(zS));
        TK.REDIS.hset("plot:" + uuid.toString(), "xE", String.valueOf(xE));
        TK.REDIS.hset("plot:" + uuid.toString(), "yE", String.valueOf(yE));
        TK.REDIS.hset("plot:" + uuid.toString(), "zE", String.valueOf(zE));
        TK.REDIS.hset("plot:" + uuid.toString(), "depth", depth ? "true" : "false");

        RedisPlot plot = new RedisPlot(uuid);

        ((RedisTown)settings.getTown()).addPlot(plot);

        return plot;
    }
}
