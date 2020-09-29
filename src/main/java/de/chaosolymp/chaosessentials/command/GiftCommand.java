package de.chaosolymp.chaosessentials.command;

import de.chaosolymp.chaosessentials.ChaosEssentials;
import de.chaosolymp.chaosessentials.config.BuyConfig;
import de.chaosolymp.chaosessentials.perks.AbstractPurchasable;
import de.chaosolymp.chaosessentials.util.MessageConverter;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class GiftCommand extends AbstractPurchasable implements CommandExecutor {

    private final FileConfiguration buyConfig = BuyConfig.get();
    private Player target;

    public GiftCommand() {
        buildGui();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player player = null;

        if (sender instanceof Player)
            player = (Player) sender;
        else
            sender.sendMessage("Muss als Spieler ausgeführt werden!");

        if (player.hasPermission("ce.gift")) {
            if (args.length > 0) {
                target = Bukkit.getPlayer(args[0]);
                if (target != null) {
                    if (target != player) {
                        openInventory(player);
                    } else {
                        MessageConverter.sendConfMessage(player, "TargetIsSender");
                    }
                } else {
                    MessageConverter.sendConfMessage(player, "playernotfound");
                }
            } else {
                MessageConverter.sendConfMessage(player, "usage.gift");
            }
        } else {
            MessageConverter.sendNoPermission(player);
        }
        return false;
    }

    @Override
    public void passInventoryClick(InventoryClickEvent e) {
        OfflinePlayer player = (OfflinePlayer) e.getWhoClicked();
        e.getWhoClicked().closeInventory();

        new BukkitRunnable() {
            @Override
            public void run() {
                processPurchase((Player) player, target, String.valueOf(getItems().get(e.getSlot())));
            }

        }.runTaskAsynchronously(ChaosEssentials.getPlugin());

    }


}
