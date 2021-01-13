package com.ewoudje.townskings.town.plot;

import com.ewoudje.townskings.api.town.Plot;
import com.ewoudje.townskings.api.town.PlotSettings;
import com.ewoudje.townskings.api.world.BlockPosition;
import de.tr7zw.nbtapi.NBTCompound;
import de.tr7zw.nbtapi.NBTListCompound;

import java.util.UUID;
import java.util.function.Function;

public class TKPlot implements Plot {
    private final BlockPosition start, end;
    private final boolean depth;
    private final PlotSettings settings;

    public TKPlot(BlockPosition start, BlockPosition end, boolean depth, PlotSettings settings) {
        this.start = start;
        this.end = end;
        this.depth = depth;
        this.settings = settings;
    }

    @Override
    public PlotSettings getSettings() {
        return settings;
    }

    @Override
    public BlockPosition getStartPosition() {
        return start;
    }

    @Override
    public BlockPosition getEndPosition() {
        return end;
    }

    @Override
    public boolean isInfiniteDepth() {
        return depth;
    }

    @Override
    public void save(NBTCompound compound) {
        start.save(compound.addCompound("start"));
        end.save(compound.addCompound("end"));
        compound.setBoolean("depth", depth);
        compound.setUUID("settings", settings.getId());
    }

    public static TKPlot fromCompound(NBTCompound compound, Function<UUID, PlotSettings> getSettings) {
        return new TKPlot(
                new BlockPosition(compound.getCompound("start")),
                new BlockPosition(compound.getCompound("end")),
                compound.getBoolean("depth"),
                getSettings.apply(compound.getUUID("settings"))
        );
    }
}
