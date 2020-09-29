package de.chaosolymp.chaosessentials.command;

import de.chaosolymp.chaosessentials.ChaosEssentials;
import de.chaosolymp.chaosessentials.config.BuyConfig;
import de.chaosolymp.chaosessentials.perks.AbstractPurchasable;
import de.chaosolymp.chaosessentials.util.MessageConverter;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class BuyCommand extends AbstractPurchasable implements CommandExecutor {

    private final FileConfiguration buyConfig = BuyConfig.get();

    public BuyCommand() {
        buildGui();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player player = null;

        if (sender instanceof Player)
            player = (Player) sender;
        else
            sender.sendMessage("Muss als Spieler ausgeführt werden!");

        if (player.hasPermission("ce.buy")) {
            openInventory(player);
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
                processPurchase((Player) player, null, String.valueOf(getItems().get(e.getSlot())));
            }

        }.runTaskAsynchronously(ChaosEssentials.getPlugin());

    }


}
