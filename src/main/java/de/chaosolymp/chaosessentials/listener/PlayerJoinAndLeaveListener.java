package de.chaosolymp.chaosessentials.listener;

import de.chaosolymp.chaosessentials.ChaosEssentials;
import de.chaosolymp.chaosessentials.DatabaseController;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;

public class PlayerJoinAndLeaveListener implements Listener {

    HashSet noFall = new HashSet();

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();

        DatabaseController db = new DatabaseController();
        db.addPlayer(player.getUniqueId().toString());

        if (player.hasPermission("ce.auto.creative")) {
            player.setGameMode(GameMode.CREATIVE);
        }
        if (player.hasPermission("ce.safelogin")) {
            noFall.add(e.getPlayer());

            new BukkitRunnable() {
                @Override
                public void run() {
                    noFall.remove(player);
                }
            }.runTaskLater(ChaosEssentials.getPlugin(), 20 * 20);
        }
    }

    @EventHandler
    public void onPlayerFall(EntityDamageEvent e) {
        if (e.getEntity() instanceof Player) {
            Player player = (Player) e.getEntity();
            if (e.getCause() == EntityDamageEvent.DamageCause.FALL || e.getCause() == EntityDamageEvent.DamageCause.FLY_INTO_WALL) {
                if (noFall.contains(e.getEntity()) || player.removeScoreboardTag("falling"))
                    e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        DatabaseController db = new DatabaseController();
        db.updatePlayer(e.getPlayer().getUniqueId().toString());
    }

}
