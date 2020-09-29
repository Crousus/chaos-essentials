package de.chaosolymp.chaosessentials.config;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class BuyConfig {

    private static File file;
    private static FileConfiguration customFile;

    public static boolean setup() {
        file = new File(Bukkit.getServer().getPluginManager().getPlugin("ChaosEssentials").getDataFolder(), "buycommands.yml");
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

    public static boolean buildConfig() {
        if (!BuyConfig.setup()) {
            List<String> nodes = Arrays.asList("essentials.fly", "essentials.fly.safelogin");

            BuyConfig.get().addDefault("commands", Arrays.asList("fly", "hat", "launch", "rest", "boat", "ptime", "pweather", "workbench", "enderchest"));

            //Add Fly command to Config
            BuyConfig.get().addDefault("commandlist.fly.duration", 24);
            BuyConfig.get().addDefault("commandlist.fly.price", 1111.0);
            BuyConfig.get().addDefault("commandlist.fly.cooldown", 48);
            BuyConfig.get().addDefault("commandlist.fly.servers", Arrays.asList("survival"));
            BuyConfig.get().addDefault("commandlist.fly.worlds", Arrays.asList());
            BuyConfig.get().addDefault("commandlist.fly.item_type", "ELYTRA");
            BuyConfig.get().addDefault("commandlist.fly.item_pos", 11);
            BuyConfig.get().addDefault("commandlist.fly.item_name", "Fly");
            BuyConfig.get().addDefault("commandlist.fly.type", true);
            BuyConfig.get().addDefault("commandlist.fly.additionalLore", "&8>> &7Kaufe dir den Befehl Fly");

            BuyConfig.get().addDefault("commandlist.fly.permission_node", nodes);


            nodes = Arrays.asList("ce.use.hat");
            BuyConfig.get().addDefault("commandlist.hat.duration", 30);
            BuyConfig.get().addDefault("commandlist.hat.price", 500.0);
            BuyConfig.get().addDefault("commandlist.hat.cooldown", 0);
            BuyConfig.get().addDefault("commandlist.hat.servers", Arrays.asList("survival", "farm"));
            BuyConfig.get().addDefault("commandlist.hat.worlds", Arrays.asList());
            BuyConfig.get().addDefault("commandlist.hat.item_type", "IRON_HELMET");
            BuyConfig.get().addDefault("commandlist.hat.item_pos", 12);
            BuyConfig.get().addDefault("commandlist.hat.item_name", "Hat");
            BuyConfig.get().addDefault("commandlist.hat.type", false);
            BuyConfig.get().addDefault("commandlist.hat.additionalLore", "&8>> &7Setze dir Items auf den Kopf");

            BuyConfig.get().addDefault("commandlist.hat.permission_node", nodes);

            nodes = Arrays.asList("ce.use.launch");
            BuyConfig.get().addDefault("commandlist.launch.duration", 10);
            BuyConfig.get().addDefault("commandlist.launch.price", 150.0);
            BuyConfig.get().addDefault("commandlist.launch.cooldown", 0);
            BuyConfig.get().addDefault("commandlist.launch.servers", Arrays.asList("survival", "farm"));
            BuyConfig.get().addDefault("commandlist.launch.worlds", Arrays.asList());
            BuyConfig.get().addDefault("commandlist.launch.item_type", "FIREWORK_ROCKET");
            BuyConfig.get().addDefault("commandlist.launch.item_pos", 13);
            BuyConfig.get().addDefault("commandlist.launch.item_name", "Launch");
            BuyConfig.get().addDefault("commandlist.launch.type", false);
            BuyConfig.get().addDefault("commandlist.launch.additionalLore", "&8>> &7Schieße dich in die Luft");

            BuyConfig.get().addDefault("commandlist.launch.permission_node", nodes);

            nodes = Arrays.asList("ce.use.rest");
            BuyConfig.get().addDefault("commandlist.rest.duration", 20);
            BuyConfig.get().addDefault("commandlist.rest.price", 1000.0);
            BuyConfig.get().addDefault("commandlist.rest.cooldown", 0);
            BuyConfig.get().addDefault("commandlist.rest.servers", Arrays.asList("survival", "farm"));
            BuyConfig.get().addDefault("commandlist.rest.worlds", Arrays.asList());
            BuyConfig.get().addDefault("commandlist.rest.item_type", "RED_BED");
            BuyConfig.get().addDefault("commandlist.rest.item_pos", 14);
            BuyConfig.get().addDefault("commandlist.rest.item_name", "Sleep");
            BuyConfig.get().addDefault("commandlist.rest.type", false);
            BuyConfig.get().addDefault("commandlist.rest.additionalLore", "&8>> &7Selber Effekt wie in einem Bett");

            BuyConfig.get().addDefault("commandlist.rest.permission_node", nodes);

            nodes = Arrays.asList("ce.use.boat");
            BuyConfig.get().addDefault("commandlist.boat.duration", 30);
            BuyConfig.get().addDefault("commandlist.boat.price", 100.0);
            BuyConfig.get().addDefault("commandlist.boat.cooldown", 0);
            BuyConfig.get().addDefault("commandlist.boat.servers", Arrays.asList("survival", "farm"));
            BuyConfig.get().addDefault("commandlist.boat.worlds", Arrays.asList());
            BuyConfig.get().addDefault("commandlist.boat.item_type", "OAK_BOAT");
            BuyConfig.get().addDefault("commandlist.boat.item_pos", 15);
            BuyConfig.get().addDefault("commandlist.boat.item_name", "Boot");
            BuyConfig.get().addDefault("commandlist.boat.type", false);
            BuyConfig.get().addDefault("commandlist.boat.additionalLore", "&8>> &7Spawne ein Boot vor dir");

            BuyConfig.get().addDefault("commandlist.boat.permission_node", nodes);

            nodes = Arrays.asList("essentials.ptime");
            BuyConfig.get().addDefault("commandlist.ptime.duration", 168);
            BuyConfig.get().addDefault("commandlist.ptime.price", 300);
            BuyConfig.get().addDefault("commandlist.ptime.cooldown", 0);
            BuyConfig.get().addDefault("commandlist.ptime.servers", Arrays.asList("survival", "farm"));
            BuyConfig.get().addDefault("commandlist.ptime.worlds", Arrays.asList());
            BuyConfig.get().addDefault("commandlist.ptime.item_type", "CLOCK");
            BuyConfig.get().addDefault("commandlist.ptime.item_pos", 20);
            BuyConfig.get().addDefault("commandlist.ptime.item_name", "Ptime");
            BuyConfig.get().addDefault("commandlist.ptime.type", true);
            BuyConfig.get().addDefault("commandlist.ptime.additionalLore", "&8>> &7Lasse fuer dich die Zeit anders erscheinen");

            BuyConfig.get().addDefault("commandlist.ptime.permission_node", nodes);

            nodes = Arrays.asList("essentials.pweather");
            BuyConfig.get().addDefault("commandlist.pweather.duration", 168);
            BuyConfig.get().addDefault("commandlist.pweather.price", 300);
            BuyConfig.get().addDefault("commandlist.pweather.cooldown", 0);
            BuyConfig.get().addDefault("commandlist.pweather.servers", Arrays.asList("survival", "farm"));
            BuyConfig.get().addDefault("commandlist.pweather.worlds", Arrays.asList());
            BuyConfig.get().addDefault("commandlist.pweather.item_type", "SUNFLOWER");
            BuyConfig.get().addDefault("commandlist.pweather.item_pos", 21);
            BuyConfig.get().addDefault("commandlist.pweather.item_name", "Pweather");
            BuyConfig.get().addDefault("commandlist.pweather.type", true);
            BuyConfig.get().addDefault("commandlist.pweather.additionalLore", "&8>> &7Lasse fuer dich das Wetter anders erscheinen");

            BuyConfig.get().addDefault("commandlist.pweather.permission_node", nodes);

            nodes = Arrays.asList("essentials.workbench");
            BuyConfig.get().addDefault("commandlist.workbench.duration", 336);
            BuyConfig.get().addDefault("commandlist.workbench.price", 300);
            BuyConfig.get().addDefault("commandlist.workbench.cooldown", 0);
            BuyConfig.get().addDefault("commandlist.workbench.servers", Arrays.asList("survival", "farm"));
            BuyConfig.get().addDefault("commandlist.workbench.worlds", Arrays.asList());
            BuyConfig.get().addDefault("commandlist.workbench.item_type", "CRAFTING_TABLE");
            BuyConfig.get().addDefault("commandlist.workbench.item_pos", 22);
            BuyConfig.get().addDefault("commandlist.workbench.item_name", "Workbench");
            BuyConfig.get().addDefault("commandlist.workbench.type", true);
            BuyConfig.get().addDefault("commandlist.workbench.additionalLore", "&8>> &7Habe immer eine Werkbank dabei");

            BuyConfig.get().addDefault("commandlist.workbench.permission_node", nodes);

            nodes = Arrays.asList("essentials.enderchest");
            BuyConfig.get().addDefault("commandlist.enderchest.duration", 336);
            BuyConfig.get().addDefault("commandlist.enderchest.price", 400);
            BuyConfig.get().addDefault("commandlist.enderchest.cooldown", 0);
            BuyConfig.get().addDefault("commandlist.enderchest.servers", Arrays.asList("survival", "farm"));
            BuyConfig.get().addDefault("commandlist.enderchest.worlds", Arrays.asList());
            BuyConfig.get().addDefault("commandlist.enderchest.item_type", "ENDER_CHEST");
            BuyConfig.get().addDefault("commandlist.enderchest.item_pos", 23);
            BuyConfig.get().addDefault("commandlist.enderchest.item_name", "Enderchest");
            BuyConfig.get().addDefault("commandlist.enderchest.type", true);
            BuyConfig.get().addDefault("commandlist.enderchest.additionalLore", "&8>> &7Oeffne ueberall und sofort deine Enderchest");

            BuyConfig.get().addDefault("commandlist.enderchest.permission_node", nodes);

            return true;
        } else {
            return false;
        }

    }
}
