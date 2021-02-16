package de.chaosolymp.chaosessentials.command;

import de.chaosolymp.chaosessentials.ChaosEssentials;
import de.chaosolymp.chaosessentials.config.BuyConfig;
import de.chaosolymp.chaosessentials.config.QuestConfig;
import de.chaosolymp.chaosessentials.config.RandomTpConfig;
import de.chaosolymp.chaosessentials.config.VariableConfig;
import de.chaosolymp.chaosessentials.listener.HarvestListener;
import de.chaosolymp.chaosessentials.util.MessageConverter;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class ReloadCommand implements CommandExecutor {
    private HarvestListener harvestListener;
    public ReloadCommand(HarvestListener harvestListener){
        this.harvestListener = harvestListener;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        final Player player;

        if (sender instanceof Player) {
            player = (Player) sender;

            if (player.hasPermission("ce.reload")) {
                new BukkitRunnable(){

                    @Override
                    public void run() {
                        BuyConfig.setup();
                        QuestConfig.setup();
                        RandomTpConfig.setup();
                        VariableConfig.setup();
                        ChaosEssentials.getPlugin().saveConfig();
                        harvestListener.cacheSettings();
                        MessageConverter.sendMessage(player,"&aAll plugin configs reloaded!");
                    }
                }.runTaskAsynchronously(ChaosEssentials.getPlugin());
            }
        }
        return false;
    }
}
