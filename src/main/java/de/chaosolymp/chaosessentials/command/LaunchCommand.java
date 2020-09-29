package de.chaosolymp.chaosessentials.command;

import de.chaosolymp.chaosessentials.ChaosEssentials;
import de.chaosolymp.chaosessentials.events.CommandEvent;
import de.chaosolymp.chaosessentials.util.MessageConverter;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;


public class LaunchCommand implements CommandExecutor {

    private final HashSet<Player> players;

    public LaunchCommand() {
        this.players = new HashSet<>();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        final Player player;

        if (sender instanceof Player) {
            player = (Player) sender;

            if (player.hasPermission("ce.use.launch") || player.hasPermission("ce.launch")) {
                if (players.contains(player)) {
                    MessageConverter.sendConfMessage(player, "launch_cd");
                    return false;
                }

                players.add(player);
                player.setVelocity(player.getVelocity().setY(10.0));
                player.addScoreboardTag("falling");
                MessageConverter.sendConfMessage(player, "launch");

                if (!player.hasPermission("ce.hat")) {
                    CommandEvent event = new CommandEvent(player, "launch");
                    Bukkit.getPluginManager().callEvent(event);
                }
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        players.remove(player);
                        player.removeScoreboardTag("falling");
                    }
                }.runTaskLater(ChaosEssentials.getPlugin(), 150L);

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
