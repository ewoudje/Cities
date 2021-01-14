package com.ewoudje.townskings.datastore;

import com.ewoudje.townskings.TK;
import com.ewoudje.townskings.api.OfflinePlayer;
import com.ewoudje.townskings.api.town.Demographic;
import com.ewoudje.townskings.api.town.Plot;
import com.ewoudje.townskings.api.town.PlotSettings;
import com.ewoudje.townskings.api.town.Town;
import com.ewoudje.townskings.api.wrappers.TKBlock;
import com.ewoudje.townskings.api.wrappers.TKPlayer;
import com.ewoudje.townskings.api.wrappers.TKWorld;
import com.ewoudje.townskings.util.UUIDUtil;
import io.sentry.Sentry;
import org.bukkit.Bukkit;

import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class RedisTown implements Town {
    private final UUID uuid;

    public RedisTown(UUID uuid) {
        this.uuid = uuid;
    }

    @Override
    public String getName() {
        return TK.REDIS.hget("town:" + uuid.toString(), "name");
    }

    @Override
    public Set<OfflinePlayer> getMembers() {
        return TK.REDIS.smembers("town:" + uuid.toString() + ":members").stream()
                .map((s) -> new RedisOfflinePlayer(UUID.fromString(s))).collect(Collectors.toSet());
    }

    @Override
    public boolean isInvited(TKPlayer player) {
        return TK.REDIS.sismember("town:" + uuid.toString() + ":invited", player.getUID().toString());
    }

    @Override
    public void invite(TKPlayer player) {
        TK.REDIS.sadd("town:" + uuid.toString() + ":invited", player.getUID().toString());
        Sentry.addBreadcrumb("Player invited to a town!");
    }

    @Override
    public void join(TKPlayer player) {
        TK.REDIS.srem("town:" + uuid.toString() + ":invited", player.getUID().toString());
        TK.REDIS.sadd("town:" + uuid.toString() + ":members", player.getUID().toString());
        ((RedisPlayer) player).setTown(this);
        Sentry.addBreadcrumb("Player joined a town!");
    }

    @Override
    public void leave(TKPlayer player) {
        TK.REDIS.srem("town:" + uuid.toString() + ":members", player.getUID().toString());
        ((RedisPlayer) player).setTown(null);
        Sentry.addBreadcrumb("Player left a town!");

        if (player.is(getFounder())) this.disband();
    }

    @Override
    public OfflinePlayer getFounder() {
        return UUIDUtil.fromString(TK.REDIS.hget("town:" + uuid.toString(), "founder")).map(OfflinePlayer::getFromUUID).get();
    }

    @Override
    public Set<Plot> getPlots() {
        return TK.REDIS.smembers("town:" + uuid.toString() + ":plots").stream()
                .map(UUID::fromString).map(RedisPlot::new).collect(Collectors.toSet());
    }

    @Override
    public Set<Demographic> getDemographics() {
        return TK.REDIS.hgetAll("town:" + uuid.toString() + ":demos").values()
                .stream().map(UUID::fromString).map(RedisDemographic::new).collect(Collectors.toSet());
    }

    @Override
    public void disband() {
        UUIDUtil.fromString(TK.REDIS.hget("town:" + uuid.toString(), "founding-block")).map(RedisBlock::new)
                .ifPresent(RedisBlock::destroy);

        UUIDUtil.fromString(TK.REDIS.hget("town:" + uuid.toString(), "world")).map(RedisWorld::new)
                .ifPresent(world -> world.removeTown(TK.REDIS.hget("town:" + uuid.toString(), "name")));

        getPlots().forEach(Plot::dispose);
        getPlotSettings().forEach(PlotSettings::dispose);
        getDemographics().forEach(Demographic::dispose);

        TK.REDIS.del("town:" + uuid.toString());
        TK.REDIS.del("town:" + uuid.toString() + ":members");
        TK.REDIS.del("town:" + uuid.toString() + ":invited");
        TK.REDIS.del("town:" + uuid.toString() + ":plots");
        TK.REDIS.del("town:" + uuid.toString() + ":plotsettings");
        TK.REDIS.del("town:" + uuid.toString() + ":demos");

        Sentry.addBreadcrumb("Disbanded town!");
    }

    @Override
    public Set<PlotSettings> getPlotSettings() {
        return TK.REDIS.hgetAll("town:" + uuid.toString() + ":plotsettings").values().stream()
                .map(UUID::fromString).map(RedisPlotSettings::new).collect(Collectors.toSet());
    }

    @Override
    public UUID getUID() {
        return uuid;
    }

    @Override
    public void setFounder(OfflinePlayer player) {
        TK.REDIS.hset("town:" + uuid.toString(), "founder", player.getUniqueId().toString());
    }

    public void addPlot(Plot plot) {
        TK.REDIS.sadd("town:" + uuid.toString() + ":plots", plot.getUID().toString());
    }

    public void addPlotSettings(PlotSettings settings) {
        TK.REDIS.hset("town:" + uuid.toString() + ":plotsettings", settings.getName(), settings.getUID().toString());
    }

    private void addDemographic(Demographic demo) {
        TK.REDIS.hset("town:" + uuid.toString() + ":demos", demo.getName(), demo.getUID().toString());
    }

    public static Set<Town> getAllTowns() {
        return Bukkit.getWorlds().stream()
                .flatMap((w) -> TK.REDIS.hgetAll("world:" + w.getUID().toString() + ":towns").values().stream())
                .map((s) -> new RedisTown(UUID.fromString(s))).collect(Collectors.toSet());
    }

    public static Town create(String name, TKPlayer player, TKBlock founding, TKWorld world) {
        UUID uuid = UUID.randomUUID();

        TK.REDIS.hset("town:" + uuid.toString(), "name", name);
        TK.REDIS.hset("town:" + uuid.toString(), "world", world.getUID().toString());
        TK.REDIS.hset("town:" + uuid.toString(), "founding-block", founding.getUID().toString());
        TK.REDIS.hset("town:" + uuid.toString(), "founder", player.getUID().toString());

        Sentry.addBreadcrumb("Player created a town!");

        ((RedisWorld) world).addTown(name, uuid);

        RedisTown town = new RedisTown(uuid);

        Demographic mayor = RedisDemographic.createDemographic("mayor", town);
        mayor.addMember(player);
        town.addDemographic(mayor);

        Demographic def = RedisDemographic.createDemographic("default", town);
        town.addDemographic(def);

        town.addPlotSettings(RedisPlotSettings.createPlotSettings("default", 10000,
                town, Set.of(def), Set.of(mayor)));

        town.join(player);

        return town;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RedisTown redisTown = (RedisTown) o;
        return Objects.equals(uuid, redisTown.uuid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid);
    }
}
