package com.ewoudje.townskings.world;

import com.ewoudje.townskings.api.world.BlockPosition;
import com.ewoudje.townskings.api.world.ChangeBlockList;
import com.ewoudje.townskings.api.world.ChunkChange;
import org.apache.logging.log4j.util.TriConsumer;
import org.bukkit.Material;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public abstract class PerBlockChange implements ChangeBlockList {

    @Override
    public List<ChunkChange> getChunkChanges() {
        HashMap<BlockPosition, DynamicChunkChange> changes = new HashMap<>();

        forEach((x, y, z, material) -> {
            BlockPosition pos = new BlockPosition(x >> 4, y >> 4, z >> 4);

            DynamicChunkChange pc = changes.get(pos);
            DynamicChunkChange change = Objects.requireNonNullElseGet(pc, () -> new DynamicChunkChange(pos));

            change.put(x, y, z, material);

            if (pc == null)
                changes.put(pos, change);
            else
                changes.replace(pos, change);
        });

        return changes.values().stream().map(DynamicChunkChange::build).collect(Collectors.toList());
    }

    protected abstract void forEach(ForEachBlock forEach);

    protected interface ForEachBlock {
        void accept(int x, int y, int z, Material material);
    }

}
