package de.chaosolymp.chaosessentials.tokens;

import org.bukkit.entity.Player;

import java.sql.Timestamp;

public class Token {
    private Player player;
    private String uuid;
    private String command;
    private Timestamp creation;
    private Timestamp redeem;

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

    public Timestamp getCreation() {
        return creation;
    }

    public void setCreation(Timestamp creation) {
        this.creation = creation;
    }

    public Timestamp getRedeem() {
        return redeem;
    }

    public void setRedeem(Timestamp redeem) {
        this.redeem = redeem;
    }
}
