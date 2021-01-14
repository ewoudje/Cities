package com.ewoudje.townskings.api.wrappers;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.ewoudje.townskings.TK;
import com.ewoudje.townskings.api.OfflinePlayer;
import com.ewoudje.townskings.api.wrappers.TKWorld;
import com.ewoudje.townskings.datastore.RedisOfflinePlayer;
import com.ewoudje.townskings.datastore.RedisPlayer;
import com.ewoudje.townskings.datastore.RedisTown;
import com.ewoudje.townskings.datastore.RedisWorld;
import com.ewoudje.townskings.api.town.Town;
import me.wiefferink.interactivemessenger.generators.TellrawGenerator;
import me.wiefferink.interactivemessenger.parsers.YamlParser;
import me.wiefferink.interactivemessenger.processing.Message;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public interface TKPlayer {

    static TKPlayer wrap(Player player) {
        return RedisPlayer.read(player); //TODO hmm?
    }

    @Nullable
    Town getTown();

    @Nonnull
    Player getPlayer();

    @Nonnull
    TKWorld getWorld();

    @Nonnull
    OfflinePlayer getOfflinePlayer();

    UUID getUID();

    boolean is(OfflinePlayer player);
}
