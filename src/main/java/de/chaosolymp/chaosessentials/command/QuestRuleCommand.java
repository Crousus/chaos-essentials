package de.chaosolymp.chaosessentials.command;

import de.chaosolymp.chaosessentials.ChaosEssentials;
import de.chaosolymp.chaosessentials.config.QuestConfig;
import de.chaosolymp.chaosessentials.listener.HarvestListener;
import de.chaosolymp.chaosessentials.util.MessageConverter;
import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class QuestRuleCommand implements CommandExecutor {
    private final String[] menu = new String[]{"quest", "stage", "type", "replant_time", "region", "sound", "item_stack"};
    private final HarvestListener harvestListener;

    public QuestRuleCommand(HarvestListener harvestListener) {
        this.harvestListener = harvestListener;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        final Player player;

        if (sender instanceof Player) {
            player = (Player) sender;

            if (player.hasPermission("ce.questrule")) {
                if (args.length > 0) {
                    if (args[0].equals("add")) {
                        List<String> message = ChaosEssentials.getPlugin().getConfig().getStringList("rule_added");
                        String block = player.getInventory().getItemInMainHand().getType().toString();
                        String path = "item." + block;
                        if (QuestConfig.get().get(block) == null) {
                            MessageConverter.sendMessage(player, message.get(0));
                            MessageConverter.sendMessage(player, getMessage(message, "block").replaceAll("%block%", block));
                            for (int i = 1; i < args.length; i++) {
                                if (NumberUtils.isNumber(args[i]))
                                    QuestConfig.get().set(path + "." + menu[i - 1], Integer.parseInt(args[i]));
                                else
                                    QuestConfig.get().set(path + "." + menu[i - 1], args[i].replaceAll("/s", " "));

                                String msg = getMessage(message, menu[i - 1]);
                                if (msg != null)
                                    MessageConverter.sendMessage(player, msg.replaceAll("%" + menu[i - 1] + "%", args[i]));
                            }
                            QuestConfig.get().set(path + "." + menu[menu.length - 1], player.getInventory().getItemInMainHand().serialize());
                        } else
                            MessageConverter.sendConfMessage(player, "rule_exists");
                    } else if (args[0].equals("del")) {
                        if (QuestConfig.get().get(args[1].toUpperCase()) != null) {
                            QuestConfig.get().set("item." + args[1].toUpperCase(), null);
                            MessageConverter.sendConfMessage(player, "rule_delete");
                        }
                    }
                    QuestConfig.save();
                    QuestConfig.setup();
                    harvestListener.cacheSettings();
                }
            }
        }
        return false;
    }

    private String getMessage(List<String> message, String part) {
        for (String msgPart : message) {
            if (msgPart.contains("%" + part + "%"))
                return msgPart;
        }
        return null;
    }
}
