package de.chaosolymp.chaosessentials.command;

import de.chaosolymp.chaosessentials.events.CommandEvent;
import de.chaosolymp.chaosessentials.util.MessageConverter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class HatCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player player = null;

        if (sender instanceof Player)
            player = (Player) sender;
        else
            sender.sendMessage("Muss als Spieler ausgeführt werden!");

        if (player.hasPermission("ce.use.hat") || player.hasPermission("ce.hat")) {
            if (player.getInventory().getItemInMainHand().getType() != Material.AIR) {
                ItemStack hand = player.getInventory().getItemInMainHand();
                ItemStack head = player.getInventory().getHelmet();

                player.getInventory().setItemInMainHand(head);
                player.getInventory().setHelmet(hand);
                MessageConverter.sendConfMessage(player, "hat");
                if (!player.hasPermission("ce.hat")) {
                    CommandEvent event = new CommandEvent(player, "hat");
                    Bukkit.getPluginManager().callEvent(event);
                }
            } else {
                MessageConverter.sendConfMessage(player, "hat_no_item");
            }
        } else {
            if (player.hasPermission("ce.buy"))
                MessageConverter.sendPurchaseable(player);
            else
                MessageConverter.sendNoPermission(player);
        }

        return false;
    }
}
