package com.ewoudje.townskings.user.commands;

import com.ewoudje.townskings.api.TKPlugin;
import com.ewoudje.townskings.api.wrappers.TKPlayer;
import com.jonahseguin.drink.CommandService;
import com.jonahseguin.drink.argument.CommandArg;
import com.jonahseguin.drink.parametric.DrinkProvider;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.annotation.Annotation;
import java.util.List;
import java.util.stream.Collectors;

public class TKPlayerProvider extends DrinkProvider<TKPlayer> {

    private TKPlugin plugin;

    public TKPlayerProvider(TKPlugin plugin) {
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
    public TKPlayer provide(@Nonnull CommandArg arg, @Nonnull List<? extends Annotation> annotations) throws CommandExitMessage {
        String name = arg.get();

        for (Annotation a : annotations) {
            System.out.println(a.toString());
        }

        if (name.equals("null") && annotations.stream().anyMatch((p) -> p.annotationType().equals(Noneable.class)))
            return null;

        Player pla = plugin.getServer().getPlayer(name);
        TKPlayer p = TKPlayer.wrap(pla);
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
        drink.bind(TKPlayer.class).toProvider(this);
        drink.bind(TKPlayer.class).annotatedWith(Noneable.class).toProvider(this);
    }
}
