package com.ewoudje.townskings.version.v1_16_R3;

import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.BlockPosition;
import net.minecraft.server.v1_16_R3.PacketPlayInUseItem;
import net.minecraft.server.v1_16_R3.PacketPlayOutGameStateChange;
import net.minecraft.server.v1_16_R3.PacketPlayOutPosition;
import org.bukkit.GameMode;

import java.lang.reflect.Field;

public class PacketUtil {

    private static Field stateChangeReason;
    private static Field positionFlag;

    static {
        try {
            stateChangeReason = PacketPlayOutGameStateChange.class.getDeclaredField("m");
            stateChangeReason.setAccessible(true);
            positionFlag = PacketPlayOutPosition.class.getDeclaredField("f");
            positionFlag.setAccessible(true);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param container with TYPE USE_ITEM
     * @return correct block position
     */
    public static BlockPosition getBlockPosition(PacketContainer container) {
        return BlockPosition.getConverter().getSpecific(
                ((PacketPlayInUseItem) container.getHandle()).c().getBlockPosition()
        );
    }

    /**
     * @param container with TYPE GAME_STATE
     */
    public static void setGameMode(PacketContainer container, GameMode mode) {
        container.getFloat().write(0, (float) mode.getValue());
        try {
            stateChangeReason.set(container.getHandle(),
                    new PacketPlayOutGameStateChange.a(GameStateReason.GAMEMODE.getValue()));
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }



}
