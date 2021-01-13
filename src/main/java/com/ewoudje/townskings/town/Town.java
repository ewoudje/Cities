package com.ewoudje.townskings.town;

import com.ewoudje.townskings.api.OfflinePlayer;
import com.ewoudje.townskings.api.PlotOwner;
import com.ewoudje.townskings.api.TKPlugin;
import com.ewoudje.townskings.api.town.Plot;
import com.ewoudje.townskings.api.town.PlotSettings;
import com.ewoudje.townskings.api.wrappers.TKBlock;
import com.ewoudje.townskings.api.wrappers.TKPlayer;
import com.ewoudje.townskings.api.wrappers.TKWorld;
import com.ewoudje.townskings.town.plot.TKPlot;
import de.tr7zw.nbtapi.NBTCompound;
import de.tr7zw.nbtapi.NBTCompoundList;
import de.tr7zw.nbtapi.NBTListCompound;
import me.wiefferink.interactivemessenger.processing.Message;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class Town implements PlotOwner {

    private final TKWorld world;

    private UUID id;

    private String name;
    private OfflinePlayer owner;
    private TKBlock foundingBlock;
    private List<OfflinePlayer> members;
    private List<OfflinePlayer> invited;
    private List<Plot> plots;

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
        this.plots = new ArrayList<>();
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
        NBTCompoundList plNbt = compound.getCompoundList("plots");

        for (Plot p : plots) {
            p.save(plNbt.addCompound());
        }

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
        town.plots = compound.getCompoundList("plots")
                .stream().map((NBTListCompound c) -> TKPlot.fromCompound(c, (u) -> new PlotSettings() {
                    @Override
                    public String getName() {
                        return "WOW";
                    }

                    @Override
                    public PlotOwner getOwner() {
                        return town;
                    }

                    @Override
                    public Town getTown() {
                        return town;
                    }

                    @Override
                    public UUID getId() {
                        return u;
                    }
                })).collect(Collectors.toList());

        for (Plot plot : town.plots) {
            world.claimPlot(plot);
        }

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

    @Override
    public void onPlotAdd(Plot plot) {
        if (this.plots.contains(plot)) return;

        this.plots.add(plot);
    }
}
