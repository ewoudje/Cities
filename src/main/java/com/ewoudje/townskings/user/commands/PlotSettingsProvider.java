package com.ewoudje.townskings.user.commands;

import com.ewoudje.townskings.api.town.PlotCategory;
import com.ewoudje.townskings.api.wrappers.TKPlayer;
import com.ewoudje.townskings.remote.RemotePlayer;
import com.jonahseguin.drink.CommandService;
import com.jonahseguin.drink.argument.CommandArg;
import com.jonahseguin.drink.parametric.DrinkProvider;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.List;

public class PlotSettingsProvider extends DrinkProvider<PlotCategory> {

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
    public PlotCategory provide(@Nonnull CommandArg arg, @Nonnull List<? extends Annotation> annotations) throws CommandExitMessage {
        if (arg.isSenderPlayer()) {
            Player p = arg.getSenderAsPlayer();
            TKPlayer cp = RemotePlayer.read(p);

            if (cp.getTown() != null) {
                PlotCategory category = cp.getTown().getPlotCategory(arg.get());

                if (category == null)
                    throw new CommandExitMessage("plotsettings-not-found");

                return category;
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
        drink.bind(PlotCategory.class).toProvider(this);
    }
}
