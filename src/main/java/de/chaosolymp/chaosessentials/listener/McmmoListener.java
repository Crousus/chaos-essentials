package de.chaosolymp.chaosessentials.listener;

import com.gmail.nossr50.events.experience.McMMOPlayerLevelUpEvent;
import de.chaosolymp.chaosessentials.ChaosEssentials;
import de.chaosolymp.chaosessentials.util.MessageConverter;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class McmmoListener implements Listener {

    @EventHandler
    public void onMcmmoLevelup(McMMOPlayerLevelUpEvent e){
        if(e.getSkillLevel() % ChaosEssentials.getPlugin().getConfig().getInt("mcmmo-reward-lvl") == 0) {
            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(),
                    ChaosEssentials.getPlugin().getConfig().getString("mcmmo-reward-cmd").replaceFirst("%player%", e.getPlayer().getName()));
            MessageConverter.sendConfMessage(e.getPlayer(),"mcmmo-reward-msg");
        }
    }
}
