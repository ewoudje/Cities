package com.ewoudje.townskings.user.commands;

import com.ewoudje.townskings.NonePlayer;
import com.ewoudje.townskings.api.TKPlugin;
import com.ewoudje.townskings.api.town.PlotCategory;
import com.ewoudje.townskings.api.town.Town;
import com.ewoudje.townskings.api.wrappers.TKItem;
import com.ewoudje.townskings.api.wrappers.TKPlayer;
import com.ewoudje.townskings.block.FoundingBlock;
import com.ewoudje.townskings.item.Items;
import com.ewoudje.townskings.user.DefaultPermission;
import com.ewoudje.townskings.user.mode.ClaimPlotMode;
import com.ewoudje.townskings.util.SendUtil;
import com.ewoudje.townskings.util.StringUtil;
import com.jonahseguin.drink.annotation.Command;
import com.jonahseguin.drink.annotation.Require;
import com.jonahseguin.drink.annotation.Sender;
import com.jonahseguin.drink.annotation.Text;
import me.wiefferink.interactivemessenger.processing.Message;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;

import java.util.Set;
import java.util.UUID;

public class TKCommands {

    private final TKPlugin plugin;

    public TKCommands(TKPlugin plugin) {
        this.plugin = plugin;
    }

    @Command(name = "", aliases = {}, desc = "Returns version...", usage = "")
    @Require("tk.version")
    public void root(@Sender CommandSender sender) {
        Message.fromKey("version").replacements(plugin.getVersion()).send(sender);
    }

    @Command(name = "create", aliases = {}, desc = "Create Town", usage = "")
    @Require("tk.user.create")
    public void create(@Sender TKPlayer player, @Text String name) {
        if (player.getTown() != null) {
            SendUtil.send(player, Message.fromKey("already-in-town").replacements(player.getTown().getName()));
            return;
        }

        if (player.getWorld().getTown(name).isPresent()) {
            SendUtil.send(player, Message.fromKey("town-exists").replacements(name));
            return;
        }

        ItemStack item = player.getPlayer().getInventory().getItemInMainHand();
        TKItem ci = Items.getItem(item);
        if (ci == null || !ci.getType().is(FoundingBlock.class)) {
            SendUtil.send(player, Message.fromKey("invalid-item"));
            return;
        }

        SendUtil.send(player, Message.fromKey("when-placed-create-town"));

        FoundingBlock.makeReady(ci, name);
    }

    @Command(name = "list", aliases = {}, desc = "List towns", usage = "")
    @Require("tk.user.list")
    public void list(@Sender TKPlayer player) {
        Set<Town> towns = player.getWorld().getTowns();

        Message message = Message.fromKey("list-towns-start");

        for (Town town : towns) {
            message.append(Message.fromKey("list-towns-entry").replacements(town.getName()));
        }

       SendUtil.send(player, message);
    }

    @Command(name = "join", aliases = {}, desc = "Join town", usage = "")
    @Require("tk.user.join")
    public void join(@Sender TKPlayer player, Town town) {

        if (player.getTown() != null) {
            SendUtil.send(player, Message.fromKey("already-in-town").replacements(player.getTown().getName(), town));
            return;
        }

        if (town.isInvited(player)) {
            SendUtil.broadcast(town, Message.fromKey("player-joined").replacements(player.getPlayer().getName()));

            town.join(player);

            SendUtil.send(player, Message.fromKey("join-town").replacements(town.getName()));
        } else {
            SendUtil.broadcast(town, Message.fromKey("wants-join").replacements(player.getPlayer().getName()));

            SendUtil.send(player, Message.fromKey("need-invite"));
        }
    }

    @Command(name = "invite", aliases = {}, desc = "Invite person in town", usage = "")
    @Require("tk.user.invite")
    public void invite(@Sender @RequireTown TKPlayer player, TKPlayer invitee) {

        player.getTown().invite(invitee);

        SendUtil.send(player, Message.fromKey("invite-success")
                .replacements(invitee.getPlayer().getName(), player.getTown().getName()));
        SendUtil.send(invitee, Message.fromKey("town-invite")
                .replacements(player.getPlayer().getName(), player.getTown().getName()));
    }

    @Command(name = "leave", aliases = {}, desc = "Leave town", usage = "")
    @Require("tk.user.leave")
    public void leave(@Sender @RequireTown TKPlayer player) {
        Town c = player.getTown();
        String name = c.getName(); //We have to get it now bcs after leaving it can become invalid

        c.leave(player);
    }

    @Command(name = "test", aliases = {}, desc = "Test", usage = "")
    @Require("tk.admin.test")
    public void test(@Sender TKPlayer player) {
        TKItem item = Items.getItem(player.getPlayer().getInventory().getItemInMainHand());

        if (item == null) {
            SendUtil.send(player, Message.fromKey("invalid-item"));
            return;
        }

        item.getNBT().setUUID("id", UUID.randomUUID()); //THIS IS NOT GOOD OR NORMAL (test reasons)
    }

    @Command(name = "claim", aliases = {}, desc = "claim mode", usage = "")
    @Require("tk.user.claim")
    public void claim(@Sender @RequireTown TKPlayer s, PlotCategory settings) {
        if (true) //TODO enable claim
            return;

        if (settings.isAllowed(s, DefaultPermission.MANAGE)) {
            if (plugin.getModeHandler().get(s.getPlayer()) == null) {
                plugin.getModeHandler().goInto(s, new ClaimPlotMode(settings));
            } else {
                plugin.getModeHandler().disable(s.getPlayer());
            }
        } else {
            SendUtil.send(s, Message.fromKey("no-permission"));
        }
    }

    @Command(name = "owner", aliases = {}, desc = "Set owner of town", usage = "")
    @Require("tk.user.owner")
    public void owner(@Sender @RequireTown TKPlayer player, TKPlayer owner) {
        owner(player, owner, player.getTown());
    }

    @Command(name = "owner", aliases = {}, desc = "Set owner of town", usage = "")
    @Require("tk.admin.owner")
    public void owner(@Sender TKPlayer player, @Noneable TKPlayer owner, Town town) {
        town.setFounder(owner == null ? new NonePlayer() : owner.getOfflinePlayer());

        SendUtil.send(player, Message.fromKey("owner-changed")
                .replacements(town.getName(), owner == null ? "None" : owner.getPlayer().getName()));
    }

    @Command(name = "give", aliases = {}, desc = "Get item", usage = "")
    @Require("tk.admin.give")
    public void owner(@Sender TKPlayer _sender, TKPlayer player, String itemKey, int amount) {
        TKItem item = Items.createItem(null, StringUtil.getKey(itemKey), amount);

        if (item == null) {
            SendUtil.send(player, Message.fromKey("invalid-item-key").replacements(itemKey));
            return;
        }

        player.getPlayer().getInventory().addItem(item.getItem());
    }



}
