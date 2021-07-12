package de.chaosolymp.chaosessentials.listener;

import com.gmail.nossr50.events.experience.McMMOPlayerLevelUpEvent;
import de.chaosolymp.chaosessentials.ChaosEssentials;
import de.chaosolymp.chaosessentials.util.MessageConverter;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.List;

public class McmmoListener implements Listener {

    @EventHandler
    public void onMcmmoLevelup(McMMOPlayerLevelUpEvent e){
        int level = e.getSkillLevel();
        int rewardLevel = ChaosEssentials.getPlugin().getConfig().getInt("mcmmo-reward-lvl");
        if(level % rewardLevel == 0) {
            List<String> cmds = ChaosEssentials.getPlugin().getConfig().getStringList("mcmmo-reward-cmd");
            int delay = 1;
            for(String cmd : cmds){
                Bukkit.getScheduler().runTaskLater(ChaosEssentials.getPlugin(),() -> {
                    Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(),
                            cmd.replaceFirst("%player%", e.getPlayer().getName()));
                },delay*20);

                delay++;
            }
            MessageConverter.sendConfMessage(e.getPlayer(),"mcmmo-reward-msg");
        }

        if(level % (rewardLevel*2) == 0){
            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(),
                    "token give "+e.getPlayer().getName()+" titanbox");
        }
    }
}
