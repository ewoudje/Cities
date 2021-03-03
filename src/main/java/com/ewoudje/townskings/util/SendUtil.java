package com.ewoudje.townskings.util;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.ewoudje.townskings.api.OfflinePlayer;
import com.ewoudje.townskings.api.town.Town;
import com.ewoudje.townskings.api.wrappers.TKPlayer;
import com.ewoudje.townskings.api.wrappers.TKWorld;
import me.wiefferink.interactivemessenger.generators.TellrawGenerator;
import me.wiefferink.interactivemessenger.parsers.YamlParser;
import me.wiefferink.interactivemessenger.processing.Message;
import org.bukkit.Bukkit;

import java.lang.reflect.InvocationTargetException;

public class SendUtil {


    public static void broadcast(Message message) {
        Bukkit.getOnlinePlayers().forEach(message::send);
    }

    public static void broadcast(TKWorld world, Message message) {
        world.getWorld().getPlayers().forEach(message::send);
    }

    public static void broadcast(Town town, Message message) {
        town.getMembers().stream().map(OfflinePlayer::getOnline).forEach(message::send);
    }

    public static void send(TKPlayer player, Message message) {
        if (player == null) return;
        message.send(player.getPlayer());
    }

    public static void showActionBar(TKPlayer player, Message message) {
        PacketContainer container = new PacketContainer(PacketType.Play.Server.TITLE);
        container.getTitleActions().write(0, EnumWrappers.TitleAction.ACTIONBAR);
        container.getChatComponents().write(0,
                WrappedChatComponent.fromJson(String.join("",
                        TellrawGenerator.generate(YamlParser.parse(message.doReplacements().getRaw())))));

        try {
            ProtocolLibrary.getProtocolManager().sendServerPacket(player.getPlayer(), container);
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}
