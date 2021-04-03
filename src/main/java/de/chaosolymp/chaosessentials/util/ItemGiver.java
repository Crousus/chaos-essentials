package de.chaosolymp.chaosessentials.util;

import de.chaosolymp.chaosessentials.ChaosEssentials;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class ItemGiver {
    public static boolean giveItemSave(Player player, ItemStack item) {
        Inventory inv = player.getInventory();

        if (inv.firstEmpty() == -1) {
            Bukkit.getScheduler().runTask(ChaosEssentials.getPlugin(), () -> player.getLocation().getWorld().dropItem(player.getLocation(), item));
            MessageConverter.sendConfMessage(player, "inv-full");
            return false;
        } else {
            inv.addItem(item);
            return true;
        }
    }
}
