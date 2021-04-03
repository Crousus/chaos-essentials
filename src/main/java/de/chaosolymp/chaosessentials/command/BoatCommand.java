package de.chaosolymp.chaosessentials.command;

import de.chaosolymp.chaosessentials.events.CommandEvent;
import de.chaosolymp.chaosessentials.util.MessageConverter;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

public class BoatCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        final Player player;

        if (sender instanceof Player) {
            player = (Player) sender;

            if (player.hasPermission("ce.use.boat") || player.hasPermission("ce.boat")) {
                player.getWorld().spawnEntity(player.getLocation().add(0, 0.5, 0), EntityType.BOAT).addPassenger(player);
                MessageConverter.sendConfMessage(player, "boatspawn");
                CommandEvent event = new CommandEvent(player, "boat");
                Bukkit.getPluginManager().callEvent(event);
            } else {
                if (player.hasPermission("ce.buy"))
                    MessageConverter.sendPurchaseable(player);
                else
                    MessageConverter.sendNoPermission(player);
            }
        }
        return false;
    }
}
