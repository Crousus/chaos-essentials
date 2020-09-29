package de.chaosolymp.chaosessentials.util;

import de.chaosolymp.chaosessentials.ChaosEssentials;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class MessageConverter {
    public static void sendMessage(Player player, String msg) {
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', msg));
    }

    public static void sendConfMessage(Player player, String path) {
        sendMessage(player, ChaosEssentials.getPlugin().getConfig().getString(path));
    }

    public static void sendNoPermission(Player player) {
        sendConfMessage(player, "nopermission");
    }

    public static void sendPurchaseable(Player player) {
        sendConfMessage(player, "is_purchaseable");
    }
}
