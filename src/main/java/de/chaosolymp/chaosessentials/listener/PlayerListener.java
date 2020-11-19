package de.chaosolymp.chaosessentials.listener;

import de.chaosolymp.chaosessentials.ChaosEssentials;
import de.chaosolymp.chaosessentials.util.MessageConverter;
import de.chaosolymp.chaosessentials.util.PlayerDeath;
import de.chaosolymp.chaosessentials.util.RegionCheck;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.awt.*;
import java.util.*;

public class PlayerListener implements Listener {

    private final HashMap<String, Queue<Location>> playerHistory = new HashMap<>();
    private final HashMap<Player, Integer> playerMap = new HashMap<>();
    private final HashMap<String, PlayerDeath> playerDeaths = new HashMap<>();
    private final HashMap<String, Integer> nervedFlyPlayers = new HashMap<>();
    private final int afkTime = ChaosEssentials.getPlugin().getConfig().getInt("afk_time") * 60;
    private final int MAX_FLY = ChaosEssentials.getPlugin().getConfig().getInt("max_fly");
    private final int DECREASE = ChaosEssentials.getPlugin().getConfig().getInt("fly_decrease");
    private final int INCREASE = ChaosEssentials.getPlugin().getConfig().getInt("fly_increase");
    private final Location spawnLoc = new Location(Bukkit.getWorld(
            ChaosEssentials.getPlugin().getConfig().getString("afk_location.world")),
            ChaosEssentials.getPlugin().getConfig().getInt("afk_location.x"),
            ChaosEssentials.getPlugin().getConfig().getInt("afk_location.y"),
            ChaosEssentials.getPlugin().getConfig().getInt("afk_location.z"));

    public PlayerListener() {
        initAfkTask();
        initPlayerTracker();
        if(ChaosEssentials.getPlugin().getConfig().getBoolean("flycheck")){
            initFlyCheck();
            initNervedFly();
            ChaosEssentials.log("initFly");
        }

    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e) {
        if (e.getFrom().getX() == e.getTo().getX() && e.getTo().getZ() == e.getTo().getZ()) {
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

        if(e.getPlayer().hasPermission("ce.nervedfly") && !e.getPlayer().hasPermission("ce.fullfly") && !nervedFlyPlayers.containsKey(e.getPlayer().getUniqueId().toString())){
            nervedFlyPlayers.put(e.getPlayer().getUniqueId().toString(),MAX_FLY);
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
                        player.setValue(0);
                    }
                }
            }
        }.runTaskTimer(ChaosEssentials.getPlugin(), 120L, 120L);
    }

    private void initPlayerTracker() {
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

    private void initFlyCheck() {
        new BukkitRunnable() {

            @Override
            public void run() {
                for (Player player : Bukkit.getServer().getOnlinePlayers()) {
                    if(!player.hasPermission("essentials.fly") && !player.hasPermission("ce.nervedfly")){
                        if(player.getAllowFlight() && player.getGameMode() == GameMode.SURVIVAL) {
                            if(!RegionCheck.hasFlag(player.getLocation(),"fly")){
                                player.setAllowFlight(false);
                                player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 30 * 20, 100,false,false));
                                MessageConverter.sendConfMessage(player,"fly_expired");
                            }
                        }
                    }
                }
            }
        }.runTaskTimer(ChaosEssentials.getPlugin(),300L,30*20L);

    }

    private void initNervedFly(){
        new BukkitRunnable(){

            @Override
            public void run() {
                for (Map.Entry<String, Integer> entry : nervedFlyPlayers.entrySet()) {
                    Player p = Bukkit.getServer().getPlayer(UUID.fromString(entry.getKey()));
                    if (p != null) {
                        if (!p.hasPermission("essentials.fly")) {
                            if (p.isFlying() && !RegionCheck.hasFlag(p.getLocation(),"fly")) {
                                entry.setValue(entry.getValue() - DECREASE);
                                if (entry.getValue() < 1) {
                                    entry.setValue(0);
                                    p.setAllowFlight(false);
                                }
                                if (entry.getValue() < DECREASE * 10) {
                                    p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 2f, 2f);
                                }
                            } else {
                                if (entry.getValue() < MAX_FLY)
                                    entry.setValue(entry.getValue() + INCREASE);
                                if (!p.getAllowFlight() && entry.getValue() > (int) (MAX_FLY * 0.12)) {
                                    p.setAllowFlight(true);
                                }
                            }
                        }

                        TextComponent actionComponent = new TextComponent("Fly [");
                        actionComponent.setColor(ChatColor.YELLOW);
                        TextComponent component;

                        for (int i = 1; i <= 25; i++) {
                            int percent = (int) (entry.getValue() / (1.0 * MAX_FLY) * 100);
                            percent = (int) (percent * (25.0 / 100.0));
                            if (i <= percent) {
                                component = new TextComponent("|");
                            } else {
                                if((i & 1) == 1)
                                    component = new TextComponent(" ");
                                else
                                    continue;
                            }

                            if (i <= 2)
                                component.setColor(ChatColor.RED);
                            else
                                component.setColor(ChatColor.YELLOW);
                            actionComponent.addExtra(component);
                        }
                        component = new TextComponent("] " + entry.getValue() + "/" + MAX_FLY);
                        component.setColor(ChatColor.YELLOW);
                        actionComponent.addExtra(component);
                        p.spigot().sendMessage(ChatMessageType.ACTION_BAR, actionComponent);

                    }
                }
            }
        }.runTaskTimerAsynchronously(ChaosEssentials.getPlugin(),150,20);
    }

    public PlayerDeath getDeaths(String key) {
        return playerDeaths.get(key);
    }
}
