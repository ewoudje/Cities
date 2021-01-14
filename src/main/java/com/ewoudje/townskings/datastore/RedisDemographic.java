package com.ewoudje.townskings.datastore;

import com.ewoudje.townskings.TK;
import com.ewoudje.townskings.api.OfflinePlayer;
import com.ewoudje.townskings.api.town.Demographic;
import com.ewoudje.townskings.api.town.Town;
import com.ewoudje.townskings.api.wrappers.TKPlayer;
import com.ewoudje.townskings.util.UUIDUtil;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class RedisDemographic implements Demographic {
    private final UUID uuid;
    private final boolean def;

    public RedisDemographic(UUID uuid) {
        this.uuid = uuid;
        def = "default".equals(TK.REDIS.hget("demo:" + uuid.toString(), "name"));
    }

    @Override
    public String getName() {
        if (!def)
            return TK.REDIS.hget("demo:" + uuid.toString(), "name");
        else
            return "default";
    }

    @Override
    public void addMember(TKPlayer player) {
        if (!def)
            TK.REDIS.sadd("demo:" + uuid.toString() + ":members", player.getUID().toString());
    }

    @Override
    public Town getTown() {
        return UUIDUtil.fromString(TK.REDIS.hget("demo:" + uuid.toString(), "town"))
                .map(RedisTown::new).orElse(null);
    }

    @Override
    public Set<OfflinePlayer> getMembers() {
        if (!def)
            return TK.REDIS.smembers("demo:" + uuid.toString() + ":members")
                .stream().map(UUID::fromString).map(OfflinePlayer::getFromUUID)
                .collect(Collectors.toSet());
        else
            return getTown().getMembers();
    }

    @Override
    public UUID getUID() {
        return uuid;
    }

    @Override
    public void dispose() {
        TK.REDIS.del("demo:" + uuid.toString());
        TK.REDIS.del("demo:" + uuid.toString() + ":members");
    }

    public static Demographic createDemographic(String name, Town town) {
        UUID uuid = UUID.randomUUID();

        TK.REDIS.hset("demo:" + uuid.toString(), "name", name);
        TK.REDIS.hset("demo:" + uuid.toString(), "town", town.getUID().toString());

        return new RedisDemographic(uuid);
    }
}
