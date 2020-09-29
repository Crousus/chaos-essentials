package de.chaosolymp.chaosessentials.command;

import de.chaosolymp.chaosessentials.ChaosEssentials;
import de.chaosolymp.chaosessentials.DatabaseController;
import de.chaosolymp.chaosessentials.chestshoplog.AverageResponse;
import de.chaosolymp.chaosessentials.util.MessageConverter;
import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

public class AverageCommand implements CommandExecutor {
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        final Player player;

        if (sender instanceof Player) {
            player = (Player) sender;

            if (player.hasPermission("ce.average")) {

                new BukkitRunnable() {
                    @Override
                    public void run() {
                        DatabaseController db = new DatabaseController();
                        AverageResponse response;
                        String specialName = "";
                        String item = "";
                        int days = 0;
                        int count = 1;
                        boolean isItemSet = false;
                        boolean isCountSet = false;

                        Queue<String> argQueue = new LinkedList<>();
                        argQueue.addAll(Arrays.asList(args));

                        while (!argQueue.isEmpty()) {
                            if (!NumberUtils.isDigits(argQueue.peek())) {
                                if (argQueue.peek().toLowerCase().matches("([0-9])+d")) {
                                    days = Integer.parseInt(argQueue.peek().substring(0, argQueue.poll().length() - 1));
                                } else if (!isItemSet) {
                                    item = argQueue.poll();
                                    isItemSet = true;
                                } else {
                                    specialName = ChatColor.translateAlternateColorCodes('&', argQueue.poll());
                                }
                            } else {
                                count = Integer.parseInt(argQueue.poll());
                                isCountSet = true;
                            }
                        }

                        ItemStack handItem = player.getInventory().getItemInMainHand();

                        if (!isItemSet || !isCountSet) {
                            if (handItem.getType() != Material.AIR) {
                                if (!isItemSet) {
                                    item = handItem.getType().toString();
                                }
                                if (!isCountSet) {
                                    count = handItem.getAmount();
                                }
                                if (handItem.hasItemMeta()) {
                                    specialName = handItem.getItemMeta().getDisplayName();
                                }
                            } else {
                                MessageConverter.sendConfMessage(player, "no_item");
                                return;
                            }
                        }

                        response = db.getAverage(item.toUpperCase(), specialName, days);

                        if (!specialName.equals("")) {
                            item += " \"" + specialName + "\"";
                        }

                        String msg = ChaosEssentials.getPlugin().getConfig().getString("average");
                        msg = msg.replaceAll("%item%", item).
                                replaceAll("%itemcount%", String.valueOf(count)).
                                replaceAll("%avg%", String.valueOf(response.getTotalAverage(count))).
                                replaceAll("%count%", String.valueOf(response.getCount()));

                        MessageConverter.sendMessage(player, msg);

                    }
                }.runTaskAsynchronously(ChaosEssentials.getPlugin());

                return true;
            }
            MessageConverter.sendNoPermission(player);
        }
        return false;
    }

}
