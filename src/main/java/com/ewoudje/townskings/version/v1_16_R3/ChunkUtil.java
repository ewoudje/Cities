package com.ewoudje.townskings.version.v1_16_R3;

import com.ewoudje.townskings.api.wrappers.TKPlayer;
import net.minecraft.server.v1_16_R3.PacketPlayOutMapChunk;
import org.bukkit.Chunk;
import org.bukkit.craftbukkit.v1_16_R3.CraftChunk;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;

public class ChunkUtil {

    public static void chunkSend(Chunk chunk, TKPlayer player) {
        ((CraftPlayer) player.getPlayer()).getHandle().playerConnection.sendPacket(
                new PacketPlayOutMapChunk(((CraftChunk) chunk).getHandle(), 65535));
    }

}
