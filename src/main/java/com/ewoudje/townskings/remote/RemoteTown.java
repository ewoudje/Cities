package com.ewoudje.townskings.remote;

import com.ewoudje.townskings.api.OfflinePlayer;
import com.ewoudje.townskings.api.UReference;
import com.ewoudje.townskings.api.town.Plot;
import com.ewoudje.townskings.api.town.PlotCategory;
import com.ewoudje.townskings.api.town.Town;
import com.ewoudje.townskings.api.wrappers.TKBlock;
import com.ewoudje.townskings.api.wrappers.TKPlayer;
import com.ewoudje.townskings.api.wrappers.TKWorld;
import com.ewoudje.townskings.remote.faktory.FaktoryPriority;
import io.sentry.Sentry;
import org.bukkit.Bukkit;

import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class RemoteTown implements Town, UReference {
    public final static RemoteHelper R = new RemoteHelper("Town", FaktoryPriority.MC);

    private final UUID uuid;

    public RemoteTown(UUID uuid) {
        this.uuid = uuid;
    }

    @Override
    public String getName() {
        return R.get(uuid, "name");
    }

    @Override
    public Set<OfflinePlayer> getMembers() {
        return R.getSet(uuid, "members", RemoteOfflinePlayer.class).collect(Collectors.toSet());
    }

    @Override
    public boolean isInvited(TKPlayer player) {
        return R.contains(uuid, "invited", player);
    }

    @Override
    public void invite(TKPlayer player) {
        R.addSet(uuid, "invited", player);
        Sentry.addBreadcrumb("Player invited to a town!");
    }

    @Override
    public void join(TKPlayer player) {
        R.remSet(uuid, "invited", player);
        R.execute(uuid, "Join", (i) -> true, player);
        //TK.REDIS.sadd("town:" + uuid.toString() + ":members", player.getUID().toString());
        ((RemotePlayer) player).setTown(this);
        Sentry.addBreadcrumb("Player joined a town!");
    }

    @Override
    public void leave(TKPlayer player) {
        R.execute(uuid, "Leave", (i) -> true, player);
        //R.remSet(uuid, "members", player.getUID().toString());
        //((RemotePlayer) player).setTown(null);
        //Sentry.addBreadcrumb("Player left a town!");
        //if (player.is(getFounder()))
        //    this.destroy();
        //else {
        //    Sentry.addBreadcrumb("Called Demo Update!");
        //}
    }

    @Override
    public OfflinePlayer getFounder() {
        return R.get(uuid, "founder", RemoteOfflinePlayer.class);
    }

    @Override
    public Set<Plot> getPlots() {
        return R.hValues(uuid, "plots", RemotePlot.class).collect(Collectors.toSet());
    }

    @Override
    public Set<PlotCategory> getPlotCategories() {
        return R.hValues(uuid, "plots", RemotePlotCategory.class).collect(Collectors.toSet());
    }

    @Override
    public Plot getPlot(String name) {
        return R.hGet(uuid, "plots", name, RemotePlot.class);
    }

    @Override
    public PlotCategory getPlotCategory(String name) {
        return R.hGet(uuid, "plot_categories", name, RemotePlotCategory.class);
    }

    @Override
    public void destroy() {
        R.execute(uuid, "Destroy", (i) -> true);
        Sentry.addBreadcrumb("Disbanded town!");
    }

    @Override
    public UUID getUID() {
        return uuid;
    }

    @Override
    public void setFounder(OfflinePlayer player) {
        R.set(uuid, "founder", player);
    }

    public static Set<Town> getAllTowns() {
        return Bukkit.getWorlds().stream()
                .flatMap((w) -> RemoteWorld.R.hValues(w.getUID(), "towns").stream())
                .map((s) -> new RemoteTown(UUID.fromString(s))).collect(Collectors.toSet());
    }

    public static Town create(String name, TKPlayer player, TKBlock founding, TKWorld world) {
        UUID uuid = UUID.randomUUID();

        R.execute(uuid, "Create", (i) -> i == 4, name, world, player, founding);

        Sentry.addBreadcrumb("Player created a town!");

        RemoteTown town = new RemoteTown(uuid);

        return town;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RemoteTown remoteTown = (RemoteTown) o;
        return Objects.equals(uuid, remoteTown.uuid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid);
    }
}
