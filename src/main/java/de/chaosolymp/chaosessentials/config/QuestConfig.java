package de.chaosolymp.chaosessentials.config;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class QuestConfig {
    private static File file;
    private static FileConfiguration customFile;

    public static boolean setup() {
        file = new File(Bukkit.getServer().getPluginManager().getPlugin("ChaosEssentials").getDataFolder(), "quest.yml");
        boolean exist = false;
        if (!file.exists()) {
            try {
                file.createNewFile();
                Bukkit.getLogger().info("Creating File");
            } catch (IOException e) {
                Bukkit.getLogger().info(e.toString());
            }
        } else {
            exist = true;
        }

        customFile = YamlConfiguration.loadConfiguration(file);
        init();
        return exist;
    }

    public static FileConfiguration get() {
        return customFile;
    }

    public static void save() {
        try {
            customFile.save(file);
        } catch (IOException e) {
            System.out.println("Couldn't save file");
        }
    }

    private static void init() {
        List<String> permsCollect = new ArrayList<>();
        permsCollect.add("daily.applecollect");
        permsCollect.add("daily.pumpkinharvest");
        permsCollect.add("daily.wheatharvest");
        permsCollect.add("daily.herbcollect");

        List<String> permsHunt = new ArrayList<>();

        permsHunt.add("daily.hoglinslay");
        permsHunt.add("daily.fishkoi");
        permsHunt.add("daily.npcdeliver");
        permsHunt.add("daily.findbankrotis");

        get().addDefault("daily.collect", permsCollect);
        get().addDefault("daily.hunt", permsHunt);

        get().options().copyDefaults(true);
    }
}
