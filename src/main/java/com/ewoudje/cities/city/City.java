package com.ewoudje.cities.city;

import com.ewoudje.cities.*;
import com.ewoudje.cities.api.OfflinePlayer;
import de.tr7zw.nbtapi.NBTCompound;
import de.tr7zw.nbtapi.NBTCompoundList;
import me.wiefferink.interactivemessenger.processing.Message;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class City {

    private final CityWorld world;

    private UUID id;

    private String name;
    private OfflinePlayer owner;
    private CityBlock foundingBlock;
    private List<OfflinePlayer> members;
    private List<OfflinePlayer> invited;

    private City(CityWorld world) {
        this.world = world;
    }

    public City(String name, CityPlayer owner, CityWorld world, CityBlock foundingBlock) {
        this.name = name;
        this.world = world;
        this.id = UUID.randomUUID();
        this.owner = owner.getOfflinePlayer();
        this.members = new ArrayList<>();
        this.foundingBlock = foundingBlock;
        this.invited = new ArrayList<>();
        join(owner);
    }

    public void save(NBTCompound compound) {
        compound.setUUID("id", id);
        compound.setString("name", name);
        owner.save(compound.addCompound("owner"));
        NBTCompoundList pNbt = compound.getCompoundList("players");

        for (OfflinePlayer player : members) {
            player.save(pNbt.addCompound());
        }

        NBTCompoundList iNbt = compound.getCompoundList("invited");

        for (OfflinePlayer player : invited) {
            player.save(iNbt.addCompound());
        }

        compound.setUUID("foundingBlock", foundingBlock.getId());

    }

    public static City load(NBTCompound compound, CityWorld world) {
        City city = new City(world);
        city.id = compound.getUUID("id");
        city.name = compound.getString("name");
        city.members = compound.getCompoundList("players")
                .stream().map(OfflinePlayer::fromCompound).collect(Collectors.toList());

        city.invited = compound.getCompoundList("invited")
                .stream().map(OfflinePlayer::fromCompound).collect(Collectors.toList());

        city.owner = OfflinePlayer.fromCompound(compound.getCompound("owner"));
        city.foundingBlock = world.getBlock(compound.getUUID("foundingBlock"));
        return city;
    }

    public String getName() {
        return name;
    }

    public void join(CityPlayer player) {
        invited.remove(player);
        player.setCity(this);
        this.members.add(player.getOfflinePlayer());
    }

    public void invite(CityPlayer invitee) {
        this.invited.add(invitee.getOfflinePlayer());
    }

    public void leave(CityPlayer player) {
        player.setCity(null);

        if (player.equals(owner))
            disband();

        this.members.remove(player.getOfflinePlayer());
    }

    public void disband() {
        world.disbandCity(this);
    }

    public boolean contains(OfflinePlayer offlinePlayer) {
        return members.contains(offlinePlayer);
    }

    public CityWorld getWorld() {
        return world;
    }

    public List<OfflinePlayer> getMembers() {
        return members;
    }

    public List<CityPlayer> getOnlineMembers(Cities plugin) {
        return plugin.getServer().getOnlinePlayers().stream()
                .map(plugin::getPlayer).filter((p) -> this.equals(p.getCity()))
                .collect(Collectors.toList());
    }

    public CityBlock getFoundingBlock() {
        return foundingBlock;
    }

    public List<Location> getClaimPoints() {
        return Collections.singletonList(foundingBlock.getBlock().getLocation());
    }

    public boolean isInvited(CityPlayer player) {
        return invited.contains(player);
    }

    public void broadcast(Message message, Cities plugin) {
        getOnlineMembers(plugin).forEach((p) -> p.send(message));
    }

    public void setOwner(OfflinePlayer owner) {
        this.owner = owner;
    }
}
