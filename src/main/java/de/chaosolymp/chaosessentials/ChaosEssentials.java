package de.chaosolymp.chaosessentials;

import de.chaosolymp.chaosessentials.command.*;
import de.chaosolymp.chaosessentials.config.BuyConfig;
import de.chaosolymp.chaosessentials.config.QuestConfig;
import de.chaosolymp.chaosessentials.config.RandomTpConfig;
import de.chaosolymp.chaosessentials.config.VariableConfig;
import de.chaosolymp.chaosessentials.listener.*;
import de.chaosolymp.chaosessentials.tabcomplete.DeathsTabCompleter;
import de.chaosolymp.chaosessentials.tabcomplete.VariableTabCompleter;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class ChaosEssentials extends JavaPlugin {

    private static ChaosEssentials plugin;
    private Economy econ;
    private HarvestListener harvestListener;

    public void onEnable() {
        plugin = this;

        BuyConfig.setup();
        BuyConfig.get().options().copyDefaults(true);
        BuyConfig.save();
        BuyConfig.reload();

        RandomTpConfig.setup();
        RandomTpConfig.get().options().copyDefaults(true);
        RandomTpConfig.save();

        VariableConfig.setup();

        QuestConfig.setup();
        QuestConfig.save();

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
        getCommand("xpcloud").setExecutor(new XpCloudCommand());

        getCommand("token").setExecutor(new CreateTokenCommand());
        getCommand("ctp").setExecutor(new CTeleportCommand());


        PipeSignCommand pipe = new PipeSignCommand();
        harvestListener = new HarvestListener();

        getCommand("pipe").setExecutor(pipe);
        getCommand("questrule").setExecutor(new QuestRuleCommand(harvestListener));

        if (Bukkit.getPluginManager().getPlugin("MaSuiteWarps") != null)
            getCommand("cstop").setExecutor(new CstopCommand());
        else
            this.getLogger().info("[ChaosEssentials] MasuiteWarps not found");

        if(Bukkit.getPluginManager().getPlugin("CraftBook5") != null){
            getCommand("var").setExecutor(new VariableCommand());
            getCommand("var").setTabCompleter(new VariableTabCompleter());
        }

        Bukkit.getPluginManager().registerEvents(new ExcavatorListener(), this);
        Bukkit.getPluginManager().registerEvents(new MultiToolListener(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerJoinAndLeaveListener(), this);
        Bukkit.getPluginManager().registerEvents(new RandomTeleportListener(), this);
        Bukkit.getPluginManager().registerEvents(new XpSpammerListener(), this);
        //Bukkit.getPluginManager().registerEvents(new TokenListener(), this);
        Bukkit.getPluginManager().registerEvents(pipe, this);
        Bukkit.getPluginManager().registerEvents(harvestListener, this);
        //Bukkit.getPluginManager().registerEvents(new CollectListener(), this);

        getCommand("deaths").setTabCompleter(new DeathsTabCompleter());
        getCommand("creload").setExecutor(new ReloadCommand(harvestListener));

        PlayerListener playerListener = new PlayerListener();
        Bukkit.getPluginManager().registerEvents(playerListener, this);
        getCommand("deaths").setExecutor(new DeathsCommand(playerListener));
    }

    public void onDisable(){
        Bukkit.getScheduler().cancelTasks(plugin);
        harvestListener.replantAll();
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

    public static void log(String msg) {
        Bukkit.getLogger().info("[ChaosEssentials] " + msg);
    }

}
