package de.chaosolymp.chaosessentials.listener;

import com.nisovin.shopkeepers.api.events.ShopkeeperTradeEvent;
import com.nisovin.shopkeepers.api.ui.UISession;
import de.chaosolymp.chaosessentials.ChaosEssentials;
import de.chaosolymp.chaosessentials.command.SoulboundCommand;
import de.chaosolymp.chaosessentials.config.ShopConfig;
import de.chaosolymp.chaosessentials.config.TokenPresetConfig;
import de.chaosolymp.chaosessentials.tokens.TokenCreator;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;

public class ShopkeeperListener implements Listener {

    private HashSet<Player> lockedPlayers = new HashSet<>();

    @EventHandler
    public void onShop(ShopkeeperTradeEvent e){
        if(!e.isCancelled()){
            ConfigurationSection section = ShopConfig.get().getConfigurationSection(e.getShopkeeper().getIdString());
            for(String key : section.getKeys(false)) {
                if (section != null) {
                    ItemStack target = ItemStack.deserialize(section.getConfigurationSection(key+".item").getValues(true));
                    if (e.getTradingRecipe().getResultItem().isSimilar(target)) {
                        if (lockedPlayers.contains(e.getPlayer()) || e.getClickEvent().getClick().isShiftClick()) {
                            e.setCancelled(true);
                            e.getClickEvent().setCancelled(true);
                            return;
                        }
                        lockedPlayers.add(e.getPlayer());
                        new BukkitRunnable() {

                            @Override
                            public void run() {
                                ConfigurationSection tokenSection = TokenPresetConfig.get().getConfigurationSection(section.getString(key+".token"));
                                TokenCreator.tokenize(e.getClickEvent().getCursor(),
                                        e.getPlayer(),
                                        tokenSection.getString("command"),
                                        tokenSection.getBoolean("is-multi-use"),
                                        LocalDate.ofEpochDay(LocalDate.now().toEpochDay() + tokenSection.getInt("valid-days")));

                                if (tokenSection.getBoolean("soulbound")) {
                                    SoulboundCommand.bindToSoul(e.getClickEvent().getCursor(), e.getPlayer().getUniqueId().toString());
                                }
                                lockedPlayers.remove(e.getPlayer());
                            }
                        }.runTaskAsynchronously(ChaosEssentials.getPlugin());
                        break;
                    }
                }
            }
        }
    }
}
