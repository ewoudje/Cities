package com.ewoudje.cities.commands;

import com.ewoudje.cities.Cities;
import com.ewoudje.cities.CityPlayer;
import com.ewoudje.cities.city.City;
import com.jonahseguin.drink.CommandService;
import com.jonahseguin.drink.annotation.Sender;
import com.jonahseguin.drink.argument.CommandArg;
import com.jonahseguin.drink.exception.CommandExitMessage;
import com.jonahseguin.drink.parametric.DrinkProvider;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class CityProvider extends DrinkProvider<City> {

    private final Cities plugin;

    public CityProvider(Cities plugin) {
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
    public City provide(@Nonnull CommandArg arg, @Nonnull List<? extends Annotation> annotations) throws CommandExitMessage {
        StringBuilder builder = new StringBuilder(arg.get());
        while (arg.getArgs().hasNext()) {
            builder.append(" ").append(arg.getArgs().next());
        }

        String name = builder.toString();
        Optional<City> city = plugin.getWorlds().stream()
                .flatMap((w) -> w.getCities().stream())
                .filter((c) -> c.getName().equals(name))
                .findAny();

        return city.orElseThrow(() -> new CommandExitMessage("invalid-city"));
    }

    @Override
    public String argumentDescription() {
        return "city";
    }

    @Override
    public List<String> getSuggestions(@Nonnull String prefix) {
        return plugin.getWorlds().stream()
                .flatMap((w) -> w.getCities().stream())
                .map(City::getName)
                .filter((c) -> c.startsWith(prefix))
                .collect(Collectors.toList());
    }

    public void bind(CommandService drink) {
        drink.bind(City.class).toProvider(this);
    }
}
