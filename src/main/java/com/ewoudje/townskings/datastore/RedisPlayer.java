package com.ewoudje.townskings.datastore;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.ewoudje.townskings.TK;
import com.ewoudje.townskings.api.OfflinePlayer;
import com.ewoudje.townskings.api.town.Town;
import com.ewoudje.townskings.api.wrappers.TKPlayer;
import com.ewoudje.townskings.api.wrappers.TKWorld;
import com.ewoudje.townskings.util.UUIDUtil;
import me.wiefferink.interactivemessenger.generators.TellrawGenerator;
import me.wiefferink.interactivemessenger.parsers.YamlParser;
import me.wiefferink.interactivemessenger.processing.Message;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.InvocationTargetException;
import java.util.Objects;
import java.util.UUID;

public class RedisPlayer implements TKPlayer {
    private final Player player;
    private final TKWorld world;

    private RedisPlayer(Player player, TKWorld world) {
        this.player = player;
        this.world = world;
    }

    public static TKPlayer read(Player pla) {
        if (pla == null) return null;
        return new RedisPlayer(pla, new RedisWorld(pla.getWorld()));
    }

    public void setTown(Town town) {
        if (town == null) {
            TK.REDIS.hdel("player:" + player.getUniqueId().toString(), "town");
        } else {
            TK.REDIS.hset("player:" + player.getUniqueId().toString(), "town", town.getUID().toString());
        }
    }

    @Nullable
    public Town getTown() {
        return UUIDUtil.fromString(TK.REDIS.hget("player:" + player.getUniqueId().toString(), "town")).map(RedisTown::new).orElse(null);
    }

    @Nonnull
    public Player getPlayer() {
        return player;
    }

    @Nonnull
    public TKWorld getWorld() {
        return world;
    }

    @Nonnull
    public OfflinePlayer getOfflinePlayer() {
        return new RedisOfflinePlayer(player.getUniqueId());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;

        OfflinePlayer that;

        if (getClass() == o.getClass()) {
            that = ((com.ewoudje.townskings.api.wrappers.TKPlayer) o).getOfflinePlayer();
        } else if (OfflinePlayer.class == o.getClass()) {
            that = (OfflinePlayer) o;
        } else return false;

        return Objects.equals(getOfflinePlayer(), that);
    }

    @Override
    public int hashCode() {
        return Objects.hash(player);
    }

    public UUID getUID() {
        return player.getUniqueId();
    }

    @Override
    public boolean is(OfflinePlayer player) {
        return player.getUniqueId().equals(this.getUID());
    }

    public static void updateUsername(UUID uuid, String name) {
        TK.REDIS.hset("player:" + uuid.toString(), "name", name);
    }
}
