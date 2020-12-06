package de.chaosolymp.chaosessentials.command;

import de.chaosolymp.chaosessentials.tokens.Token;
import de.chaosolymp.chaosessentials.tokens.TokenCreator;
import de.chaosolymp.chaosessentials.util.MessageConverter;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class TokenCommand implements CommandExecutor {
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
                            for (int i = 3; i < args.length; i++) {
                                cmd += " " + args[i];
                            }
                            DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.GERMANY);
                            TokenCreator.tokenize(item, player, cmd, Boolean.getBoolean(args[1]), LocalDate.parse(args[2],df));
                        }
                    }
                    case "check": {
                        if (player.hasPermission("ce.token.check")) {
                            if (item.getType() != Material.AIR) {
                                Token token = TokenCreator.getToken(item);
                                if (token != null) {
                                    if (TokenCreator.isValid(token)) {
                                        MessageConverter.sendMessage(player, "&aToken ist valid");
                                    } else {
                                        MessageConverter.sendMessage(player, "&cToken is not Valid");
                                        player.sendMessage("&cToken is not Valid");
                                        if(token.getRedeem() != null){
                                            MessageConverter.sendMessage(player,"&cUsed &6+"+token.getRedeem().toString());
                                            MessageConverter.sendMessage(player,token.getRedeemUuid());
                                        }
                                    }
                                    player.sendMessage("Token has UUID: " + token.getUuid());
                                    MessageConverter.sendMessage(player,token.getCommand());
                                    MessageConverter.sendMessage(player,String.valueOf(token.isMultiUse()));
                                    MessageConverter.sendMessage(player,token.getValidUntil().toString());
                                }
                                else{
                                    MessageConverter.sendMessage(player,"&cThis is not a Token!");
                                }
                            }
                        }
                    }
                }
            }


        }
        return false;
    }
}
