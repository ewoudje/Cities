package com.ewoudje.townskings.user.commands;

import com.ewoudje.townskings.api.TKPlugin;
import com.ewoudje.townskings.town.Town;
import com.jonahseguin.drink.CommandService;
import com.jonahseguin.drink.argument.CommandArg;
import com.jonahseguin.drink.exception.CommandExitMessage;
import com.jonahseguin.drink.parametric.DrinkProvider;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class TKProvider extends DrinkProvider<Town> {

    private final TKPlugin plugin;

    public TKProvider(TKPlugin plugin) {
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
    public Town provide(@Nonnull CommandArg arg, @Nonnull List<? extends Annotation> annotations) throws CommandExitMessage {
        StringBuilder builder = new StringBuilder(arg.get());
        while (arg.getArgs().hasNext()) {
            builder.append(" ").append(arg.getArgs().next());
        }

        String name = builder.toString();
        Optional<Town> town = plugin.getWorlds().stream()
                .flatMap((w) -> w.getTKPlugin().stream())
                .filter((c) -> c.getName().equals(name))
                .findAny();

        return town.orElseThrow(() -> new CommandExitMessage("invalid-town"));
    }

    @Override
    public String argumentDescription() {
        return "town";
    }

    @Override
    public List<String> getSuggestions(@Nonnull String prefix) {
        return plugin.getWorlds().stream()
                .flatMap((w) -> w.getTKPlugin().stream())
                .map(Town::getName)
                .filter((c) -> c.startsWith(prefix))
                .collect(Collectors.toList());
    }

    public void bind(CommandService drink) {
        drink.bind(Town.class).toProvider(this);
    }
}
