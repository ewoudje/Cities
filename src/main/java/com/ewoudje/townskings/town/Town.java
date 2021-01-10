package com.ewoudje.townskings.town;

import com.ewoudje.townskings.api.OfflinePlayer;
import com.ewoudje.townskings.api.TKPlugin;
import com.ewoudje.townskings.api.wrappers.TKBlock;
import com.ewoudje.townskings.api.wrappers.TKPlayer;
import com.ewoudje.townskings.api.wrappers.TKWorld;
import de.tr7zw.nbtapi.NBTCompound;
import de.tr7zw.nbtapi.NBTCompoundList;
import me.wiefferink.interactivemessenger.processing.Message;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class Town {

    private final TKWorld world;

    private UUID id;

    private String name;
    private OfflinePlayer owner;
    private TKBlock foundingBlock;
    private List<OfflinePlayer> members;
    private List<OfflinePlayer> invited;

    private Town(TKWorld world) {
        this.world = world;
    }

    public Town(String name, TKPlayer owner, TKWorld world, TKBlock foundingBlock) {
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

    public static Town load(NBTCompound compound, TKWorld world) {
        Town town = new Town(world);
        town.id = compound.getUUID("id");
        town.name = compound.getString("name");
        town.members = compound.getCompoundList("players")
                .stream().map(OfflinePlayer::fromCompound).collect(Collectors.toList());

        town.invited = compound.getCompoundList("invited")
                .stream().map(OfflinePlayer::fromCompound).collect(Collectors.toList());

        town.owner = OfflinePlayer.fromCompound(compound.getCompound("owner"));
        town.foundingBlock = world.getBlock(compound.getUUID("foundingBlock"));
        return town;
    }

    public String getName() {
        return name;
    }

    public void join(TKPlayer player) {
        invited.remove(player);
        player.setTown(this);
        this.members.add(player.getOfflinePlayer());
    }

    public void invite(TKPlayer invitee) {
        this.invited.add(invitee.getOfflinePlayer());
    }

    public void leave(TKPlayer player) {
        player.setTown(null);

        if (player.equals(owner))
            disband();

        this.members.remove(player.getOfflinePlayer());
    }

    public void disband() {
        world.disbandTown(this);
    }

    public boolean contains(OfflinePlayer offlinePlayer) {
        return members.contains(offlinePlayer);
    }

    public TKWorld getWorld() {
        return world;
    }

    public List<OfflinePlayer> getMembers() {
        return members;
    }

    public List<TKPlayer> getOnlineMembers(TKPlugin plugin) {
        return plugin.getServer().getOnlinePlayers().stream()
                .map(plugin::getPlayer).filter((p) -> this.equals(p.getTown()))
                .collect(Collectors.toList());
    }

    public TKBlock getFoundingBlock() {
        return foundingBlock;
    }

    public List<Location> getClaimPoints() {
        return Collections.singletonList(foundingBlock.getBlock().getLocation());
    }

    public boolean isInvited(TKPlayer player) {
        return invited.contains(player);
    }

    public void broadcast(Message message, TKPlugin plugin) {
        getOnlineMembers(plugin).forEach((p) -> p.send(message));
    }

    public void setOwner(OfflinePlayer owner) {
        this.owner = owner;
    }
}
