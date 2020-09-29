package de.chaosolymp.chaosessentials.command;

import de.chaosolymp.chaosessentials.ChaosEssentials;
import de.chaosolymp.chaosessentials.events.CommandEvent;
import de.chaosolymp.chaosessentials.util.MessageConverter;
import org.bukkit.Bukkit;
import org.bukkit.Statistic;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;

public class RestCommand implements CommandExecutor {

    private final HashSet<Player> sleeping;

    public RestCommand() {
        sleeping = new HashSet<Player>();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        final Player player;

        if (sender instanceof Player) {
            player = (Player) sender;

            if (player.hasPermission("ce.use.rest") || player.hasPermission("ce.rest")) {
                if (player.getWorld().getTime() < 12000) {
                    MessageConverter.sendConfMessage(player, "sleep_only_at_night");
                    return false;
                }

                final boolean isSpleepingIgnored = player.isSleepingIgnored();
                player.setSleepingIgnored(true);

                sleeping.add(player);

                if (Bukkit.getOnlinePlayers().size() - sleeping.size() <= 0) {
                    player.getWorld().setTime(0);
                    player.getWorld().setStorm(false);
                }

                MessageConverter.sendConfMessage(player, "sleeping");
                player.setStatistic(Statistic.TIME_SINCE_REST, 0);

                if (!player.hasPermission("ce.rest")) {
                    CommandEvent event = new CommandEvent(player, "rest");
                    Bukkit.getPluginManager().callEvent(event);
                }
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        player.setSleepingIgnored(isSpleepingIgnored);
                        sleeping.remove(player);
                        MessageConverter.sendConfMessage(player, "sleeping_end");
                    }
                }.runTaskLater(ChaosEssentials.getPlugin(), 15 * 20L);
            } else {
                if (player.hasPermission("ce.buy"))
                    MessageConverter.sendPurchaseable(player);
                else
                    MessageConverter.sendNoPermission(player);
            }
        }
        return false;
    }
}
