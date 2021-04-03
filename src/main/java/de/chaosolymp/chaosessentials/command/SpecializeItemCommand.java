package de.chaosolymp.chaosessentials.command;

import de.chaosolymp.chaosessentials.ChaosEssentials;
import de.chaosolymp.chaosessentials.util.MessageConverter;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

public class SpecializeItemCommand implements CommandExecutor {

    private final NamespacedKey key = new NamespacedKey(ChaosEssentials.getPlugin(),"special");
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(sender instanceof Player){
            Player player = (Player) sender;
            if(player.hasPermission("ce.specialize")) {
                ItemStack item = player.getInventory().getItemInMainHand();
                if(item.getType() != Material.AIR){
                    ItemMeta meta = item.getItemMeta();
                    meta.getPersistentDataContainer().set(key, PersistentDataType.BYTE, (byte) 1);
                    item.setItemMeta(meta);
                    MessageConverter.sendMessage(player,"&aItem wurde SPEZIALISIERT!");
                }
            }
            else {
                MessageConverter.sendNoPermission(player);
            }
        }
        return false;
    }
}
