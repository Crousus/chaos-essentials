package de.chaosolymp.chaosessentials.command;

import de.chaosolymp.chaosessentials.tokens.TagEditor;
import de.chaosolymp.chaosessentials.tokens.TokenCreator;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class CreateTokenCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        final Player player;

        if (sender instanceof Player) {
            player = (Player) sender;

            if (args.length > 0) {
                ItemStack item = player.getInventory().getItemInMainHand();
                switch (args[0]) {
                    case "create": {
                        if (player.hasPermission("ce.token.create")) {
                            String cmd = "";
                            for (int i = 1; i < args.length; i++) {
                                cmd += " " + args[i];
                            }
                            TokenCreator.tokenize(item, player, cmd);
                        }
                    }
                    case "check": {
                        if (player.hasPermission("ce.token.check")) {
                            if (item.getType() != Material.AIR) {
                                TagEditor editor = new TagEditor(item);
                                String uuid = editor.getTag();
                                player.sendMessage("Token has UUID: " + uuid);
                            }
                        }
                    }
                }
            }


        }
        return false;
    }
}
