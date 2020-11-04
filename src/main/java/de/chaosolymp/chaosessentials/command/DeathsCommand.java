package de.chaosolymp.chaosessentials.command;

import de.chaosolymp.chaosessentials.gui.AbstractGui;
import de.chaosolymp.chaosessentials.listener.PlayerListener;
import de.chaosolymp.chaosessentials.util.MessageConverter;
import de.chaosolymp.chaosessentials.util.PlayerDeath;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class DeathsCommand implements CommandExecutor {
    private final PlayerListener playerDeaths;

    public DeathsCommand(PlayerListener playerDeaths) {
        this.playerDeaths = playerDeaths;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        final Player player;

        if (sender instanceof Player) {
            player = (Player) sender;

            if (player.hasPermission("ce.deaths")) {
                if (args.length > 1) {
                    if (args[0].equals("info")) {
                        handleInfo(args[1], player);
                    } else if (args[0].equals("inv")) {
                        if (player.hasPermission("ce.editinv"))
                            handleInvsee(args[1], player);
                        else
                            MessageConverter.sendNoPermission(player);
                    } else if (args[0].equals("armor")) {
                        if (player.hasPermission("ce.editinv"))
                            handleArmor(args[1], player);
                        else
                            MessageConverter.sendNoPermission(player);
                    } else {
                        MessageConverter.sendConfMessage(player, "deaths_usage");
                    }
                } else {
                    MessageConverter.sendConfMessage(player, "deaths_usage");
                }
            }
        }
        return false;
    }

    private void handleInfo(String suspect, Player player) {
        PlayerDeath death = playerDeaths.getDeaths(suspect);
        if (death != null) {
            MessageConverter.sendMessage(player, "&6---- Player Death Info ----");
            MessageConverter.sendMessage(player, "&6Level: &5" + death.getNewXp());
            MessageConverter.sendMessage(player, "&6Todesursache: &e" + death.getDeathMsg());
            int i = 1;
            Location[] locations = death.getLastLocations();
            while (i < locations.length) {
                TextComponent row = new TextComponent();
                do {
                    TextComponent message = new TextComponent("[" + i + "] ");
                    message.setColor(net.md_5.bungee.api.ChatColor.GREEN);
                    message.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,
                            "/ctp " + locations[i].getWorld().getName() + " " + locations[i].getX() + " " + locations[i].getY() + " " + locations[i].getZ() + " " + locations[i].getPitch() + " " + locations[i].getYaw()));
                    message.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("Teleport to Location")));
                    row.addExtra(message);
                    i++;
                } while (i < locations.length && i % 6 != 0);
                player.spigot().sendMessage(row);
            }
        } else {
            MessageConverter.sendConfMessage(player, "no_death");
        }
    }

    private void handleInvsee(String suspect, Player player) {
        PlayerDeath death = playerDeaths.getDeaths(suspect);
        ItemStack[] items = death.getLostItems();
        DeathsGui gui = new DeathsGui((int) Math.ceil(items.length / 9), "&4" + suspect + "'s Tod", player);
        for (ItemStack stack : items) {
            if (stack != null)
                gui.addItemToGui(stack);
        }
        gui.openInventory(player);
    }

    private void handleArmor(String suspect, Player player) {
        PlayerDeath death = playerDeaths.getDeaths(suspect);
        ItemStack[] items = death.getLostArmor();
        DeathsGui gui = new DeathsGui((int) Math.ceil(items.length / 9), "&4" + suspect + "'s Armor", player);
        for (ItemStack stack : items) {
            if (stack != null)
                gui.addItemToGui(stack);
        }
        gui.openInventory(player);
    }

    private class DeathsGui extends AbstractGui {

        private final Player player;

        protected DeathsGui(int size, String name, Player player) {
            super(size, name);
            this.player = player;
        }

        @Override
        public void passInventoryClick(InventoryClickEvent e) {
            e.setCancelled(false);
        }
    }

}
