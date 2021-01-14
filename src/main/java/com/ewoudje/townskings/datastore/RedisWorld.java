package com.ewoudje.townskings.datastore;

import com.ewoudje.townskings.TK;
import com.ewoudje.townskings.api.town.Town;
import com.ewoudje.townskings.api.wrappers.TKWorld;
import com.ewoudje.townskings.util.SendUtil;
import me.wiefferink.interactivemessenger.processing.Message;
import org.bukkit.Bukkit;
import org.bukkit.World;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class RedisWorld implements TKWorld {
    private final UUID uuid;

    public RedisWorld(World world) {
        this.uuid = world.getUID();
    }

    public RedisWorld(UUID uuid) {
        this.uuid = uuid;
    }

    public Optional<Town> getTown(String name) {
        return Optional.ofNullable(TK.REDIS.hget("world:" + uuid.toString() + ":towns", name))
                .map((s) -> new RedisTown(UUID.fromString(s)));
    }

    @Override
    public Set<Town> getTowns() {
        return TK.REDIS.hgetAll("world:" + uuid.toString() + ":towns").values().stream()
                .map((s) -> new RedisTown(UUID.fromString(s))).collect(Collectors.toSet());
    }

    @Override
    public World getWorld() {
        return Bukkit.getWorld(uuid);
    }

    @Override
    public String getName() {
        return TK.REDIS.hget("world:" + this.uuid.toString(), "name");
    }

    @Override
    public UUID getUID() {
        return uuid;
    }

    public void addTown(String name, UUID uuid) {
        TK.REDIS.hset("world:" + this.uuid.toString() + ":towns", name, uuid.toString());
    }

    public void removeTown(String name) {
        TK.REDIS.hdel("world:" + this.uuid.toString() + ":towns", name);
        SendUtil.broadcast(this, Message.fromKey("broadcast-town-disband").replacements(name));
    }
}
