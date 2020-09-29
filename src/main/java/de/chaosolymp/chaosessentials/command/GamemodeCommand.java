package de.chaosolymp.chaosessentials.command;

import de.chaosolymp.chaosessentials.util.MessageConverter;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GamemodeCommand implements CommandExecutor {
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        final Player player;

        if (sender instanceof Player) {
            player = (Player) sender;

            if (player.hasPermission("ce.gamemode"))
                player.setGameMode(GameMode.CREATIVE);
            else
                MessageConverter.sendNoPermission(player);
        }
        return true;
    }

}
