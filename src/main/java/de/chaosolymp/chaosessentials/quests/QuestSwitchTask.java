package de.chaosolymp.chaosessentials.quests;

import de.chaosolymp.chaosessentials.ChaosEssentials;
import de.chaosolymp.chaosessentials.config.QuestConfig;
import de.chaosolymp.chaosessentials.util.EditPermission;
import org.bukkit.scheduler.BukkitRunnable;

import java.time.Clock;
import java.time.LocalTime;
import java.util.List;

public class QuestSwitchTask {

    public static void runPermissionTimer(boolean isInitial) {
        long timer = 0;
        if (isInitial) {
            LocalTime.now(Clock.systemDefaultZone());
            LocalTime localTime = LocalTime.now();
            timer = 24 * 3600 - localTime.getHour() * 3600;
            timer += 60 * 60 - localTime.getMinute() * 60;
            timer += 60 - localTime.getSecond();
        } else {
            timer = 24 * 3600;
        }

        ChaosEssentials.log("Scheduled next Permission update in: " + timer);

        new BukkitRunnable() {

            @Override
            public void run() {
                String[] path = new String[]{"daily.collect", "daily.hunt"};
                for (int i = 0; i < 2; i++) {
                    List<String> nodes = QuestConfig.get().getStringList(path[i]);
                    String node = nodes.get((int) (Math.random() * nodes.size()));
                    EditPermission editPermission = new EditPermission();
                    editPermission.addGroupPermission("default", node);
                }
                QuestSwitchTask.runPermissionTimer(false);
            }
        }.runTaskLater(ChaosEssentials.getPlugin(), timer * 20);
    }
}
