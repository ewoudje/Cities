package com.ewoudje.townskings.remote;

import com.ewoudje.townskings.api.UReference;
import com.ewoudje.townskings.api.town.Plot;
import com.ewoudje.townskings.api.town.PlotCategory;
import com.ewoudje.townskings.api.world.BlockPosition;
import com.ewoudje.townskings.api.wrappers.TKWorld;
import com.ewoudje.townskings.remote.faktory.FaktoryPriority;
import com.ewoudje.townskings.util.PlotApplier;

import java.util.UUID;

public class RemotePlot implements Plot, UReference {
    public final static RemoteHelper R = new RemoteHelper("Plot", FaktoryPriority.MC);
    private final UUID uuid;

    public RemotePlot(UUID uuid) {
        this.uuid = uuid;
    }


    @Override
    public PlotCategory getSettings() {
        return R.get(uuid, "settings", RemotePlotCategory.class);
    }

    @Override
    public BlockPosition getStartPosition() {
        return new BlockPosition(
                Integer.parseInt(R.get(uuid, "xS")),
                Integer.parseInt(R.get(uuid, "yS")),
                Integer.parseInt(R.get(uuid, "zS"))
        );
    }

    @Override
    public BlockPosition getEndPosition() {
        return new BlockPosition(
                Integer.parseInt(R.get(uuid, "xE")),
                Integer.parseInt(R.get(uuid, "yE")),
                Integer.parseInt(R.get(uuid, "zE"))
        );
    }

    @Override
    public boolean isInfiniteDepth() {
        return !Boolean.parseBoolean(R.get(uuid, "depth"));
    }

    @Override
    public UUID getUID() {
        return uuid;
    }

    @Override
    public double getPriority() {
        return getSettings().getPriority();
    }

    @Override
    public void dispose() {
        PlotApplier.removePlot(this);
        R.delete(uuid);
    }

    public static RemotePlot createPlot(TKWorld world, String name, BlockPosition start, BlockPosition end, PlotCategory settings, boolean depth) {
        UUID uuid = UUID.randomUUID();

        int xS = Math.min(start.getX(), end.getX());
        int yS = Math.min(start.getY(), end.getY());
        int zS = Math.min(start.getZ(), end.getZ());

        int xE = Math.max(start.getX(), end.getX());
        int yE = Math.max(start.getY(), end.getY());
        int zE = Math.max(start.getZ(), end.getZ());

        R.execute(uuid, "Create", (i) -> i == 2 || i == 11, world, name, xS, yS, zS, xE, yE, zE, depth, settings); //TODO unique mask could be better

        return new RemotePlot(uuid);
    }
}
