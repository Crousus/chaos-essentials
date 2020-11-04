package de.chaosolymp.chaosessentials.command;

import de.chaosolymp.chaosessentials.util.MessageConverter;
import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashSet;

public class PipeSignCommand implements CommandExecutor, Listener {

    private final HashSet<Player> players = new HashSet<>();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        final Player player;

        if (sender instanceof Player) {
            player = (Player) sender;

            if (player.hasPermission("ce.pipesign")) {
                if (!players.contains(player)) {
                    players.add(player);
                    MessageConverter.sendConfMessage(player, "pipe_enabled");
                } else {
                    players.remove(player);
                    MessageConverter.sendConfMessage(player, "pipe_disabled");
                }
            } else {
                MessageConverter.sendNoPermission(player);
            }
        }
        return false;
    }

    @EventHandler
    public void onSignCreation(SignChangeEvent e) {
        Sign s = (Sign) e.getBlock().getState();
        if (players.contains(e.getPlayer())) {
            e.setCancelled(true);
            s.setLine(1, "[Pipe]");
            s.update();
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        players.remove(e.getPlayer());
    }
}
