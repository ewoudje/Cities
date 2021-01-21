package com.ewoudje.townskings.remote;

import com.ewoudje.townskings.TK;
import com.ewoudje.townskings.api.OfflinePlayer;
import com.ewoudje.townskings.api.UReference;
import com.ewoudje.townskings.api.town.Demographic;
import com.ewoudje.townskings.api.town.Town;
import com.ewoudje.townskings.api.wrappers.TKPlayer;
import com.ewoudje.townskings.remote.faktory.FaktoryPriority;
import com.ewoudje.townskings.util.UUIDUtil;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class RemoteDemographic implements Demographic, UReference {
    public final static RemoteHelper R = new RemoteHelper("Demographic", FaktoryPriority.MC);

    private final UUID uuid;
    private final boolean def;

    public RemoteDemographic(UUID uuid) {
        this.uuid = uuid;
        def = "default".equals(R.get(uuid, "name"));
    }

    @Override
    public String getName() {
        if (!def)
            return R.get(uuid, "name");
        else
            return "default";
    }

    @Override
    public void addMember(TKPlayer player) {
        //TODO is only allowed if it is allowed by the rules of the demographic
        //if (!def)
        //    TK.REDIS.sadd("demo:" + uuid.toString() + ":members", player.getUID().toString());
    }

    @Override
    public Town getTown() {
        return UUIDUtil.fromString(TK.REDIS.hget("demo:" + uuid.toString(), "town"))
                .map(RemoteTown::new).orElse(null);
    }

    @Override
    public Set<OfflinePlayer> getMembers() {
        if (!def)
            return R.getSet(uuid, "members")
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
        R.delete(uuid);
    }

    public static Demographic createDemographic(String name, Town town) {
        UUID uuid = UUID.randomUUID();

        R.set(uuid, "name", name);
        R.set(uuid, "town", town.getUID().toString());

        return new RemoteDemographic(uuid);
    }
}
