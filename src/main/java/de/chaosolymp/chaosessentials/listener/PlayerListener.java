package de.chaosolymp.chaosessentials.listener;

import de.chaosolymp.chaosessentials.ChaosEssentials;
import de.chaosolymp.chaosessentials.util.MessageConverter;
import de.chaosolymp.chaosessentials.util.PlayerDeath;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

public class PlayerListener implements Listener {

    private final HashMap<String, Queue<Location>> playerHistory = new HashMap<>();
    private final HashMap<Player, Integer> playerMap = new HashMap<>();
    private final HashMap<String, PlayerDeath> playerDeaths = new HashMap<>();
    private final int afkTime = ChaosEssentials.getPlugin().getConfig().getInt("afk_time") * 60;
    private final Location spawnLoc = new Location(Bukkit.getWorld(
            ChaosEssentials.getPlugin().getConfig().getString("afk_location.world")),
            ChaosEssentials.getPlugin().getConfig().getInt("afk_location.x"),
            ChaosEssentials.getPlugin().getConfig().getInt("afk_location.y"),
            ChaosEssentials.getPlugin().getConfig().getInt("afk_location.z"));

    public PlayerListener() {
        initAfkTask();
        initPlayerTracker();
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e) {
        if (e.getFrom().getX() == e.getTo().getX() && e.getFrom().getY() == e.getTo().getY() && e.getTo().getZ() == e.getTo().getZ()) {
            return;
        }
        if (playerMap.containsKey(e.getPlayer()))
            playerMap.replace(e.getPlayer(), 0);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        if (!playerHistory.containsKey(e.getPlayer().getUniqueId().toString())) {
            Queue<Location> history = new LinkedList<>();
            playerHistory.put(e.getPlayer().getName(), history);
        }

        if (e.getPlayer().hasPermission("ce.afk.bypass")) {
            return;
        }
        playerMap.put(e.getPlayer(), 0);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        playerMap.remove(e.getPlayer());
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e) {
        Player player = e.getEntity();
        ChaosEssentials.log("Death of " + player.getName() + " recorded!");
        ChaosEssentials.log(e.getDeathMessage());
        ChaosEssentials.log("Level: " + e.getNewExp());

        PlayerDeath death = new PlayerDeath();
        death.setDeathMsg(e.getDeathMessage());
        death.setName(player.getName());
        death.setNewXp(player.getLevel());
        death.setLostItems(player.getInventory().getContents());
        death.setLostArmor(player.getInventory().getArmorContents());

        Queue<Location> history = playerHistory.get(player.getName());
        ChaosEssentials.log("Last Locations:");
        Location[] locations = new Location[history.size()];
        int i = 0;
        while (history.size() > 0) {
            ChaosEssentials.log(history.peek().toString());
            locations[i] = history.poll();
            i++;
        }
        death.setLastLocations(locations);
        playerDeaths.put(player.getName(), death);

    }

    private void initAfkTask() {
        new BukkitRunnable() {

            @Override
            public void run() {
                for (Map.Entry<Player, Integer> player : playerMap.entrySet()) {
                    player.setValue(player.getValue().intValue() + 6);
                    if (playerMap.get(player.getKey()) > afkTime) {
                        player.getKey().teleport(spawnLoc);
                        MessageConverter.sendConfMessage(player.getKey(), "afk_message");
                    }
                }
            }
        }.runTaskTimer(ChaosEssentials.getPlugin(), 120L, 120L);
    }

    public void initPlayerTracker() {
        new BukkitRunnable() {

            @Override
            public void run() {
                for (Player player : Bukkit.getServer().getOnlinePlayers()) {
                    Queue current = playerHistory.get(player.getName());
                    current.add(player.getLocation());
                    if (current.size() > 30)
                        current.poll();
                }
            }
        }.runTaskTimer(ChaosEssentials.getPlugin(), 120L, 60L);
    }

    public PlayerDeath getDeaths(String key) {
        return playerDeaths.get(key);
    }
}
