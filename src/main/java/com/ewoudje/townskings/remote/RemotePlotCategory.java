package com.ewoudje.townskings.remote;

import com.ewoudje.townskings.api.UReference;
import com.ewoudje.townskings.api.town.Permission;
import com.ewoudje.townskings.api.town.PlotCategory;
import com.ewoudje.townskings.api.town.Town;
import com.ewoudje.townskings.api.wrappers.TKPlayer;
import com.ewoudje.townskings.remote.faktory.FaktoryPriority;

import java.util.UUID;

public class RemotePlotCategory implements PlotCategory, UReference {
    public final static RemoteHelper R = new RemoteHelper("PlotCategory", FaktoryPriority.MC);

    private final UUID uuid;

    public RemotePlotCategory(UUID uuid) {
        this.uuid = uuid;
    }

    @Override
    public String getName() {
        return R.get(uuid, "name");
    }

    @Override
    public boolean isAllowed(TKPlayer player, Permission permission) {
        return R.contains(uuid, "perms:" + permission.getUID().toString(), player);
    }

    @Override
    public Town getTown() {
        return R.get(uuid, "town", RemoteTown.class);
    }

    @Override
    public UUID getUID() {
        return uuid;
    }

    @Override
    public double getPriority() {
        return Double.parseDouble(R.get(uuid, "priority"));
    }

    @Override
    public void dispose() {
        R.delete(uuid);
    }

    public static RemotePlotCategory createPlotCategory(String name, double priority, Town town) {
        UUID uuid = UUID.randomUUID();

        R.execute(uuid, "Create", (i) -> i == 1 || i == 2, name, town, priority);

        return new RemotePlotCategory(uuid);
    }
}
