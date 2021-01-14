package com.ewoudje.townskings.user.commands;

import com.ewoudje.townskings.api.TKPlugin;
import com.ewoudje.townskings.api.wrappers.TKPlayer;
import com.ewoudje.townskings.datastore.RedisPlayer;
import com.jonahseguin.drink.CommandService;
import com.jonahseguin.drink.annotation.Sender;
import com.jonahseguin.drink.argument.CommandArg;
import com.jonahseguin.drink.parametric.DrinkProvider;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.List;

public class TKPlayerSenderProvider extends DrinkProvider<TKPlayer> {

    private final TKPlugin plugin;

    public TKPlayerSenderProvider(TKPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean doesConsumeArgument() {
        return false;
    }

    @Override
    public boolean isAsync() {
        return false;
    }

    @Nullable
    @Override
    public TKPlayer provide(@Nonnull CommandArg arg, @Nonnull List<? extends Annotation> annotations) throws CommandExitMessage {
        if (arg.isSenderPlayer()) {
            Player p = arg.getSenderAsPlayer();
            TKPlayer cp = RedisPlayer.read(p);

            if (annotations.stream().anyMatch((a) -> a.annotationType().equals(RequireTown.class))) {
                if (cp.getTown() != null) {
                    return cp;
                } else throw new CommandExitMessage("have-no-town");
            } else return cp;
        } else throw new CommandExitMessage("player-only");
    }

    @Override
    public String argumentDescription() {
        return "town player";
    }

    @Override
    public List<String> getSuggestions(@Nonnull String prefix) {
        return Collections.emptyList();
    }

    public void bind(CommandService drink) {
        drink.bind(TKPlayer.class).annotatedWith(Sender.class).annotatedWith(RequireTown.class).toProvider(this);
        drink.bind(TKPlayer.class).annotatedWith(Sender.class).toProvider(this);
    }
}
