package com.ewoudje.cities.listeners;

import com.ewoudje.cities.Cities;
import com.ewoudje.cities.CityPlayer;
import com.ewoudje.cities.CityWorld;
import com.ewoudje.cities.city.City;
import me.wiefferink.interactivemessenger.generators.TellrawGenerator;
import me.wiefferink.interactivemessenger.parsers.YamlParser;
import me.wiefferink.interactivemessenger.processing.Message;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.*;

import java.util.Map;
import java.util.Optional;

public class CityPlayerListener implements Listener {

    private final Cities plugin;
    private final Map<Player, CityPlayer> playerMap;
    private final float claimSize2;

    public CityPlayerListener(Cities plugin, Map<Player, CityPlayer> playerMap) {
        this.plugin = plugin;
        this.playerMap = playerMap;
        float tmp = (float) plugin.getConfig().getDouble("claim-size");
        claimSize2 = tmp * tmp;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent joinEvent) {
        playerMap.put(joinEvent.getPlayer(),
                new CityPlayer(joinEvent.getPlayer(), plugin.getWorld(joinEvent.getPlayer().getWorld())));
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent quitEvent) {
        playerMap.remove(quitEvent.getPlayer());
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        CityPlayer player = plugin.getPlayer(e.getPlayer());
        City had = player.getVisitCity();

        Optional<City> city = inCity(player.getWorld(), player.getPlayer().getLocation());

        player.setVisitCity(city.orElse(null));

        if (had != null && !city.isPresent())
            player.actionBar(Message.fromKey("left-city")
                    .replacements(player.getPlayer().getName(), had.getName()));
        else if (had == null && city.isPresent())
            player.actionBar(Message.fromKey("entered-city")
                    .replacements(player.getPlayer().getName(), player.getVisitCity().getName()));

    }

    @EventHandler
    public void onWorldSwitch(PlayerChangedWorldEvent e) {
        plugin.getPlayer(e.getPlayer()).setWorld(plugin.getWorld(e.getPlayer().getWorld()));
    }


    @EventHandler
    public void onDestroy(BlockBreakEvent e) {
        CityPlayer player = plugin.getPlayer(e.getPlayer());

        Optional<City> city = inCity(player.getWorld(), e.getBlock().getLocation());

        if (city.isPresent() && !city.get().equals(player.getCity()))
            e.setCancelled(true);
    }

    @EventHandler
    public void onDestroy(BlockPlaceEvent e) {
        CityPlayer player = plugin.getPlayer(e.getPlayer());

        Optional<City> city = inCity(player.getWorld(), e.getBlock().getLocation());

        if (city.isPresent() && !city.get().equals(player.getCity()))
            e.setCancelled(true);
    }

    private Optional<City> inCity(CityWorld world, Location loc) {
        if (!world.getWorld().equals(loc.getWorld()))
            return Optional.empty();

        Location l = loc.clone();
        l.setY(0);
        return world.getCities().stream()
                .filter((c) -> c.getClaimPoints()
                        .stream().anyMatch((p) -> {
                            Location p2 = p.clone();

                            p2.setY(0);

                            return l.distanceSquared(p2) < claimSize2;
                        }))
                .findAny();
    }


}
