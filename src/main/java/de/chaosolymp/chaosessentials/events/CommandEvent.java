package de.chaosolymp.chaosessentials.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class CommandEvent extends Event {
    private final Player player;
    private final String command;
    private static final HandlerList handlers = new HandlerList();

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public CommandEvent(Player player, String command) {
        this.player = player;
        this.command = command;
    }

    public Player getExecutor() {
        return player;
    }

    public String getCommand() {
        return command;
    }
}
