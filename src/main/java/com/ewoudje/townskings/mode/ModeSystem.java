package com.ewoudje.townskings.mode;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.ewoudje.townskings.api.TKPlugin;
import com.ewoudje.townskings.api.wrappers.TKBlock;
import com.ewoudje.townskings.api.wrappers.TKPlayer;
import com.ewoudje.townskings.api.mode.Mode;
import com.ewoudje.townskings.version.v1_16_R3.ChunkUtil;
import com.ewoudje.townskings.version.v1_16_R3.PacketUtil;
import org.bukkit.Chunk;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.RayTraceResult;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public class ModeSystem {
    private final TKPlugin plugin;

    private final boolean clientMode;
    private final TKPlayer player;
    private final Mode mode;

    private Location startPos;
    private List<Chunk> modifiedChunks = new ArrayList<>();
    private int currentSlot;
    private Location clientLocation;
    private double playerHeight;

    public ModeSystem(boolean clientMode, TKPlayer player, Mode mode, TKPlugin plugin) {
        this.clientMode = clientMode;
        this.player = player;
        this.mode = mode;
        this.plugin = plugin;
        startPos = player.getPlayer().getLocation();
        currentSlot = player.getPlayer().getInventory().getHeldItemSlot();
        clientLocation = startPos.clone();
        playerHeight = 1.62;
        updateInventory();
    }

    public boolean isClientMode() {
        return clientMode;
    }

    public TKPlayer getPlayer() {
        return player;
    }

    public Mode getMode() {
        return mode;
    }

    public void disable() {
        modifiedChunks.forEach((c) -> ChunkUtil.chunkSend(c, player));

        player.getPlayer().teleport(startPos);
        player.getPlayer().updateInventory();

        PacketContainer gamemode = new PacketContainer(PacketType.Play.Server.GAME_STATE_CHANGE);
        PacketUtil.setGameMode(gamemode, player.getPlayer().getGameMode());

        try {
            ProtocolLibrary.getProtocolManager().sendServerPacket(player.getPlayer(), gamemode);
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    public static void register(TKPlugin plugin, ModeHandler modeHandler) {
        ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(plugin,
                ListenerPriority.NORMAL,
                PacketType.Play.Client.POSITION_LOOK,  PacketType.Play.Client.POSITION, PacketType.Play.Client.LOOK,
                PacketType.Play.Client.BLOCK_DIG, PacketType.Play.Client.USE_ITEM, PacketType.Play.Client.BLOCK_PLACE,
                PacketType.Play.Client.HELD_ITEM_SLOT, PacketType.Play.Client.ARM_ANIMATION, PacketType.Play.Client.ENTITY_ACTION,
                PacketType.Play.Client.WINDOW_CLICK, PacketType.Play.Client.TRANSACTION, PacketType.Play.Client.USE_ENTITY
        ) { //USE_ITEM IS SWAPPED WITH PLACE BLOCK?
            @Override
            public void onPacketReceiving(PacketEvent event) {
                ModeSystem system = modeHandler.get(event.getPlayer());

                if (system == null || !system.clientMode) return;
                event.setCancelled(true);

                if (event.getPacketType() == PacketType.Play.Client.HELD_ITEM_SLOT) {
                    system.currentSlot = event.getPacket().getIntegers().read(0);
                } else if (event.getPacketType() == PacketType.Play.Client.BLOCK_PLACE) {
                    plugin.getServer().getScheduler().callSyncMethod(plugin, () -> {
                        system.mode.onRightClick(system.currentSlot, modeHandler, system.player, system.lookingAt());
                        return null;
                    });
                } else if (event.getPacketType() == PacketType.Play.Client.ARM_ANIMATION) {
                    plugin.getServer().getScheduler().callSyncMethod(plugin, () -> {
                        system.mode.onLeftClick(system.currentSlot, modeHandler, system.player, system.lookingAt());
                        return null;
                    });
                } else if (event.getPacketType() == PacketType.Play.Client.POSITION ||
                        event.getPacketType() == PacketType.Play.Client.POSITION_LOOK ||
                        event.getPacketType() == PacketType.Play.Client.LOOK) {

                    if (event.getPacket().getBooleans().read(1)) {
                        system.clientLocation.setX(event.getPacket().getDoubles().read(0));
                        system.clientLocation.setY(event.getPacket().getDoubles().read(1) + system.playerHeight);
                        system.clientLocation.setZ(event.getPacket().getDoubles().read(2));
                    }

                    if (event.getPacket().getBooleans().read(2)) {
                        system.clientLocation.setYaw(event.getPacket().getFloat().read(0));
                        system.clientLocation.setPitch(event.getPacket().getFloat().read(1));
                    }
                } else if (event.getPacketType() == PacketType.Play.Client.ENTITY_ACTION) {
                    switch (event.getPacket().getPlayerActions().read(0)) { //TODO you can crawl in 1.16 soooo yeaa
                        case START_SNEAKING:
                            system.playerHeight = 1.32f;
                            break;
                        case STOP_SNEAKING:
                            system.playerHeight = 1.62;
                            break;
                    }
                } else if (event.getPacketType() == PacketType.Play.Client.WINDOW_CLICK) {
                    //TODO menu in inventory?

                    system.updateInventory();
                }
            }
        });
    }

    private TKBlock lookingAt() {
        RayTraceResult result = clientLocation.getWorld().rayTraceBlocks(clientLocation, clientLocation.getDirection(), 5, FluidCollisionMode.SOURCE_ONLY);

        if (result == null) return null;

        return player.getWorld().getBlock(result.getHitBlock());
    }

    public void updateInventory() {
        PacketContainer hand = new PacketContainer(PacketType.Play.Server.SET_SLOT);
        hand.getIntegers().write(0, -1);
        hand.getIntegers().write(1, -1);
        hand.getItemModifier().write(0, new ItemStack(Material.AIR));

        PacketContainer inventory = new PacketContainer(PacketType.Play.Server.WINDOW_ITEMS);
        inventory.getIntegers().write(0, 0); //Player inventory
        inventory.getItemListModifier().write(0, mode.getItemList());
        try {
            ProtocolLibrary.getProtocolManager().sendServerPacket(player.getPlayer(), inventory);
            ProtocolLibrary.getProtocolManager().sendServerPacket(player.getPlayer(), hand);
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    private void addModChunk(Chunk chunk) {
        if (!modifiedChunks.contains(chunk))
            modifiedChunks.add(chunk);
    }

    public void updateStatus() {
        ModeStatus status = mode.getStatus();

        if (status == null) return;

        status.send(player);
    }
}
