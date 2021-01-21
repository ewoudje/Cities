package com.ewoudje.townskings.remote;

import com.ewoudje.townskings.api.UReference;
import com.ewoudje.townskings.api.town.Town;
import com.ewoudje.townskings.api.wrappers.TKWorld;
import com.ewoudje.townskings.remote.faktory.FaktoryPriority;
import com.ewoudje.townskings.util.SendUtil;
import me.wiefferink.interactivemessenger.processing.Message;
import org.bukkit.Bukkit;
import org.bukkit.World;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class RemoteWorld implements TKWorld, UReference {
    public final static RemoteHelper R = new RemoteHelper("World", FaktoryPriority.MC);

    private final UUID uuid;

    public RemoteWorld(World world) {
        this.uuid = world.getUID();
    }

    public RemoteWorld(UUID uuid) {
        this.uuid = uuid;
    }

    public Optional<Town> getTown(String name) {
        return Optional.ofNullable(R.hGet(uuid, "towns", name, RemoteTown.class));
    }

    @Override
    public Set<Town> getTowns() {
        return R.hValues(uuid, "towns").stream()
                .map((s) -> new RemoteTown(UUID.fromString(s))).collect(Collectors.toSet());
    }

    @Override
    public World getWorld() {
        return Bukkit.getWorld(uuid);
    }

    @Override
    public String getName() {
        return R.get(uuid, "name");
    }

    @Override
    public UUID getUID() {
        return uuid;
    }

    public void removeTown(String name) {
        R.hRem(uuid, "towns", name);
        SendUtil.broadcast(this, Message.fromKey("broadcast-town-disband").replacements(name));
    }
}
