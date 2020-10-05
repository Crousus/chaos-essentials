package de.chaosolymp.chaosessentials.listener;

import de.chaosolymp.chaosessentials.ChaosEssentials;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class XpSpammerListener implements Listener {

    @EventHandler
    public void onRightClick(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        if ((e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) && e.getPlayer().getInventory().getItemInMainHand().getType() != Material.AIR) {
            if (player.hasPermission("ce.xpsword")) {
                if (player.getInventory().getItemInMainHand().getItemMeta().getDisplayName().equals("Xp Sword")) {
                    new BukkitRunnable() {
                        private int count = 5;

                        @Override
                        public void run() {
                            if (count > 0) {
                                Entity bottle = player.getWorld().spawnEntity(player.getLocation().add(0, 1, 0), EntityType.THROWN_EXP_BOTTLE);
                                bottle.setVelocity(player.getLocation().getDirection().multiply(1.5));
                                count--;
                            } else {
                                this.cancel();
                            }
                        }
                    }.runTaskTimer(ChaosEssentials.getPlugin(), 0, 1);
                }
            }
        }

    }
}
