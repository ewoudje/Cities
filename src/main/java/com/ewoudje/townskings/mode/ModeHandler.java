package com.ewoudje.townskings.mode;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.ewoudje.townskings.api.TKPlugin;
import com.ewoudje.townskings.api.wrappers.TKPlayer;
import com.ewoudje.townskings.api.mode.Mode;
import com.ewoudje.townskings.version.v1_16_R3.PacketUtil;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ModeHandler {

    private final TKPlugin plugin;
    private final HashMap<Player, ModeSystem> modes = new HashMap<>();

    public ModeHandler(TKPlugin plugin) {
        this.plugin = plugin;
        register();
    }

    public void goInto(TKPlayer player, Mode mode) {
        ModeSystem modeSystem;

        try {

            PacketContainer container = new PacketContainer(PacketType.Play.Server.ABILITIES);
            boolean clientMode = false;

            for (ModeSetting setting : mode.getSettings()) {
                switch (setting) {
                    case FLY:
                        container.getBooleans().write(1, true);
                        container.getBooleans().write(2, true);
                        break;
                    case INVULNERABLE:
                        container.getBooleans().write(0, true);
                        break;
                    case CLIENTMODE:
                        clientMode = true;
                        break;
                    case INSTA_DESTROY:
                        container.getBooleans().write(3, true);
                        break;
                    case ADVENTURE_MODE:
                        PacketContainer gamemode = new PacketContainer(PacketType.Play.Server.GAME_STATE_CHANGE);
                        PacketUtil.setGameMode(gamemode, GameMode.ADVENTURE);
                        ProtocolLibrary.getProtocolManager().sendServerPacket(player.getPlayer(), gamemode);
                        break;
                }
            }

            container.getFloat().write(0, 0.05f);
            container.getFloat().write(1, 0.1f);

            ProtocolLibrary.getProtocolManager().sendServerPacket(player.getPlayer(), container);

            modeSystem = new ModeSystem(clientMode, player, mode, plugin);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        modes.put(player.getPlayer(), modeSystem);
    }

    public void disable() {
        for (Player player : modes.keySet()) {
            disable(player);
        }
    }

    public void disable(Player player) {
        ModeSystem system = modes.get(player);

        player.setAllowFlight(player.getAllowFlight()); //Resend

        system.disable();
        modes.remove(player);
    }

    public void updateStatus(TKPlayer player) {
        ModeSystem system = modes.get(player.getPlayer());

        system.updateStatus();
    }

    public void register() {
        ModeSystem.register(plugin, this);
    }

    public ModeSystem get(Player player) {
        return modes.get(player);
    }

    public Set<Map.Entry<Player, ModeSystem>> getEntries() {
        return modes.entrySet();
    }

    public void updateInventory(TKPlayer player) {
        ModeSystem system = modes.get(player.getPlayer());

        system.updateInventory();
    }
}
