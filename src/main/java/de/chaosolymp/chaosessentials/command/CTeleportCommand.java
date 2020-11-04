package de.chaosolymp.chaosessentials.command;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;

public class CTeleportCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        final Player player;

        if (sender instanceof Player) {
            player = (Player) sender;

            if (player.hasPermission("ce.deaths")) {
                Location loc = null;
                if (args.length == 6) {
                    loc = new Location(Bukkit.getWorld(args[0]), Float.parseFloat(args[1]), Float.parseFloat(args[2]), Float.parseFloat(args[3]),
                            Float.parseFloat(args[4]), Float.parseFloat(args[5]));
                } else if (args.length == 4) {
                    loc = new Location(Bukkit.getWorld(args[0]), Integer.parseInt(args[1]), Integer.parseInt(args[2]), Integer.parseInt(args[3]));
                }
                if (args.length > 4)
                    player.teleport(loc, PlayerTeleportEvent.TeleportCause.PLUGIN);
            }
        }
        return false;
    }
}
