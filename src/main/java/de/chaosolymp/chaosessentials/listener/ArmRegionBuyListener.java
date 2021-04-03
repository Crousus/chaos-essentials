package de.chaosolymp.chaosessentials.listener;

import net.alex9849.arm.events.PreBuyEvent;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class ArmRegionBuyListener implements Listener {
    @EventHandler
    public void onBuyRegion(PreBuyEvent e) {
        if (e.getRegion().getRegion().getId().startsWith("su"))
            Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "aach add 1 Custom.ShopPurchased " + e.getBuyer().getName());
    }
}
