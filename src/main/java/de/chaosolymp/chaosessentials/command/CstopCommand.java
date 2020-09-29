package de.chaosolymp.chaosessentials.command;

import de.chaosolymp.chaosessentials.ChaosEssentials;
import de.chaosolymp.chaosessentials.util.MessageConverter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class CstopCommand implements CommandExecutor {
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        final Player player;

        if (sender instanceof Player) {
            player = (Player) sender;

            if (!player.hasPermission("ce.cstop")) {
                MessageConverter.sendNoPermission(player);
                return false;
            }
        }

        Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', ChaosEssentials.getPlugin().getConfig().getString("restart")));
        for (Player p : Bukkit.getOnlinePlayers()) {
            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "sudo " + p.getName() + " warp " +
                    ChaosEssentials.getPlugin().getConfig().getString("targetWarp"));
        }
        new BukkitRunnable() {
            @Override
            public void run() {
                Bukkit.getServer().shutdown();
            }
        }.runTaskLater(ChaosEssentials.getPlugin(), 20);

        return false;
    }

}
