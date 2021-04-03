package de.chaosolymp.chaosessentials.command;

import de.chaosolymp.chaosessentials.ChaosEssentials;
import de.chaosolymp.chaosessentials.util.MessageConverter;
import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RollCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if(commandSender instanceof Player){
            Player player = (Player) commandSender;
            if(!player.hasPermission("ce.roll")) {
                MessageConverter.sendNoPermission(player);
                return false;
            }
            int max = 100;

            if(strings.length > 0 && NumberUtils.isNumber(strings[0]))
                max = Integer.parseInt(strings[0]) > 1 ? Integer.parseInt(strings[0]) : 100;
            int roll = (int) (Math.random()*max+1);

            String message = ChaosEssentials.getPlugin().getConfig().getString("rolls")
                    .replaceAll("%player%",player.getName())
                    .replaceAll("%roll%", String.valueOf(roll))
                    .replaceAll("%total%", 1+"-"+max);
            message = ChatColor.translateAlternateColorCodes('&',message);

            for(Player target : Bukkit.getOnlinePlayers()){
                if(target.getLocation().distance(player.getLocation()) < 50){
                    target.sendMessage(message);
                }
            }


        }
        return false;
    }
}
