package com.ewoudje.townskings.version.v1_16_R3;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.BlockPosition;
import com.ewoudje.townskings.api.world.ChunkChange;
import com.ewoudje.townskings.api.wrappers.TKPlayer;
import com.ewoudje.townskings.api.wrappers.TKWorld;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.server.v1_16_R3.*;
import org.bukkit.Chunk;
import org.bukkit.craftbukkit.v1_16_R3.CraftChunk;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;

import java.lang.reflect.Field;
import java.nio.ByteBuffer;

public class ChunkUtil {

    private static Field pallete;

    static {
        try {
            pallete = ChunkSection.class.getDeclaredField("blockIds");
            pallete.setAccessible(true);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    public static void chunkSend(Chunk chunk, TKPlayer player) {
        ((CraftPlayer) player.getPlayer()).getHandle().playerConnection.sendPacket(
                new PacketPlayOutMapChunk(((CraftChunk) chunk).getHandle(), 65535));
    }

    public static PacketContainer chunkMake(Chunk chunk, int mask) {
        return new PacketContainer(PacketType.Play.Server.MAP_CHUNK, new PacketPlayOutMapChunk(((CraftChunk) chunk).getHandle(), mask));
    }

    public static byte[] modifySection(byte[] sb, int y, ChunkChange c) {
        PacketDataSerializer s = new PacketDataSerializer(Unpooled.wrappedBuffer(sb));
        short nonEmpty = s.readShort();
        ChunkSection section = new ChunkSection(y, nonEmpty, nonEmpty, s.readByte());
        //TODO IMPLMENT THIS CRAP
        ChunkChange.ChangesList list = c.getChanges();

        do {
            BlockPosition position = list.relative();
            section.setType(position.getX(), position.getY(), position.getZ(),
                    (IBlockData) list.blockData().getHandle());
        } while(list.next());

        section.recalcBlockCounts();
        PacketDataSerializer data = new PacketDataSerializer(Unpooled.buffer());
        section.b(data);

        byte[] bytes = new byte[data.readableBytes()];
        data.readBytes(bytes);

        return bytes;
    }
}
