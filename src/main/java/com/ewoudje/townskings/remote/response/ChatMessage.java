package com.ewoudje.townskings.remote.response;

import com.ewoudje.townskings.TK;
import com.ewoudje.townskings.api.wrappers.TKPlayer;
import com.ewoudje.townskings.remote.RemoteTown;
import com.ewoudje.townskings.remote.RemoteWorld;
import com.ewoudje.townskings.util.SendUtil;
import com.ewoudje.townskings.util.UUIDUtil;
import me.wiefferink.interactivemessenger.processing.Message;
import org.bukkit.Bukkit;

import java.util.UUID;

public class ChatMessage implements RemoteResponseType<ChatMessage.BroadcastValues> {
    @Override
    public String getName() {
        return "chat";
    }

    @Override
    public boolean sync() {
        return true;
    }

    @Override
    public BroadcastValues requestValues(String loc) {
        BroadcastValues values = new BroadcastValues();

        values.message = TK.REDIS.hget(loc, "message");
        values.parameters = TK.REDIS.hget(loc, "params").split(":");
        values.targetType = TK.REDIS.hget(loc, "ttype");
        values.target = UUIDUtil.fromString(TK.REDIS.hget(loc, "target")).orElse(null);

        return values;
    }

    @Override
    public void execute(BroadcastValues input) {
        if (input.targetType == null || input.targetType.equals("all")) {
            SendUtil.broadcast(Message.fromKey(input.message).replacements((Object[]) input.parameters));
        } else if (input.targetType.equals("player")) {
            SendUtil.send(TKPlayer.wrap(Bukkit.getPlayer(input.target)), Message.fromKey(input.message).replacements((Object[]) input.parameters));
        } else if (input.targetType.equals("world")) {
            SendUtil.broadcast(new RemoteWorld(input.target), Message.fromKey(input.message).replacements((Object[]) input.parameters));
        } else if (input.targetType.equals("town")) {
            SendUtil.broadcast(new RemoteTown(input.target), Message.fromKey(input.message).replacements((Object[]) input.parameters));
        }
    }

    public static class BroadcastValues {
        private String message;
        private String[] parameters;
        private String targetType;
        private UUID target;
    }
}
