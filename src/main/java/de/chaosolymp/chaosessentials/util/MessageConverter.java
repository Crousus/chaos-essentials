package de.chaosolymp.chaosessentials.util;

import de.chaosolymp.chaosessentials.ChaosEssentials;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MessageConverter {

    public static void sendMessage(CommandSender sender, String msg) {
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', msg));
    }

    public static void sendConfMessage(CommandSender sender, String path) {
        sendMessage(sender, ChaosEssentials.getPlugin().getConfig().getString(path));
    }

    public static void sendNoPermission(CommandSender sender) {
        sendConfMessage(sender, "nopermission");
    }

    public static void sendPurchaseable(Player player) {
        sendConfMessage(player, "is_purchaseable");
    }
}
