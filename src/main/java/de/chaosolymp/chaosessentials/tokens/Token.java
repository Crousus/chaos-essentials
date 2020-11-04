package de.chaosolymp.chaosessentials.tokens;

import org.bukkit.entity.Player;

public class Token {
    private Player player;
    private String uuid;
    private String command;

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }
}
