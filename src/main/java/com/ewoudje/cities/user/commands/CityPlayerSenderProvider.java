package com.ewoudje.cities.user.commands;

import com.ewoudje.cities.Cities;
import com.ewoudje.cities.CityPlayer;
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

public class CityPlayerSenderProvider extends DrinkProvider<CityPlayer> {

    private final Cities plugin;

    public CityPlayerSenderProvider(Cities plugin) {
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
    public CityPlayer provide(@Nonnull CommandArg arg, @Nonnull List<? extends Annotation> annotations) throws CommandExitMessage {
        if (arg.isSenderPlayer()) {
            Player p = arg.getSenderAsPlayer();
            CityPlayer cp = plugin.getPlayer(p);

            if (annotations.stream().anyMatch((a) -> a.annotationType().equals(RequireCity.class))) {
                if (cp.getCity() != null) {
                    return cp;
                } else throw new CommandExitMessage("have-no-city");
            } else return cp;
        } else throw new CommandExitMessage("player-only");
    }

    @Override
    public String argumentDescription() {
        return "city player";
    }

    @Override
    public List<String> getSuggestions(@Nonnull String prefix) {
        return Collections.emptyList();
    }

    public void bind(CommandService drink) {
        drink.bind(CityPlayer.class).annotatedWith(Sender.class).annotatedWith(RequireCity.class).toProvider(this);
        drink.bind(CityPlayer.class).annotatedWith(Sender.class).toProvider(this);
    }
}
