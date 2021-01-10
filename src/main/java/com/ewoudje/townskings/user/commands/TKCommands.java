package com.ewoudje.townskings.user.commands;

import com.ewoudje.townskings.api.wrappers.TKPlayer;
import com.ewoudje.townskings.api.TKPlugin;
import com.ewoudje.townskings.town.Town;
import com.ewoudje.townskings.api.wrappers.TKItem;
import com.ewoudje.townskings.block.FoundingBlock;
import com.ewoudje.townskings.user.mode.ClaimPlotMode;
import com.jonahseguin.drink.annotation.Command;
import com.jonahseguin.drink.annotation.Require;
import com.jonahseguin.drink.annotation.Sender;
import com.jonahseguin.drink.annotation.Text;
import me.wiefferink.interactivemessenger.processing.Message;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;

import java.util.List;

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
            player.send(Message.fromKey("already-in-town").replacements(player.getTown().getName()));
            return;
        }

        ItemStack item = player.getPlayer().getInventory().getItemInMainHand();
        TKItem ci = plugin.getItem(item);
        if (ci == null || !ci.getType().is(FoundingBlock.class)) {
            player.send(Message.fromKey("invalid-item"));
            return;
        }

        player.send(Message.fromKey("when-placed-create-town"));

        FoundingBlock.makeReady(ci, name);
    }

    @Command(name = "list", aliases = {}, desc = "List towns", usage = "")
    @Require("tk.user.list")
    public void list(@Sender TKPlayer player) {
        List<Town> towns = player.getWorld().getTKPlugin();

        Message message = Message.fromKey("list-towns-start");

        for (Town town : towns) {
            message.append(Message.fromKey("list-towns-entry").replacements(town.getName()));
        }

       player.send(message);
    }

    @Command(name = "join", aliases = {}, desc = "Join town", usage = "")
    @Require("tk.user.join")
    public void join(@Sender TKPlayer player, Town town) {

        if (player.getTown() != null) {
            player.send(Message.fromKey("already-in-town").replacements(player.getTown().getName(), town));
            return;
        }

        if (town.isInvited(player)) {
            town.broadcast(Message.fromKey("player-joined").replacements(player.getPlayer().getName()), plugin);

            town.join(player);

            player.send(Message.fromKey("join-town").replacements(town.getName()));
        } else {
            town.broadcast(Message.fromKey("wants-join").replacements(player.getPlayer().getName()), plugin);

            player.send(Message.fromKey("need-invite"));
        }
    }

    @Command(name = "invite", aliases = {}, desc = "Invite person in town", usage = "")
    @Require("tk.user.invite")
    public void invite(@Sender @RequireTown TKPlayer player, TKPlayer invitee) {

        player.getTown().invite(invitee);

        player.send(Message.fromKey("invite-success")
                .replacements(invitee.getPlayer().getName(), player.getTown().getName()));
        invitee.send(Message.fromKey("town-invite")
                .replacements(player.getPlayer().getName(), player.getTown().getName()));
    }

    @Command(name = "leave", aliases = {}, desc = "Leave town", usage = "")
    @Require("tk.user.leave")
    public void leave(@Sender @RequireTown TKPlayer player) {
        Town c = player.getTown();

        c.leave(player);
        c.broadcast(Message.fromKey("player-left").replacements(player.getPlayer().getName()), plugin);

        player.send(Message.fromKey("leave-town").replacements(c.getName()));
    }

    @Command(name = "test", aliases = {}, desc = "Test", usage = "")
    @Require("tk.user.test")
    public void test(@Sender TKPlayer player) {

    }

    @Command(name = "claim", aliases = {}, desc = "claim mode", usage = "")
    @Require("tk.user.claim")
    public void claim(@Sender @RequireTown TKPlayer s) {
        if (plugin.getModeHandler().get(s.getPlayer()) == null) {
            plugin.getModeHandler().goInto(s, new ClaimPlotMode());
        } else {
            plugin.getModeHandler().disable(s.getPlayer());
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
        town.setOwner(owner == null ? null : owner.getOfflinePlayer());

        player.send(Message.fromKey("owner-changed")
                .replacements(town.getName(), owner == null ? "null" : owner.getPlayer().getName()));
    }

}
