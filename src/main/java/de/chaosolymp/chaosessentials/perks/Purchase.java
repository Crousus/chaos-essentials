package de.chaosolymp.chaosessentials.perks;

import org.bukkit.entity.Player;

import java.sql.Timestamp;

public class Purchase {

    private Player target;
    private Player sender;
    private int duration;
    private double price;
    private String command;
    private int cooldown;
    private Timestamp time;
    private String[] servers;
    private String[] worlds;
    private String type;

    public Player getTarget() {
        return target;
    }

    public void setTarget(Player target) {
        this.target = target;
    }

    public Player getSender() {
        return sender;
    }

    public void setSender(Player sender) {
        this.sender = sender;
    }

    public int getDuration() {
        return duration;
    }

    public String getDurationString() {
        return String.valueOf(duration);
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getCommand() {
        command = command == null ? "" : command;
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public int getCooldown() {
        return cooldown;
    }

    public void setCooldown(int cooldown) {
        this.cooldown = cooldown;
    }

    public Timestamp getTime() {
        return time;
    }

    public void setTime(Timestamp time) {
        this.time = time;
    }

    public String[] getServers() {
        return servers;
    }

    public void setServers(String[] servers) {
        this.servers = servers;
    }

    public String[] getWorlds() {
        return worlds;
    }

    public void setWorlds(String[] worlds) {
        this.worlds = worlds;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public long getRemainingCooldown() {
        if (time == null || cooldown == 0)
            return -1;
        return time.getTime() + cooldown * 3600000 - System.currentTimeMillis();
    }
}
