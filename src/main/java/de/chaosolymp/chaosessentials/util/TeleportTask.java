package de.chaosolymp.chaosessentials.util;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;

public class TeleportTask implements Runnable {
    private final Location destination;
    private final Player player;

    public TeleportTask(final Location dest, final Player p) {
        destination = dest;
        player = p;
    }

    @Override
    public void run() {
        player.teleport(destination, PlayerTeleportEvent.TeleportCause.PLUGIN);
    }
}
