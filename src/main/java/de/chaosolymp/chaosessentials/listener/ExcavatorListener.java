package de.chaosolymp.chaosessentials.listener;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

public class ExcavatorListener implements Listener {

    @EventHandler
    public void onBlockDestroy(BlockBreakEvent e) {
        Player player = e.getPlayer();
        Material mat = e.getBlock().getType();
        if (player.hasPermission("ce.excavator")) {
            if (player.getInventory().getItemInMainHand().getType() != Material.AIR && player.getInventory().getItemInMainHand().getItemMeta().getDisplayName().equals("Giga Drill")) {
                Location loc = e.getBlock().getLocation();
                for (int i = -1; i <= 1; i++) {
                    for (int s = -1; s <= 1; s++) {
                        for (int j = -1; j <= 1; j++) {
                            Block toBreak = (new Location(player.getWorld(), loc.getX() + i, loc.getY() + s, loc.getZ() + j).getBlock());
                            if (toBreak.getType() == mat) {
                                toBreak.breakNaturally();
                                ItemMeta item = player.getInventory().getItemInMainHand().getItemMeta();

                                if (item instanceof Damageable) {
                                    Damageable damageable = (Damageable) item;
                                    damageable.setDamage(damageable.getDamage() + 2);

                                    if (damageable.getDamage() > player.getInventory().getItemInMainHand().getType().getMaxDurability()) {
                                        player.getInventory().setItemInMainHand(new ItemStack(Material.AIR));
                                        player.playSound(player.getLocation(), Sound.ENTITY_ITEM_BREAK, 2f, 0.5f);
                                    } else
                                        player.getInventory().getItemInMainHand().setItemMeta((ItemMeta) damageable);
                                }
                            }
                        }
                    }
                }


            }

        }

    }
}
