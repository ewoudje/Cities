package com.ewoudje.townskings.datastore;

import com.ewoudje.townskings.TK;
import com.ewoudje.townskings.api.OfflinePlayer;
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
import java.util.Optional;
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

        if (player.is(getOwner())) this.disband();
    }

    @Override
    public OfflinePlayer getOwner() {
        return UUIDUtil.fromString(TK.REDIS.hget("town:" + uuid.toString(), "owner")).map(OfflinePlayer::getFromUUID).get();
    }

    @Override
    public Set<Plot> getPlots() {
        return TK.REDIS.smembers("town:" + uuid.toString() + ":plots").stream()
                .map(UUID::fromString).map(RedisPlot::new).collect(Collectors.toSet());
    }

    @Override
    public void disband() {
        UUIDUtil.fromString(TK.REDIS.hget("town:" + uuid.toString(), "founding-block")).map(RedisBlock::new)
                .ifPresent(RedisBlock::destroy);

        UUIDUtil.fromString(TK.REDIS.hget("town:" + uuid.toString(), "world")).map(RedisWorld::new)
                .ifPresent(world -> world.removeTown(TK.REDIS.hget("town:" + uuid.toString(), "name")));

        getPlots().forEach((p) -> {
            PlotSettings settings = p.getSettings();
            p.dispose();
            settings.dispose();
        });

        TK.REDIS.del("town:" + uuid.toString());
        TK.REDIS.del("town:" + uuid.toString() + ":members");
        TK.REDIS.del("town:" + uuid.toString() + ":invited");
        TK.REDIS.del("town:" + uuid.toString() + ":plots");

        Sentry.addBreadcrumb("Disbanded town!");
    }

    @Override
    public UUID getUID() {
        return uuid;
    }

    @Override
    public void setOwner(OfflinePlayer player) {
        TK.REDIS.hset("town:" + uuid.toString(), "owner", player.getUniqueId().toString());
    }

    public void addPlot(Plot plot) {
        TK.REDIS.sadd("town:" + uuid.toString() + ":plots", plot.getUID().toString());
    }

    public static Set<Town> getAllTowns() {
        return Bukkit.getWorlds().stream()
                .flatMap((w) -> TK.REDIS.hgetAll("world:" + w.getUID().toString() + ":towns").keySet().stream())
                .map((s) -> new RedisTown(UUID.fromString(s))).collect(Collectors.toSet());
    }

    public static Town create(String name, TKPlayer player, TKBlock founding, TKWorld world) {
        UUID uuid = UUID.randomUUID();

        TK.REDIS.hset("town:" + uuid.toString(), "name", name);
        TK.REDIS.hset("town:" + uuid.toString(), "world", world.getUID().toString());
        TK.REDIS.hset("town:" + uuid.toString(), "founding-block", founding.getUID().toString());
        TK.REDIS.hset("town:" + uuid.toString(), "owner", player.getUID().toString());

        Sentry.addBreadcrumb("Player created a town!");

        ((RedisWorld) world).addTown(name, uuid);

        Town town = new RedisTown(uuid);
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
