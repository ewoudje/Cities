package com.ewoudje.cities;

import com.ewoudje.cities.block.BlockData;
import com.ewoudje.cities.block.BlockType;
import com.ewoudje.cities.block.Blocks;
import com.ewoudje.cities.block.FoundingBlock;
import com.ewoudje.cities.city.City;
import de.bluecolored.bluemap.api.BlueMapAPI;
import de.bluecolored.bluemap.api.BlueMapMap;
import de.bluecolored.bluemap.api.BlueMapWorld;
import de.bluecolored.bluemap.api.marker.Marker;
import de.bluecolored.bluemap.api.marker.MarkerAPI;
import de.bluecolored.bluemap.api.marker.MarkerSet;
import de.bluecolored.bluemap.api.marker.Shape;
import de.tr7zw.nbtapi.NBTCompound;
import de.tr7zw.nbtapi.NBTCompoundList;
import de.tr7zw.nbtapi.NBTFile;
import de.tr7zw.nbtapi.NBTListCompound;
import me.wiefferink.interactivemessenger.processing.Message;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;


import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class CityWorld {
    private final World world;
    private final Cities plugin;
    private HashMap<UUID, CityBlock> blocksViaUUID;
    private HashMap<BlockPosition, CityBlock> blocksViaPos;
    private List<City> cities;

    public CityWorld(World world, Cities plugin) {
        this.world = world;
        this.plugin = plugin;
        load();
    }

    public List<City> getCities() {
        return cities;
    }

    private void load() {
        NBTFile file;
        try {
            file = new NBTFile(new File(world.getWorldFolder(), "cities.dat"));
        } catch (IOException e) {
            e.printStackTrace();
            plugin.getLogger().severe("World " + world.getName() + " cities.dat could not be loaded!!!");
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
            CityBlock block = new CityBlock(this, world.getBlockAt(pos.getX(), pos.getY(), pos.getZ()),
                    type, data, c.getUUID("id"));
            blocksViaPos.put(pos, block);
            blocksViaUUID.put(block.getId(), block);
        }

        cities = file.getCompoundList("cities").stream()
                .map((NBTListCompound compound) -> City.load(compound, this)).collect(Collectors.toList());

    }

    public void save() {
        NBTFile file;
        try {
            File f = new File(world.getWorldFolder(), "cities.dat");
            f.delete();
            file = new NBTFile(f);
        } catch (IOException e) {
            e.printStackTrace();
            plugin.getLogger().severe("World " + world.getName() + " cities.dat could not be saved!!!");
            return;
        }

        for (City city : cities) {
            city.save(file.getCompoundList("cities").addCompound());
        }

        NBTCompoundList list = file.getCompoundList("blocks");
        for (CityBlock c : blocksViaUUID.values()) {
            c.save(list.addCompound());
        }

        try {
            file.save();
        } catch (IOException e) {
            e.printStackTrace();
            plugin.getLogger().severe("World " + world.getName() + " cities.dat could not be saved!!!");
        }

        BlueMapAPI.getInstance().ifPresent(api -> {
            BlueMapWorld mw = api.getWorld(world.getUID()).orElseThrow(() -> new NullPointerException("No map for world: " + world.getName()));
            BlueMapMap map = mw.getMaps().stream().filter((m) -> m.getId().equals("world")).findAny().orElse(null);
            try {
                MarkerAPI m = api.getMarkerAPI();
                MarkerSet set = m.getMarkerSet("Cities")
                        .orElseGet(() -> m.createMarkerSet("Cities"));

                for (City c : cities) {
                    Optional<Marker> marker = set.getMarker(c.getName());
                    if (!marker.isPresent()) {
                        Location loc = c.getFoundingBlock().getBlock().getLocation();
                        set.createPOIMarker(c.getName(), map, loc.getX(), loc.getY(), loc.getZ());
                        //TODO claim shape?
                    }
                }

                m.save();

            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public void createCity(String name, CityPlayer player, CityBlock foundingBlock) {
        cities.add(new City(name, player, this, foundingBlock));
    }

    public City getCity(String city) {
        for (City c : cities) {
            if (c.getName().equals(city))
                return c;
        }

        return null;
    }

    public void disbandCity(City city) {
        if (city.getWorld() == this) {
            city.getOnlineMembers(plugin).forEach(city::leave);
            cities.remove(city);
            city.getFoundingBlock().remove();

            broadcast(Message.fromKey("broadcast-city-disband").replacements(city.getName()));
        } else city.disband();
    }

    public void broadcast(Message message) {
        message = message.prefix();
        world.getPlayers().forEach(message::send);
    }

    public CityBlock getBlock(Block block) {
        CityBlock result = blocksViaPos.get(new BlockPosition(block));

        if (result == null) result = new CityBlock(this, block, null, null, UUID.randomUUID());

        return result;
    }

    public CityBlock getBlock(UUID id) {
        return blocksViaUUID.get(id);
    }

    public CityBlock createBlock(CityBlock block, Class<? extends FoundingBlock> clazz) {
        if (block.getType() != null && block.getType().getClass() == clazz) return block;

        CityBlock result = Blocks.createBlock(block.getWorld(), block.getBlock(), clazz);

        blocksViaPos.put(new BlockPosition(result.getBlock()), result); //TODO what if already exists
        blocksViaUUID.put(result.getId(), result);

        return result;
    }

    public void remove(CityBlock block) {
        block.getBlock().setType(Material.AIR);
        this.blocksViaPos.remove(new BlockPosition(block.getBlock()));
        this.blocksViaUUID.remove(block.getId());
    }
}
