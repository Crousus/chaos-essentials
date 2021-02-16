package de.chaosolymp.chaosessentials.listener;

import de.chaosolymp.chaosessentials.ChaosEssentials;
import de.chaosolymp.chaosessentials.config.RandomTpConfig;
import de.chaosolymp.chaosessentials.util.MessageConverter;
import de.chaosolymp.chaosessentials.util.TeleportTask;
import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.List;

public class RandomTeleportListener implements Listener {
    final private HashMap<String, Long> players = new HashMap<>();

    @EventHandler
    public void onRightClick(PlayerInteractEvent e) {
        if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
            Block block = e.getClickedBlock();
            if (block.getState() instanceof Sign) {
                if (e.getPlayer().hasPermission("ce.randomtp")) {
                    Sign sign = (Sign) block.getState();
                    if (checkSign(sign)) {
                        if (players.containsKey(e.getPlayer().getUniqueId().toString())) {
                            long time = players.get(e.getPlayer().getUniqueId().toString()) + (RandomTpConfig.get().getInt("worldsettings." + block.getWorld().getName() + ".cooldown") * 1000) - System.currentTimeMillis();
                            if (players.containsKey(e.getPlayer()) && time > 0) {
                                String msg = ChaosEssentials.getPlugin().getConfig().getString("tp_cooldown").replaceAll("%time%", String.valueOf(Math.round(time / 1000)));
                                MessageConverter.sendMessage(e.getPlayer(), msg);
                                return;
                            }
                        }
                        new BukkitRunnable() {

                            @Override
                            public void run() {
                                int distance = teleportRandom(block, e.getPlayer());
                                MessageConverter.sendMessage(e.getPlayer(), ChaosEssentials.getPlugin().getConfig().getString("teleported").replaceAll("%distance%", "" + distance));
                                players.remove(e.getPlayer().getUniqueId().toString());
                                players.put(e.getPlayer().getUniqueId().toString(), System.currentTimeMillis());
                            }
                        }.runTaskAsynchronously(ChaosEssentials.getPlugin());
                        e.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 6 * 20, 50, false, false));


                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                players.remove(e.getPlayer().getUniqueId().toString());
                            }
                        }.runTaskLater(ChaosEssentials.getPlugin(), RandomTpConfig.get().getInt("worldsettings." + block.getWorld().getName() + ".cooldown") * 20L);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onSignCreation(SignChangeEvent e) {
        if (e.getLine(1).equalsIgnoreCase("[randomtp]") && NumberUtils.isNumber(e.getLine(2))) {
            if (e.getPlayer().hasPermission("ce.create.randomtp")) {
                int i = 0;
                RandomTpConfig.get().addDefault("worldsettings", e.getBlock().getWorld().getName());
                RandomTpConfig.get().addDefault("worldsettings." + e.getBlock().getWorld().getName() + ".radius", Integer.parseInt(e.getLine(2)));
                if (NumberUtils.isNumber(e.getLine(3)))
                    RandomTpConfig.get().addDefault("worldsettings." + e.getBlock().getWorld().getName() + ".cooldown", Integer.parseInt(e.getLine(3)));
                for (String s : ChaosEssentials.getPlugin().getConfig().getStringList("rtp_sign")) {
                    e.setLine(i, ChatColor.translateAlternateColorCodes('&', s));
                    i++;
                }
                RandomTpConfig.get().options().copyDefaults(true);
                RandomTpConfig.save();
                e.getBlock().getState().update();
            } else {
                e.setCancelled(true);
            }

        }
    }

    private boolean checkSign(Sign sign) {
        int i = 0;
        List<String> configSign = ChaosEssentials.getPlugin().getConfig().getStringList("rtp_sign");
        for (String s : sign.getLines()) {
            if (!s.equals(ChatColor.translateAlternateColorCodes('&', configSign.get(i))))
                return false;
            i++;
        }
        return true;
    }

    private int teleportRandom(Block block, Player player) {
        World world = block.getWorld();
        int radius = RandomTpConfig.get().getInt("worldsettings." + world.getName() + ".radius");
        Location original = player.getLocation();
        int i = 0;
        while (i < 60) {
            int x = (int) (block.getLocation().getX() - (((Math.random() * 2) - 1) * radius));
            int z = (int) (block.getLocation().getZ() - (((Math.random() * 2) - 1) * radius));
            int y = 100;

            for (; y > 10; y--) {
                if (!world.getBlockAt(x, y, z).isEmpty()) {
                    if (isBlockSafe(world.getBlockAt(x, y, z))) {
                        Location destination = new Location(world, x + 0.5, y + 1, z + 0.5);
                        if (world.getBlockAt(x, y + 1, z).isEmpty() && isBlockSafe(world.getBlockAt(x, y + 1, z)) && world.getBlockAt(x, y + 2, z).isEmpty() && isBlockSafe(world.getBlockAt(x, y + 2, z))) {
                            Bukkit.getScheduler().runTask(ChaosEssentials.getPlugin(), new TeleportTask(destination, player));
                            return (int) destination.distance(original);
                        }
                    }
                }
            }
        }
        MessageConverter.sendConfMessage(player, "nodestination");
        return 0;
    }

    private boolean isBlockSafe(Block block) {
        Material m = block.getType();
        return m != Material.LAVA && m != Material.MAGMA_BLOCK && m != Material.FIRE && m != Material.CACTUS && m != Material.CAMPFIRE;
    }


}
