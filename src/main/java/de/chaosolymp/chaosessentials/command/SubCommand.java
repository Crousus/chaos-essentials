package de.chaosolymp.chaosessentials.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public interface SubCommand {

    void executeCommand(Player player, String[] args);

    void executeCommand(CommandSender sender, String[] args);
}
