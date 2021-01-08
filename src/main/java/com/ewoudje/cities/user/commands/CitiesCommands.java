package com.ewoudje.cities.user.commands;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.ewoudje.cities.Cities;
import com.ewoudje.cities.CityBlock;
import com.ewoudje.cities.CityPlayer;
import com.ewoudje.cities.city.City;
import com.ewoudje.cities.CityItem;
import com.ewoudje.cities.block.FoundingBlock;
import com.ewoudje.cities.item.InventoryDesigner;
import com.ewoudje.cities.item.ItemIcon;
import com.ewoudje.cities.item.ItemNone;
import com.ewoudje.cities.item.Items;
import com.ewoudje.cities.mode.Mode;
import com.ewoudje.cities.mode.ModeHandler;
import com.ewoudje.cities.mode.ModeSetting;
import com.ewoudje.cities.mode.ModeStatus;
import com.ewoudje.cities.user.mode.ClaimPlotMode;
import com.ewoudje.cities.version.v1_16_R3.PacketUtil;
import com.jonahseguin.drink.annotation.Command;
import com.jonahseguin.drink.annotation.Require;
import com.jonahseguin.drink.annotation.Sender;
import com.jonahseguin.drink.annotation.Text;
import me.wiefferink.interactivemessenger.processing.Message;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Random;

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

    @Command(name = "test", aliases = {}, desc = "Test", usage = "")
    @Require("cities.test")
    public void test(@Sender CityPlayer player) {
        if (plugin.getModeHandler().get(player.getPlayer()) == null) {
            plugin.getModeHandler().goInto(player, new Mode() {

                private final InventoryDesigner inventoryDesigner = new InventoryDesigner(Material.AIR);

                {
                    CityItem item = Items.createItem(player.getWorld(), ItemNone.class);
                    for (int i = 36; i <= 44; i++)
                        inventoryDesigner.set(i, item);

                    inventoryDesigner.set(37, Items.createItem(player.getWorld(), ItemIcon.MINUS));
                    inventoryDesigner.set(38, Items.createItem(player.getWorld(), ItemIcon.ROAD));
                    inventoryDesigner.set(39, Items.createItem(player.getWorld(), ItemIcon.PLUS));
                }

                @Override
                public void onLeftClick(int slot, @Nonnull ModeHandler modeHandler, @Nonnull CityPlayer player, @Nullable CityBlock block) {
                    String bs = "null";
                    if (block != null)
                        if (block.getType() != null) {
                            bs = block.getType().getName();
                        } else {
                            bs = block.getBlock().getType().name();
                        }

                    plugin.getLogger().info("LEFTCLICK Slot: " + slot + ", Block: " + bs);
                }

                @Override
                public void onRightClick(int slot, @Nonnull ModeHandler modeHandler, @Nonnull CityPlayer player, @Nullable CityBlock block) {
                    String bs = "null";
                    if (block != null)
                        if (block.getType() != null) {
                            bs = block.getType().getName();
                        } else {
                            bs = block.getBlock().getType().name();
                        }

                    plugin.getLogger().info("RIGHTCLICK Slot: " + slot + ", Block: " + bs);
                }

                @Override
                public void init(CityPlayer player) {

                }

                @Override
                @Nullable
                public ModeStatus getStatus() {
                    return null;
                }

                @Override
                public @Nonnull List<ModeSetting> getSettings() {
                    return List.of(ModeSetting.FLY, ModeSetting.CLIENTMODE, ModeSetting.INVULNERABLE, ModeSetting.INSTA_DESTROY);
                }

                @Nonnull
                @Override
                public List<ItemStack> getItemList() {
                    return inventoryDesigner.getPlayerFullItemList();
                }
            });
        } else {
            plugin.getModeHandler().disable(player.getPlayer());
        }
    }

    @Command(name = "claim", aliases = {}, desc = "claim mode", usage = "")
    @Require("cities.claim")
    public void claim(@Sender @RequireCity CityPlayer s) {
        if (plugin.getModeHandler().get(s.getPlayer()) == null) {
            plugin.getModeHandler().goInto(s, new ClaimPlotMode());
        } else {
            plugin.getModeHandler().disable(s.getPlayer());
        }
    }

}
