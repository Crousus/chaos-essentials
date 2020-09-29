package de.chaosolymp.chaosessentials.util;

import de.chaosolymp.chaosessentials.ChaosEssentials;
import de.chaosolymp.chaosessentials.perks.Purchase;
import org.apache.commons.lang.StringUtils;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.concurrent.TimeUnit;

public class PlaceholderReplacer {

    public static String replaceAll(Purchase data, String msg) {
        FileConfiguration config = ChaosEssentials.getPlugin().getConfig();

        msg = StringUtils.replaceEach(msg, new String[]{"%duration%", "%command%", "%cooldown%", "%price%"}, new String[]{
                data.getDurationString(),
                data.getCommand(),
                String.valueOf(data.getCooldown()),
                String.valueOf(data.getPrice())});

        if (data.getSender() != null)
            msg = msg.replaceAll("%player%", data.getSender().getName());

        if (data.getTarget() != null)
            msg = msg.replaceAll("%target%", data.getTarget().getName());

        long time = 0;
        if (data.getTime() != null) {
            if (data.getTime().getTime() > (System.currentTimeMillis() - (long) data.getCooldown() * 3600000L)) {
                time = data.getTime().getTime() - (System.currentTimeMillis() - (long) data.getCooldown() * 3600000L);
            }

            int days = 0;
            if (msg.contains("%time%")) {
                String timestring = "";

                days = (int) TimeUnit.MILLISECONDS.toDays(time);
                int hours = (int) TimeUnit.MILLISECONDS.toHours(time - 86400000L * days);
                int minutes = (int) TimeUnit.MILLISECONDS.toMinutes(time - 86400000L * days - 3600000L * hours);

                if (days > 0)
                    timestring = timestring + days + config.getString("timeformat.days");
                if (days != 0 || hours != 0)
                    timestring = timestring + hours + config.getString("timeformat.hours");

                timestring = timestring + minutes + config.getString("timeformat.minutes");
                msg = msg.replace("%time%", timestring);

            }
        }
        return msg;
    }


}
