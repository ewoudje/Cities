package com.ewoudje.townskings.datastore;

import com.ewoudje.townskings.TK;
import com.ewoudje.townskings.api.town.Demographic;
import com.ewoudje.townskings.api.town.PlotSettings;
import com.ewoudje.townskings.api.town.Town;
import com.ewoudje.townskings.util.UUIDUtil;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class RedisPlotSettings implements PlotSettings {
    private final UUID uuid;

    public RedisPlotSettings(UUID uuid) {
        this.uuid = uuid;
    }

    @Override
    public String getName() {
        return TK.REDIS.hget("plot-settings:" + uuid.toString(), "name");
    }

    @Override
    public Set<Demographic> allowedManage() {
        return TK.REDIS.smembers("plot-settings:" + uuid.toString() + ":managers")
                .stream().map(UUID::fromString).map(RedisDemographic::new).collect(Collectors.toSet());
    }

    @Override
    public Set<Demographic> allowedBuild() {
        return TK.REDIS.smembers("plot-settings:" + uuid.toString() + ":builders")
                .stream().map(UUID::fromString).map(RedisDemographic::new).collect(Collectors.toSet());
    }

    @Override
    public Town getTown() {
        return UUIDUtil.fromString(TK.REDIS.hget("plot-settings:" + uuid.toString(), "town"))
                .map(RedisTown::new).orElse(null);
    }

    @Override
    public UUID getUID() {
        return uuid;
    }

    @Override
    public double getPriority() {
        return Double.parseDouble(TK.REDIS.hget("plot-settings:" + uuid.toString(), "priority"));
    }

    @Override
    public void dispose() {
        TK.REDIS.del("plot-settings:" + uuid.toString());
        TK.REDIS.del("plot-settings:" + uuid.toString() + ":managers");
        TK.REDIS.del("plot-settings:" + uuid.toString() + ":builders");
    }

    public static RedisPlotSettings createPlotSettings(String name, double priority, Town town,
                                                       Set<Demographic> allowedBuild, Set<Demographic> allowedManage) {
        UUID uuid = UUID.randomUUID();

        TK.REDIS.hset("plot-settings:" + uuid.toString(), "name", name);
        TK.REDIS.hset("plot-settings:" + uuid.toString(), "priority", String.valueOf(priority));
        TK.REDIS.hset("plot-settings:" + uuid.toString(), "town", town.getUID().toString());
        allowedManage.stream().map(Demographic::getUID)
                .forEach((m) -> TK.REDIS.sadd("plot-settings:" + uuid.toString() + ":managers", m.toString()));
        allowedBuild.stream().map(Demographic::getUID)
                .forEach((m) -> TK.REDIS.sadd("plot-settings:" + uuid.toString() + ":builders", m.toString()));

        return new RedisPlotSettings(uuid);
    }
}
