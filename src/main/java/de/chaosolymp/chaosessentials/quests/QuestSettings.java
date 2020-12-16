package de.chaosolymp.chaosessentials.quests;

import org.bukkit.Sound;
import org.bukkit.inventory.ItemStack;

public class QuestSettings {
    private String itemType;
    private String quest;
    private int stage;
    private int type;
    private long replant_time;
    private String region;
    private Sound sound;
    private ItemStack item;

    public String getItemType() {
        return itemType;
    }

    public void setItemType(String itemType) {
        this.itemType = itemType;
    }

    public String getQuest() {
        return quest;
    }

    public void setQuest(String quest) {
        this.quest = quest;
    }

    public int getStage() {
        return stage;
    }

    public void setStage(int stage) {
        this.stage = stage;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public long getReplant_time() {
        return replant_time;
    }

    public void setReplant_time(long replant_time) {
        this.replant_time = replant_time;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public Sound getSound() {
        return sound;
    }

    public void setSound(Sound sound) {
        this.sound = sound;
    }

    public void setSound(String sound) {
        this.sound = Sound.valueOf(sound);
    }

    public ItemStack getItem() {
        return item;
    }

    public void setItem(ItemStack item) {
        this.item = item;
    }

    @Override
    public String toString() {
        return "QuestSettings{" +
                "quest='" + quest + '\'' +
                ", stage=" + stage +
                ", type=" + type +
                ", replant_time=" + replant_time +
                ", region='" + region + '\'' +
                ", sound=" + sound +
                ", item=" + item +
                '}';
    }
}
