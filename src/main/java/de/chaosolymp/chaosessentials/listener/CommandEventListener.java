package de.chaosolymp.chaosessentials.listener;


import de.chaosolymp.chaosessentials.ChaosEssentials;
import de.chaosolymp.chaosessentials.DatabaseController;
import de.chaosolymp.chaosessentials.config.BuyConfig;
import de.chaosolymp.chaosessentials.events.CommandEvent;
import de.chaosolymp.chaosessentials.util.EditPermission;
import de.chaosolymp.chaosessentials.util.MessageConverter;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

public class CommandEventListener implements Listener {
    @EventHandler
    public void onCommandExecute(CommandEvent e) {
        new BukkitRunnable() {
            @Override
            public void run() {
                DatabaseController db = new DatabaseController();
                if (!db.takeCommandUse(e.getCommand(), e.getExecutor())) {
                    EditPermission edit = new EditPermission();
                    for (String s : BuyConfig.get().getStringList("commandlist." + e.getCommand() + ".permission_node"))
                        edit.removePermission(e.getExecutor(), s);

                    MessageConverter.sendConfMessage(e.getExecutor(), "last_use");
                }
            }
        }.runTaskAsynchronously(ChaosEssentials.getPlugin());
    }
}
