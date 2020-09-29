package de.chaosolymp.chaosessentials;

import de.chaosolymp.chaosessentials.command.*;
import de.chaosolymp.chaosessentials.config.BuyConfig;
import de.chaosolymp.chaosessentials.config.RandomTpConfig;
import de.chaosolymp.chaosessentials.listener.*;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class ChaosEssentials extends JavaPlugin {

    private static ChaosEssentials plugin;
    private Economy econ;

    public void onEnable() {
        plugin = this;

        if (BuyConfig.buildConfig())
            BuyConfig.get().options().copyDefaults(true);
        BuyConfig.save();

        RandomTpConfig.setup();
        RandomTpConfig.get().options().copyDefaults(true);
        RandomTpConfig.save();

        this.saveDefaultConfig();

        DatabaseController db = new DatabaseController();
        if (db.createTables())
            this.getLogger().info("Tables created!");

        //register Buy Commands
        if (setupEconomy() && DatabaseProvider.isConnected()) {
            BuyCommand buy = new BuyCommand();
            GiftCommand gift = new GiftCommand();

            this.getCommand("buy").setExecutor(buy);
            this.getCommand("gift").setExecutor(gift);

            Bukkit.getPluginManager().registerEvents(buy, this);
            Bukkit.getPluginManager().registerEvents(gift, this);
            Bukkit.getPluginManager().registerEvents(new CommandEventListener(), this);
            if (Bukkit.getPluginManager().getPlugin("ChestShop") != null)
                Bukkit.getPluginManager().registerEvents(new ChestShopListener(), this);
            else
                this.getLogger().info("[ChaosEssentials] ChestShop not found");
        }

        //register Arm Region Buy Listener if ARM is installed
        if (Bukkit.getPluginManager().getPlugin("AdvancedRegionMarket") != null)
            Bukkit.getPluginManager().registerEvents(new ArmRegionBuyListener(), this);
        else
            this.getLogger().info("[ChaosEssentials] ARM not found");

        getCommand("hat").setExecutor(new HatCommand());
        getCommand("rest").setExecutor(new RestCommand());
        getCommand("launch").setExecutor(new LaunchCommand());
        getCommand("boat").setExecutor(new BoatCommand());
        getCommand("average").setExecutor(new AverageCommand());
        getCommand("crea").setExecutor(new GamemodeCommand());

        if (Bukkit.getPluginManager().getPlugin("MaSuiteWarps") != null)
            getCommand("cstop").setExecutor(new CstopCommand());
        else
            this.getLogger().info("[ChaosEssentials] MasuiteWarps not found");

        Bukkit.getPluginManager().registerEvents(new ExcavatorListener(), this);
        Bukkit.getPluginManager().registerEvents(new MultiToolListener(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerJoinAndLeaveListener(), this);
        Bukkit.getPluginManager().registerEvents(new RandomTeleportListener(), this);

    }

    public static ChaosEssentials getPlugin() {
        return plugin;
    }

    private boolean setupEconomy() {
        if (Bukkit.getPluginManager().getPlugin("Vault") == null) {
            return false;
        }

        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }

    public Economy getEconomy() {
        return econ;
    }

}
