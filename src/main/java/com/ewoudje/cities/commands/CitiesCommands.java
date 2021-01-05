package com.ewoudje.cities.commands;

import com.ewoudje.cities.Cities;
import com.ewoudje.cities.CityPlayer;
import com.ewoudje.cities.city.City;
import com.ewoudje.cities.CityItem;
import com.ewoudje.cities.block.FoundingBlock;
import com.ewoudje.cities.item.Items;
import com.jonahseguin.drink.annotation.Command;
import com.jonahseguin.drink.annotation.Require;
import com.jonahseguin.drink.annotation.Sender;
import com.jonahseguin.drink.annotation.Text;
import me.wiefferink.interactivemessenger.processing.Message;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class CitiesCommands {

    private final Cities plugin;

    public CitiesCommands(Cities plugin) {
        this.plugin = plugin;
    }

    @Command(name = "", aliases = {}, desc = "Returns version...", usage = "")
    @Require("cities.version")
    public void root(@Sender CommandSender sender) {
        Message.fromKey("version").replacements(plugin.getVersion()).send(sender);
    }

    @Command(name = "create", aliases = {}, desc = "Create City", usage = "")
    @Require("cities.create")
    public void create(@Sender CityPlayer player, @Text String name) {
        if (player.getCity() != null) {
            player.send(Message.fromKey("already-in-city").replacements(player.getCity().getName()));
            return;
        }

        ItemStack item = player.getPlayer().getInventory().getItemInMainHand();
        CityItem ci = plugin.getItem(item);
        if (ci == null || !ci.getType().is(FoundingBlock.class)) {
            player.send(Message.fromKey("invalid-item"));
            return;
        }

        player.send(Message.fromKey("when-placed-create-city"));

        FoundingBlock.makeReady(ci, name);
    }

    @Command(name = "list", aliases = {}, desc = "List cities", usage = "")
    @Require("cities.list")
    public void list(@Sender CityPlayer player) {
        List<City> cities = player.getWorld().getCities();

        Message message = Message.fromKey("list-cities-start");

        for (City city : cities) {
            message.append(Message.fromKey("list-cities-entry").replacements(city.getName()));
        }

       player.send(message);
    }

    @Command(name = "join", aliases = {}, desc = "Join city", usage = "")
    @Require("cities.join")
    public void join(@Sender CityPlayer player, City city) {

        if (player.getCity() != null) {
            player.send(Message.fromKey("already-in-city").replacements(player.getCity().getName(), city));
            return;
        }

        if (city.isInvited(player)) {
            city.broadcast(Message.fromKey("player-joined").replacements(player.getPlayer().getName()), plugin);

            city.join(player);

            player.send(Message.fromKey("join-city").replacements(city.getName()));
        } else {
            city.broadcast(Message.fromKey("wants-join").replacements(player.getPlayer().getName()), plugin);

            player.send(Message.fromKey("need-invite"));
        }
    }

    @Command(name = "invite", aliases = {}, desc = "Invite person in city", usage = "")
    @Require("cities.invite")
    public void invite(@Sender @RequireCity CityPlayer player, CityPlayer invitee) {

        player.getCity().invite(invitee);

        player.send(Message.fromKey("invite-success")
                .replacements(invitee.getPlayer().getName(), player.getCity().getName()));
        invitee.send(Message.fromKey("city-invite")
                .replacements(player.getPlayer().getName(), player.getCity().getName()));
    }

    @Command(name = "leave", aliases = {}, desc = "Leave city", usage = "")
    @Require("cities.leave")
    public void leave(@Sender @RequireCity CityPlayer player) {
        City c = player.getCity();

        c.leave(player);
        c.broadcast(Message.fromKey("player-left").replacements(player.getPlayer().getName()), plugin);

        player.send(Message.fromKey("leave-city").replacements(c.getName()));
    }

}
