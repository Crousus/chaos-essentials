package de.chaosolymp.chaosessentials.command;

import de.chaosolymp.chaosessentials.ChaosEssentials;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public class SoulboundCommand implements CommandExecutor {

    private static NamespacedKey key = new NamespacedKey(ChaosEssentials.getPlugin(),"sb_id");
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(sender instanceof Player){
            Player player = (Player) sender;
            if(sender.hasPermission("ce.soulbind")){
                ItemStack item = player.getInventory().getItemInMainHand();
                if(args[0].equals("read")) {
                    if(isSoulBound(item)){
                        player.sendMessage(item.getItemMeta().getPersistentDataContainer().get(key,PersistentDataType.STRING));
                        return false;
                    }
                }
                if(args.length > 0) {
                    Player target = Bukkit.getPlayer(args[0]);
                    if(target != null)
                        bindToSoul(item, target.getUniqueId().toString());
                }
                else
                    bindToSoul(item,player.getUniqueId().toString());
            }
        }
        return false;
    }

    public static void bindToSoul(ItemStack item, String uuid){
        if(item != null && !item.getType().isAir() && !isSoulBound(item)){
            ItemMeta meta = item.getItemMeta();
            meta.getPersistentDataContainer().set(key, PersistentDataType.STRING,uuid);
            item.setItemMeta(meta);
        }
    }

    public static boolean isSoulOwner(ItemStack item, String uuid){
        return !isSoulBound(item) || item.getItemMeta().getPersistentDataContainer().get(key, PersistentDataType.STRING).equals(uuid);
    }

    public static boolean isSoulBound(ItemStack item) {
        return item.getItemMeta().getPersistentDataContainer().get(key, PersistentDataType.STRING) != null;
    }

}
