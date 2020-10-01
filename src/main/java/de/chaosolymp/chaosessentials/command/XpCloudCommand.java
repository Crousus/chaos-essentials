package de.chaosolymp.chaosessentials.command;

import de.chaosolymp.chaosessentials.ChaosEssentials;
import de.chaosolymp.chaosessentials.util.MessageConverter;
import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class XpCloudCommand implements CommandExecutor {

    private boolean isRunning = false;
    private int times;

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        final Player player;

        if (sender instanceof Player) {
            player = (Player) sender;

            if (player.hasPermission("ce.xpcloud")) {

                if(args.length > 0 && NumberUtils.isNumber(args[0])) {
                    Location loc = player.getLocation();
                    times = 200;
                    int rate = 2;
                    if (args.length > 1 && NumberUtils.isNumber(args[1]))
                        times = Integer.parseInt(args[1]);
                    int radius = Integer.parseInt(args[0]);
                    if(args.length > 2 && NumberUtils.isNumber(args[2])){
                        rate = rate > 0 ? Integer.parseInt(args[2]) : 1;
                    }
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            for(short i = 0; i <=3; i++){
                                Location newLoc = new Location(loc.getWorld(), loc.getX() + Math.random() * (radius * 2) - radius, loc.getY(), loc.getZ() + Math.random() * (radius * 2) - radius);
                                newLoc.getWorld().spawnEntity(newLoc, EntityType.THROWN_EXP_BOTTLE);
                                newLoc.getWorld().spawnParticle(Particle.CLOUD,newLoc,25,3f,0.2f,3f,0);
                            }
                            if(times <= 0){
                                this.cancel();
                            }
                            times--;
                        }
                    }.runTaskTimer(ChaosEssentials.getPlugin(), 0, rate);
                }
            }
            else {
                MessageConverter.sendNoPermission(player);
            }
        }
        return true;
    }
}
