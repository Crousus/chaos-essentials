package de.chaosolymp.chaosessentials.listener;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class MultiToolListener implements Listener {

    @EventHandler
    public void onRightClick(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        if (e.getAction() == Action.RIGHT_CLICK_BLOCK && e.getPlayer().getInventory().getItemInMainHand().getType() != Material.AIR) {
            if (player.hasPermission("ce.multitool")) {
                ItemStack item = player.getInventory().getItemInMainHand();
                if (item.getItemMeta().getDisplayName().equals("Multi Tool") && isValidTool(item.getType()) && item.getType() != getBestTool(e.getClickedBlock().getType(), item.getType())) {
                    ItemMeta meta = item.getItemMeta();
                    ItemStack newItem = new ItemStack(getBestTool(e.getClickedBlock().getType(), item.getType()), 1);
                    newItem.setItemMeta(meta);
                    player.getInventory().setItemInMainHand(newItem);
                    player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1f, 0.5f);
                }
            }
        }

    }

    private boolean isValidTool(Material m) {
        return m == Material.DIAMOND_PICKAXE || m == Material.DIAMOND_SHOVEL || m == Material.DIAMOND_HOE || m == Material.DIAMOND_AXE ||
                m == Material.NETHERITE_PICKAXE || m == Material.NETHERITE_SHOVEL || m == Material.NETHERITE_HOE || m == Material.NETHERITE_AXE;
    }

    private Material getBestTool(Material m, Material tool) {
        System.out.println(m.toString());

        if (m.toString().contains("STONE") || m.toString().contains("ORE")) {
            if (tool.toString().contains("NETHERITE"))
                return Material.NETHERITE_PICKAXE;
            else
                return Material.DIAMOND_PICKAXE;
        }
        if (m.toString().contains("DIRT") || m.toString().contains("SAND")) {
            if (tool.toString().contains("NETHERITE"))
                return Material.NETHERITE_SHOVEL;
            else
                return Material.DIAMOND_SHOVEL;
        }
        if (m.toString().contains("WOOD") || m.toString().contains("MUSHROOM") || m.toString().contains("LOG")) {
            if (tool.toString().contains("NETHERITE"))
                return Material.NETHERITE_AXE;
            else
                return Material.DIAMOND_AXE;
        }
        if (m == Material.FARMLAND || m == Material.WHEAT_SEEDS) {
            if (tool.toString().contains("NETHERITE"))
                return Material.NETHERITE_HOE;
            else
                return Material.DIAMOND_HOE;
        }
        return tool;
    }

}
