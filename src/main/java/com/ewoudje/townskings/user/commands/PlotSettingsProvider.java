package com.ewoudje.townskings.user.commands;

import com.ewoudje.townskings.api.town.PlotSettings;
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

public class PlotSettingsProvider extends DrinkProvider<PlotSettings> {

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
    public PlotSettings provide(@Nonnull CommandArg arg, @Nonnull List<? extends Annotation> annotations) throws CommandExitMessage {
        if (arg.isSenderPlayer()) {
            Player p = arg.getSenderAsPlayer();
            TKPlayer cp = RedisPlayer.read(p);

            if (cp.getTown() != null) {
                    return cp.getTown().getPlotSettings().stream().filter((s) -> arg.get().equals(s.getName()))
                            .findAny().orElseThrow(() -> new CommandExitMessage("plotsettings-not-found"));
            } else throw new CommandExitMessage("have-no-town");
        } else throw new CommandExitMessage("player-only");
    }

    @Override
    public String argumentDescription() {
        return "plot settings";
    }

    @Override
    public List<String> getSuggestions(@Nonnull String prefix) {
        return Collections.emptyList();
    }

    public void bind(CommandService drink) {
        drink.bind(PlotSettings.class).toProvider(this);
    }
}
