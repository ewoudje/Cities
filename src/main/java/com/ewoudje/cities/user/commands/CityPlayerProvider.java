package com.ewoudje.cities.user.commands;

import com.ewoudje.cities.Cities;
import com.ewoudje.cities.CityPlayer;
import com.jonahseguin.drink.CommandService;
import com.jonahseguin.drink.argument.CommandArg;
import com.jonahseguin.drink.parametric.DrinkProvider;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.annotation.Annotation;
import java.util.List;
import java.util.stream.Collectors;

public class CityPlayerProvider extends DrinkProvider<CityPlayer> {

    private Cities plugin;

    public CityPlayerProvider(Cities plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean doesConsumeArgument() {
        return true;
    }

    @Override
    public boolean isAsync() {
        return false;
    }

    @Nullable
    @Override
    public CityPlayer provide(@Nonnull CommandArg arg, @Nonnull List<? extends Annotation> annotations) throws CommandExitMessage {
        String name = arg.get();

        for (Annotation a : annotations) {
            System.out.println(a.toString());
        }

        if (name.equals("null") && annotations.stream().anyMatch((p) -> p.annotationType().equals(Noneable.class)))
            return null;

        CityPlayer p = plugin.getPlayer(plugin.getServer().getPlayer(name));
        if (p != null) {
            return p;
        }
        throw new CommandExitMessage("invalid-player");
    }

    @Override
    public String argumentDescription() {
        return "player";
    }

    @Override
    public List<String> getSuggestions(@Nonnull String prefix) {
        final String finalPrefix = prefix.toLowerCase();
        return plugin.getServer().getOnlinePlayers().stream()
                .map(p -> p.getName().toLowerCase())
                .filter(s -> finalPrefix.length() == 0 || s.startsWith(finalPrefix))
                .collect(Collectors.toList());
    }

    public void bind(CommandService drink) {
        drink.bind(CityPlayer.class).toProvider(this);
        drink.bind(CityPlayer.class).annotatedWith(Noneable.class).toProvider(this);
    }
}
