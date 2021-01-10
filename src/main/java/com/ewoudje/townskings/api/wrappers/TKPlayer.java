package com.ewoudje.townskings.api.wrappers;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.ewoudje.townskings.api.OfflinePlayer;
import com.ewoudje.townskings.town.Town;
import me.wiefferink.interactivemessenger.generators.TellrawGenerator;
import me.wiefferink.interactivemessenger.parsers.YamlParser;
import me.wiefferink.interactivemessenger.processing.Message;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.InvocationTargetException;
import java.util.Objects;

public class TKPlayer {
    private final Player player;
    private TKWorld world;
    private Town town;
    private Town visitTown;

    public TKPlayer(@Nonnull Player player, @Nonnull TKWorld world) {
        this.player = player;
        this.world = world;

        town = world.getTKPlugin().stream().filter((c) -> c.contains(getOfflinePlayer())).findAny()
                .orElse(null);
    }

    public void setTown(@Nullable Town town) {
        this.town = town;
    }

    @Nullable
    public Town getTown() {
        return town;
    }

    @Nonnull
    public Player getPlayer() {
        return player;
    }

    @Nonnull
    public TKWorld getWorld() {
        return world;
    }

    public void setWorld(@Nonnull TKWorld world) {
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
            that = ((TKPlayer) o).getOfflinePlayer();
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

    public void setVisitTown(Town town) {
        this.visitTown = town;
    }

    public Town getVisitTown() {
        return visitTown;
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
