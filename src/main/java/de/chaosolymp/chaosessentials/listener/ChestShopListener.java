package de.chaosolymp.chaosessentials.listener;

import com.Acrobot.ChestShop.Events.TransactionEvent;
import de.chaosolymp.chaosessentials.ChaosEssentials;
import de.chaosolymp.chaosessentials.DatabaseController;
import de.chaosolymp.chaosessentials.chestshoplog.ChestShopPurchase;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class ChestShopListener implements Listener {
    @EventHandler
    public void onChestShopTransaction(final TransactionEvent e) {
        if (!e.getClient().hasPermission("ce.bypass.shoplogging")) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    int amount = Integer.parseInt(e.getSign().getLine(1));

                    ItemStack item = e.getStock()[0];

                    DatabaseController db = new DatabaseController();
                    ChestShopPurchase p = new ChestShopPurchase();
                    p.setClient(e.getClient().getUniqueId().toString());
                    p.setOwner(e.getOwnerAccount().getUuid().toString());
                    p.setPrice(e.getExactPrice().doubleValue());
                    p.setItem(e.getStock()[0].getType().toString());
                    p.setQuantity(e.getStock()[0].getAmount());
                    p.setType(e.getTransactionType());
                    p.setSpecialName(item.getItemMeta().getDisplayName());

                    p.setSpecial(item.hasItemMeta());

                    db.addTransaction(p);
                }
            }.runTaskAsynchronously(ChaosEssentials.getPlugin());
        }
    }
}
