package de.chaosolymp.chaosessentials.listener;

import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import de.chaosolymp.chaosessentials.ChaosEssentials;
import de.chaosolymp.chaosessentials.command.SoulboundCommand;
import de.chaosolymp.chaosessentials.config.DailyPlayersConfig;
import de.chaosolymp.chaosessentials.util.EditPermission;
import de.chaosolymp.chaosessentials.util.MessageConverter;
import de.chaosolymp.chaosessentials.util.PlayerDeath;
import de.chaosolymp.chaosessentials.util.RegionCheck;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryEvent;
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.*;

public class PlayerListener implements Listener {


    private NamespacedKey uuidKey;
    private NamespacedKey specialItemKey;
    private final HashMap<String, Queue<Location>> playerHistory = new HashMap<>();
    private final HashMap<Player, Integer> playerMap = new HashMap<>();
    private final HashMap<String, Queue<PlayerDeath>> playerDeaths = new HashMap<>();
    private final HashMap<String, Integer> nervedFlyPlayers = new HashMap<>();
    //private final HashMap<String ,long[]> dailyPlayers = new HashMap<>();
    private final int afkTime = ChaosEssentials.getPlugin().getConfig().getInt("afk_time") * 60;
    private final int MAX_FLY = ChaosEssentials.getPlugin().getConfig().getInt("max_fly");
    private final int DECREASE = ChaosEssentials.getPlugin().getConfig().getInt("fly_decrease");
    private final int INCREASE = ChaosEssentials.getPlugin().getConfig().getInt("fly_increase");
    private boolean isflycheck = false;
    private final Location spawnLoc = new Location(Bukkit.getWorld(
            ChaosEssentials.getPlugin().getConfig().getString("afk_location.world")),
            ChaosEssentials.getPlugin().getConfig().getInt("afk_location.x"),
            ChaosEssentials.getPlugin().getConfig().getInt("afk_location.y"),
            ChaosEssentials.getPlugin().getConfig().getInt("afk_location.z"));

    public PlayerListener() {
        initAfkTask();
        initPlayerTracker();
        if (ChaosEssentials.getPlugin().getConfig().getBoolean("flycheck")) {
            initFlyCheck();
            initNervedFly();
            ChaosEssentials.log("initFly");
            isflycheck = true;
        }
        uuidKey = new NamespacedKey(ChaosEssentials.getPlugin(),"sb_id");
        specialItemKey = new NamespacedKey(ChaosEssentials.getPlugin(),"special");

    }

    @EventHandler
    public void onSprintToggle(PlayerToggleSprintEvent e) {
        resetAfkTimer(e.getPlayer());
    }

    @EventHandler
    public void onSleep(PlayerBedEnterEvent e) {
        resetAfkTimer(e.getPlayer());
    }

    @EventHandler
    public void onFish(PlayerFishEvent e) {
        resetAfkTimer(e.getPlayer());
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent e) {
        resetAfkTimer(e.getPlayer());
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        if (((int) e.getFrom().getX()) == ((int) e.getTo().getX()) && ((int) e.getTo().getZ()) == ((int) e.getTo().getZ())) {
            return;
        }
        Vector vec = e.getFrom().toVector().getCrossProduct(e.getTo().toVector());
        if ((Math.abs(vec.getX()) + Math.abs(vec.getZ()) + Math.abs(vec.getY())) > 46 && (Math.abs(vec.getY()) + Math.abs(vec.getX()) + Math.abs(vec.getZ())) < 54) {
            resetAfkTimer(e.getPlayer());
        }
    }

    private void resetAfkTimer(Player p) {
        if (playerMap.containsKey(p))
            playerMap.replace(p, 0);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        if (!playerHistory.containsKey(e.getPlayer().getUniqueId().toString())) {
            Queue<Location> history = new LinkedList<>();
            playerHistory.put(e.getPlayer().getName(), history);
        }

        if (e.getPlayer().hasPermission("ce.nervedfly") && !e.getPlayer().hasPermission("ce.fullfly") && !nervedFlyPlayers.containsKey(e.getPlayer().getUniqueId().toString())) {
            nervedFlyPlayers.put(e.getPlayer().getUniqueId().toString(), MAX_FLY);
        }

        if (!e.getPlayer().hasPlayedBefore() && isflycheck) {
            EditPermission editPermission = new EditPermission();
            editPermission.addPermission(e.getPlayer(), "ce.afk.bypass", 72L, null, null);
        }

        if (e.getPlayer().hasPermission("ce.afk.bypass")) {
            return;
        }
/**
 *
 * Coming soon
        if (e.getPlayer().hasPermission("ce.dailychest")){
            final String uuid = e.getPlayer().getUniqueId().toString();
            if(dailyPlayers.containsKey(uuid)){
                long[] data = dailyPlayers.get(e.getPlayer().getUniqueId().toString());
                if(data[0] == LocalDate.now().toEpochDay()){
                    if(data[1] < ChaosEssentials.getPlugin().getConfig().getInt("daily.time") * 60){
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                if(data[0] != LocalDate.now().toEpochDay()) {
                                    data[1] = 0;
                                    data[0] = LocalDate.now().toEpochDay();
                                }
                                if(Bukkit.getPlayer(uuid) != null){
                                    data[1] = data[1] + 30;
                                    if(data[1] > ChaosEssentials.getPlugin().getConfig().getInt("daily.time") * 60) {
                                        Bukkit.getScheduler().runTask(ChaosEssentials.getPlugin(), () ->
                                                Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), ChaosEssentials.getPlugin().getConfig()
                                                        .getString("daily.command").replaceFirst("%player%",Bukkit.getPlayer(uuid).getName())));
                                        DailyPlayersConfig.get().set(data[1]+"."+uuid,data[1]);
                                        DailyPlayersConfig.save();
                                    }
                                } else {
                                    this.cancel();
                                }
                            }
                        }.runTaskTimerAsynchronously(ChaosEssentials.getPlugin(),30,30*20);
                    }
                }
            }
        } **/

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
        ChaosEssentials.log("Level: " + player.getLevel());

        PlayerDeath death = new PlayerDeath();
        death.setDeathMsg(e.getDeathMessage());
        death.setName(player.getName());
        death.setNewXp(player.getLevel());
        death.setLostItems(player.getInventory().getContents());
        death.setLostArmor(player.getInventory().getArmorContents());
        death.setDeathTime(new Timestamp(System.currentTimeMillis()));

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

        if(!playerDeaths.containsKey(player.getName())){
            Queue<PlayerDeath> deathQueue = new LinkedList<>();
            playerDeaths.put(player.getName(), deathQueue);
        } else if(playerDeaths.get(player.getName()).size() >= ChaosEssentials.getPlugin().getConfig().getInt("deaths-save-amount")) {
            playerDeaths.get(player.getName()).remove();
        }
        playerDeaths.get(player.getName()).offer(death);

    }

    @EventHandler
    public void itemPickupListener(EntityPickupItemEvent e){
        if(e.getEntity() instanceof Player){
            if(!SoulboundCommand.isSoulOwner(e.getItem().getItemStack(),e.getEntity().getUniqueId().toString())) {
                if(e.getEntity().hasPermission("ce.soulbound.bypass")) {
                    MessageConverter.sendConfMessage(e.getEntity(),"soulbound-alert");
                } else {
                    e.setCancelled(true);
                    e.getItem().setPickupDelay(80);
                    MessageConverter.sendConfMessage(e.getEntity(), "soulbound-notice");
                }
            }
        }
    }

    @EventHandler
    public void itemClickListener(InventoryClickEvent e) {
        ItemStack item = e.getCurrentItem();
        if(item == null || item.getType() == Material.AIR || !SoulboundCommand.isSoulBound(item))
            return;
        if (!SoulboundCommand.isSoulOwner(item,e.getWhoClicked().getUniqueId().toString())) {
            if(e.getWhoClicked().hasPermission("ce.soulbound.bypass")){
                MessageConverter.sendConfMessage(e.getWhoClicked(), "soulbound-alert");
            } else {
                e.setCancelled(true);
                MessageConverter.sendConfMessage(e.getWhoClicked(), "soulbound-notice");
            }
        }
    }

    @EventHandler (priority = EventPriority.LOW)
    public void onSpecialItemClick(PlayerInteractEvent e){
        if(e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK){
            if(e.useItemInHand().equals(Event.Result.DENY)){
                return;
            }
            ItemStack item = e.getPlayer().getInventory().getItemInMainHand();
            if(item.getType() != Material.AIR)
            if(item.getItemMeta().getPersistentDataContainer().has(specialItemKey,PersistentDataType.BYTE)){
                e.setCancelled(true);
                ItemStack hat = e.getPlayer().getInventory().getHelmet();
                e.getPlayer().getInventory().setHelmet(item);
                e.getPlayer().getInventory().setItemInMainHand(hat);
            }
        }
    }

    private void initAfkTask() {
        new BukkitRunnable() {

            @Override
            public void run() {
                for (Map.Entry<Player, Integer> player : playerMap.entrySet()) {
                    player.setValue(player.getValue().intValue() + 6);
                    if (playerMap.get(player.getKey()) > afkTime) {
                        ApplicableRegionSet regions = RegionCheck.getRegions(player.getKey().getLocation());
                        for (ProtectedRegion rg : regions) {
                            if (rg.getId().equals("spawn")) {
                                player.setValue(0);
                                return;
                            }
                        }
                        player.getKey().teleport(spawnLoc);
                        MessageConverter.sendConfMessage(player.getKey(), "afk_message");
                        player.setValue(0);
                    } else if (playerMap.get(player.getKey()) > (afkTime - 30) && playerMap.get(player.getKey()) < (afkTime - 23)) {
                        MessageConverter.sendConfMessage(player.getKey(), "afk_warn");
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
                    if (!player.hasPermission("essentials.fly") && !player.hasPermission("ce.nervedfly")) {
                        if (player.getAllowFlight() && player.getGameMode() == GameMode.SURVIVAL) {
                            if (!RegionCheck.hasFlag(player.getLocation(), "fly")) {
                                player.setAllowFlight(false);
                                player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 30 * 20, 100, false, false));
                                MessageConverter.sendConfMessage(player, "fly_expired");
                            }
                        }
                    }
                }
            }
        }.runTaskTimer(ChaosEssentials.getPlugin(), 300L, 30 * 20L);

    }

    private void initNervedFly() {
        new BukkitRunnable() {

            @Override
            public void run() {
                for (Map.Entry<String, Integer> entry : nervedFlyPlayers.entrySet()) {
                    Player p = Bukkit.getServer().getPlayer(UUID.fromString(entry.getKey()));
                    if (p != null) {
                        if (!p.hasPermission("essentials.fly")) {
                            if (p.isFlying() && !RegionCheck.hasFlag(p.getLocation(), "fly")) {
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
                                if ((i & 1) == 1)
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
        }.runTaskTimerAsynchronously(ChaosEssentials.getPlugin(), 150, 20);
    }

    public Queue<PlayerDeath> getDeaths(String key) {
        return playerDeaths.get(key);
    }
}
