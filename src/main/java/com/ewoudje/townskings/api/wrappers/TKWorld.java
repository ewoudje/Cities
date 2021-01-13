package com.ewoudje.townskings.api.wrappers;

import com.ewoudje.townskings.api.town.Plot;
import com.ewoudje.townskings.api.world.BlockPosition;
import com.ewoudje.townskings.api.TKPlugin;
import com.ewoudje.townskings.api.block.BlockData;
import com.ewoudje.townskings.api.block.BlockType;
import com.ewoudje.townskings.api.world.ChunkPosition;
import com.ewoudje.townskings.api.world.Tile;
import com.ewoudje.townskings.api.world.TilePosition;
import com.ewoudje.townskings.block.Blocks;
import com.ewoudje.townskings.block.FoundingBlock;
import com.ewoudje.townskings.town.Town;
import com.ewoudje.townskings.world.Plotter;
import com.ewoudje.townskings.world.TKTile;
import de.bluecolored.bluemap.api.BlueMapAPI;
import de.bluecolored.bluemap.api.BlueMapMap;
import de.bluecolored.bluemap.api.BlueMapWorld;
import de.bluecolored.bluemap.api.marker.Marker;
import de.bluecolored.bluemap.api.marker.MarkerAPI;
import de.bluecolored.bluemap.api.marker.MarkerSet;
import de.tr7zw.nbtapi.NBTCompound;
import de.tr7zw.nbtapi.NBTCompoundList;
import de.tr7zw.nbtapi.NBTFile;
import de.tr7zw.nbtapi.NBTListCompound;
import me.wiefferink.interactivemessenger.processing.Message;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;


import javax.annotation.Nonnull;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class TKWorld {
    private final World world;
    private final TKPlugin plugin;
    private HashMap<UUID, TKBlock> blocksViaUUID;
    private HashMap<BlockPosition, TKBlock> blocksViaPos;
    private HashMap<TilePosition, Tile> tiles;
    private List<Town> towns;

    public TKWorld(World world, TKPlugin plugin) {
        this.world = world;
        this.plugin = plugin;
        load();
    }

    public List<Town> getTKPlugin() {
        return towns;
    }

    private void load() {
        NBTFile file;
        try {
            file = new NBTFile(new File(world.getWorldFolder(), "tk.dat"));
        } catch (IOException e) {
            e.printStackTrace();
            plugin.getLogger().severe("World " + world.getName() + " towns.dat could not be loaded!!!");
            return;
        }

        blocksViaPos = new HashMap<>();
        blocksViaUUID = new HashMap<>();

        for (NBTCompound c : file.getCompoundList("blocks")) {
            BlockPosition pos = new BlockPosition(c);
            BlockType type = Blocks.getBlockType(c.getString("type"));
            BlockData data = type.createBlockData();
            if (data != null)
                data.load(c);
            TKBlock block = new TKBlock(this, world.getBlockAt(pos.getX(), pos.getY(), pos.getZ()),
                    type, data, c.getUUID("id"));
            blocksViaPos.put(pos, block);
            blocksViaUUID.put(block.getId(), block);
        }

        towns = file.getCompoundList("towns").stream()
                .map((NBTListCompound compound) -> Town.load(compound, this)).collect(Collectors.toList());


        tiles = new HashMap<>(); //TODO load tiles from nbt
    }

    public void save() {
        NBTFile file;
        try {
            File f = new File(world.getWorldFolder(), "tk.dat");
            f.delete();
            file = new NBTFile(f);
        } catch (IOException e) {
            e.printStackTrace();
            plugin.getLogger().severe("World " + world.getName() + " tk.dat could not be saved!!!");
            return;
        }

        for (Town town : towns) {
            town.save(file.getCompoundList("towns").addCompound());
        }

        NBTCompoundList list = file.getCompoundList("blocks");
        for (TKBlock c : blocksViaUUID.values()) {
            c.save(list.addCompound());
        }

        try {
            file.save();
        } catch (IOException e) {
            e.printStackTrace();
            plugin.getLogger().severe("World " + world.getName() + " tk.dat could not be saved!!!");
        }

        BlueMapAPI.getInstance().ifPresent(api -> {
            BlueMapWorld mw = api.getWorld(world.getUID()).orElseThrow(() -> new NullPointerException("No map for world: " + world.getName()));
            BlueMapMap map = mw.getMaps().stream().filter((m) -> m.getId().equals(this.world.getName())).findAny().orElse(null);
            try {
                MarkerAPI m = api.getMarkerAPI();
                MarkerSet set = m.getMarkerSet("Towns")
                        .orElseGet(() -> m.createMarkerSet("Towns"));


                for (Marker mm : set.getMarkers()) {
                    if (towns.stream().noneMatch((c) -> c.getName().equals(mm.getLabel()))) {
                        set.removeMarker(mm);
                    }
                }

                for (Town c : towns) {
                    Optional<Marker> marker = set.getMarker(c.getName());
                    if (!marker.isPresent()) {
                        Location loc = c.getFoundingBlock().getBlock().getLocation();
                        set.createPOIMarker(c.getName(), map, loc.getX() + 0.5, loc.getY() + 0.5, loc.getZ() + 0.5);
                        //TODO claim shape?
                    }
                }

                m.save();

            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public void createTown(String name, TKPlayer player, TKBlock foundingBlock) {
        towns.add(new Town(name, player, this, foundingBlock));
    }

    public Town getTown(String town) {
        for (Town c : towns) {
            if (c.getName().equals(town))
                return c;
        }

        return null;
    }

    public void disbandTown(Town town) {
        if (town.getWorld() == this) {
            town.getOnlineMembers(plugin).forEach(town::leave);
            towns.remove(town);
            town.getFoundingBlock().remove();

            broadcast(Message.fromKey("broadcast-town-disband").replacements(town.getName()));
        } else town.disband();
    }

    public void broadcast(Message message) {
        message = message.prefix();
        world.getPlayers().forEach(message::send);
    }

    public TKBlock getBlock(Block block) {
        TKBlock result = blocksViaPos.get(new BlockPosition(block));

        if (result == null) result = new TKBlock(this, block, null, null, UUID.randomUUID());

        return result;
    }

    public TKBlock getBlock(UUID id) {
        return blocksViaUUID.get(id);
    }

    public TKBlock createBlock(TKBlock block, Class<? extends FoundingBlock> clazz) {
        if (block.getType() != null && block.getType().getClass() == clazz) return block;

        TKBlock result = Blocks.createBlock(block.getWorld(), block.getBlock(), clazz);

        blocksViaPos.put(new BlockPosition(result.getBlock()), result); //TODO what if already exists
        blocksViaUUID.put(result.getId(), result);

        return result;
    }

    public void remove(TKBlock block) {
        block.getBlock().setType(Material.AIR);
        this.blocksViaPos.remove(new BlockPosition(block.getBlock()));
        this.blocksViaUUID.remove(block.getId());
    }

    public World getWorld() {
        return world;
    }

    public Plot getPlotAt(BlockPosition position) {
        Tile result = tiles.get(TilePosition.fromBlockPos(position));
        if (result == null) return null;
        return result.getPlotAt(position.getX(), position.getY(), position.getZ());
    }

    public boolean claimPlot(Plot plot) {
        return Plotter.claim(plot, this, tiles);
    }

    @Nonnull
    public Tile getTile(TilePosition position) {
        return Optional.ofNullable(tiles.get(position)).orElseGet(
                () -> {
                    TKTile r = new TKTile(position);
                    tiles.put(position, r);
                    return r;
                });
    }

    public TKChunk getChunk(ChunkPosition position) {
        return getTile(position.intoTilePos()).getChunk(position); //TODO keep them and store them
    }
}
