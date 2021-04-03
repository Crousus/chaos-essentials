package de.chaosolymp.chaosessentials.util;

import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

import java.sql.Timestamp;


public class PlayerDeath {

    private String uuid;
    private String name;
    private String deathMsg;
    private int newXp;
    private Location[] lastLocations;
    private ItemStack[] lostItems;
    private ItemStack[] lostArmor;
    private Timestamp deathTime;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDeathMsg() {
        return deathMsg;
    }

    public void setDeathMsg(String deathMsg) {
        this.deathMsg = deathMsg;
    }

    public int getNewXp() {
        return newXp;
    }

    public void setNewXp(int newXp) {
        this.newXp = newXp;
    }

    public Location[] getLastLocations() {
        return lastLocations;
    }

    public void setLastLocations(Location[] lastLocations) {
        this.lastLocations = lastLocations;
    }

    public ItemStack[] getLostItems() {
        return lostItems;
    }

    public void setLostItems(ItemStack[] lostItems) {
        this.lostItems = lostItems;
    }

    public ItemStack[] getLostArmor() {
        return lostArmor;
    }

    public void setLostArmor(ItemStack[] lostArmor) {
        this.lostArmor = lostArmor;
    }

    public Timestamp getDeathTime() {
        return deathTime;
    }

    public void setDeathTime(Timestamp deathTime) {
        this.deathTime = deathTime;
    }

}
