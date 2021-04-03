package de.chaosolymp.chaosessentials.config;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class TokenPresetConfig {

    private static File file;
    private static FileConfiguration customFile;

    public static boolean setup() {
        file = new File(Bukkit.getServer().getPluginManager().getPlugin("ChaosEssentials").getDataFolder(), "tokenpresets.yml");
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

    public static void reload() {
        customFile = YamlConfiguration.loadConfiguration(file);
    }
}
