package com.ewoudje.cities.commands;

import me.wiefferink.interactivemessenger.processing.Message;
import org.bukkit.command.CommandSender;

public class CommandExitMessage extends com.jonahseguin.drink.exception.CommandExitMessage {
    public CommandExitMessage(String message) {
        super(message);
    }

    @Override
    public void print(CommandSender sender) {
        Message.fromKey(getMessage()).send(sender);
    }
}
