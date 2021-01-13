package com.ewoudje.townskings.listeners;

import com.ewoudje.townskings.api.TKPlugin;
import com.ewoudje.townskings.api.world.BlockPosition;
import com.ewoudje.townskings.api.wrappers.TKPlayer;
import com.ewoudje.townskings.api.wrappers.TKWorld;
import com.ewoudje.townskings.town.Town;
import me.wiefferink.interactivemessenger.processing.Message;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.*;

import java.util.Map;
import java.util.Optional;

public class TKPlayerListener implements Listener {

    private final TKPlugin plugin;
    private final Map<Player, TKPlayer> playerMap;
    private final float claimSize2;

    public TKPlayerListener(TKPlugin plugin, Map<Player, TKPlayer> playerMap) {
        this.plugin = plugin;
        this.playerMap = playerMap;
        float tmp = (float) plugin.getConfig().getDouble("claim-size");
        claimSize2 = tmp * tmp;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent joinEvent) {
        playerMap.put(joinEvent.getPlayer(),
                new TKPlayer(joinEvent.getPlayer(), plugin.getWorld(joinEvent.getPlayer().getWorld())));
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent quitEvent) {
        playerMap.remove(quitEvent.getPlayer());
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        TKPlayer player = plugin.getPlayer(e.getPlayer());
        Town had = player.getVisitTown();

        Optional<Town> town = inTown(player.getWorld(), player.getPlayer().getLocation());

        player.setVisitTown(town.orElse(null));

        if (had != null && !town.isPresent())
            player.actionBar(Message.fromKey("left-town")
                    .replacements(player.getPlayer().getName(), had.getName()));
        else if (had == null && town.isPresent())
            player.actionBar(Message.fromKey("entered-town")
                    .replacements(player.getPlayer().getName(), player.getVisitTown().getName()));

        player.getFollowTile().setPos(new BlockPosition(player.getPlayer().getLocation()).getChunkPos(), player.getWorld());
    }

    @EventHandler
    public void onWorldSwitch(PlayerChangedWorldEvent e) {
        plugin.getPlayer(e.getPlayer()).setWorld(plugin.getWorld(e.getPlayer().getWorld()));
    }


    @EventHandler
    public void onDestroy(BlockBreakEvent e) {
        TKPlayer player = plugin.getPlayer(e.getPlayer());

        Optional<Town> town = inTown(player.getWorld(), e.getBlock().getLocation());

        if (town.isPresent() && !town.get().equals(player.getTown()))
            e.setCancelled(true);

        if (player.getWorld().getPlotAt(new BlockPosition(e.getBlock())) != null) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onDestroy(BlockPlaceEvent e) {
        TKPlayer player = plugin.getPlayer(e.getPlayer());

        Optional<Town> town = inTown(player.getWorld(), e.getBlock().getLocation());

        if (town.isPresent() && !town.get().equals(player.getTown()))
            e.setCancelled(true);
    }

    private Optional<Town> inTown(TKWorld world, Location loc) {
        if (!world.getWorld().equals(loc.getWorld()))
            return Optional.empty();

        Location l = loc.clone();
        l.setY(0);
        return world.getTKPlugin().stream()
                .filter((c) -> c.getClaimPoints()
                        .stream().anyMatch((p) -> {
                            Location p2 = p.clone();

                            p2.setY(0);

                            return l.distanceSquared(p2) < claimSize2;
                        }))
                .findAny();
    }


}
