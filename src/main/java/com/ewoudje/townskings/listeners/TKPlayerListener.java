package com.ewoudje.townskings.listeners;

import com.ewoudje.townskings.api.TKPlugin;
import com.ewoudje.townskings.api.town.Plot;
import com.ewoudje.townskings.api.world.BlockPosition;
import com.ewoudje.townskings.api.world.ChunkPosition;
import com.ewoudje.townskings.api.wrappers.TKPlayer;
import com.ewoudje.townskings.datastore.RedisChunk;
import io.sentry.Sentry;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.*;

import java.util.List;

public class TKPlayerListener implements Listener {

    private final TKPlugin plugin;
    private final double claimSize;

    public TKPlayerListener(TKPlugin plugin) {
        this.plugin = plugin;
        claimSize = plugin.getConfig().getDouble("claim-size");
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent joinEvent) {
        try {
            //playerMap.put(joinEvent.getPlayer(),
            //        new TKPlayer(joinEvent.getPlayer(), plugin.getWorld(joinEvent.getPlayer().getWorld())));
        } catch (Exception ex) {
            Sentry.captureException(ex);
            ex.printStackTrace();
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent quitEvent) {
        try {
            //playerMap.remove(quitEvent.getPlayer());
        } catch (Exception ex) {
            Sentry.captureException(ex);
            ex.printStackTrace();
        }
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        /*


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
        */
    }

    @EventHandler
    public void onWorldSwitch(PlayerChangedWorldEvent e) {
        try {
            //plugin.getPlayer(e.getPlayer()).setWorld(plugin.getWorld(e.getPlayer().getWorld()));
        } catch (Exception ex) {
            Sentry.captureException(ex);
            ex.printStackTrace();
        }
    }


    @EventHandler
    public void onDestroy(BlockBreakEvent e) {
        try {
            if (!e.isCancelled()) {
                TKPlayer player = TKPlayer.wrap(e.getPlayer());
                e.setCancelled(cancelBlockChange(new BlockPosition(e.getBlock()), player));
            }
        } catch (Exception ex) {
            Sentry.captureException(ex);
            ex.printStackTrace();
        }
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent e) {
        try {
            if (!e.isCancelled()) {
                TKPlayer player = TKPlayer.wrap(e.getPlayer());
                e.setCancelled(cancelBlockChange(new BlockPosition(e.getBlock()), player));
            }

        } catch (Exception ex) {
            Sentry.captureException(ex);
            ex.printStackTrace();
        }
    }

    private boolean cancelBlockChange(BlockPosition pos, TKPlayer player) {
        List<Plot> plots = new RedisChunk(pos.getChunkPos()).getPlots();

        for (Plot p : plots) {
            BlockPosition start = p.getStartPosition();
            BlockPosition end = p.getEndPosition();

            if (pos.getX() > start.getX() && pos.getX() < end.getX() &&
                    pos.getZ() > start.getZ() && pos.getZ() < end.getZ()) {
                if (p.isInfiniteDepth() || (
                        pos.getY() > p.getStartPosition().getY() && pos.getY() < p.getEndPosition().getY()
                )) {
                    return !p.getSettings().getTown().equals(player.getTown());
                }
            }
        }

        return false;
    }


}
