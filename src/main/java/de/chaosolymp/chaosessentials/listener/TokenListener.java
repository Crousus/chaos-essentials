package de.chaosolymp.chaosessentials.listener;

import de.chaosolymp.chaosessentials.DatabaseController;
import de.chaosolymp.chaosessentials.tokens.TagEditor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class TokenListener implements Listener {

    @EventHandler
    public void onRightClick(PlayerInteractEvent e) {
        if (e.getAction() == Action.RIGHT_CLICK_BLOCK || e.getAction() == Action.RIGHT_CLICK_AIR) {
            ItemStack item = e.getPlayer().getInventory().getItemInMainHand();
            if (item.getType() != Material.AIR) {
                TagEditor tagEditor = new TagEditor(e.getPlayer().getInventory().getItemInMainHand());
                String tag = tagEditor.getTag();
                if (tag != null) {
                    DatabaseController db = new DatabaseController();
                    String command = db.getToken(tag).replaceAll("%player%", e.getPlayer().getName());
                    Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command.substring(1));
                    e.getPlayer().getInventory().remove(item);
                }
            }
        }
    }
}
