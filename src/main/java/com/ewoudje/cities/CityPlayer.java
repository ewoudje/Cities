package com.ewoudje.cities;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.ewoudje.cities.api.OfflinePlayer;
import com.ewoudje.cities.city.City;
import me.wiefferink.interactivemessenger.generators.TellrawGenerator;
import me.wiefferink.interactivemessenger.parsers.YamlParser;
import me.wiefferink.interactivemessenger.processing.Message;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.InvocationTargetException;
import java.util.Objects;
import java.util.stream.Collectors;

public class CityPlayer {
    private final Player player;
    private CityWorld world;
    private City city;
    private City visitCity;

    public CityPlayer(@Nonnull Player player, @Nonnull CityWorld world) {
        this.player = player;
        this.world = world;

        city = world.getCities().stream().filter((c) -> c.contains(getOfflinePlayer())).findAny()
                .orElse(null);
    }

    public void setCity(@Nullable City city) {
        this.city = city;
    }

    @Nullable
    public City getCity() {
        return city;
    }

    @Nonnull
    public Player getPlayer() {
        return player;
    }

    @Nonnull
    public CityWorld getWorld() {
        return world;
    }

    public void setWorld(@Nonnull CityWorld world) {
        this.world = world;
    }

    @Nonnull
    public OfflinePlayer getOfflinePlayer() {
        return OfflinePlayer.fromPlayer(player);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;

        OfflinePlayer that;

        if (getClass() == o.getClass()) {
            that = ((CityPlayer) o).getOfflinePlayer();
        } else if (OfflinePlayer.class == o.getClass()) {
            that = (OfflinePlayer) o;
        } else return false;

        return Objects.equals(getOfflinePlayer(), that);
    }

    @Override
    public int hashCode() {
        return Objects.hash(player);
    }

    public void send(Message mes) {
        mes.send(getPlayer());
    }

    public void setVisitCity(City city) {
        this.visitCity = city;
    }

    public City getVisitCity() {
        return visitCity;
    }

    public void actionBar(Message message) {
        PacketContainer container = new PacketContainer(PacketType.Play.Server.TITLE);
        container.getTitleActions().write(0, EnumWrappers.TitleAction.ACTIONBAR);
        container.getChatComponents().write(0,
                WrappedChatComponent.fromJson(String.join("",
                        TellrawGenerator.generate(YamlParser.parse(message.doReplacements().getRaw())))));

        try {
            ProtocolLibrary.getProtocolManager().sendServerPacket(player, container);
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}
