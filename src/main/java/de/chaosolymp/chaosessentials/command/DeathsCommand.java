package de.chaosolymp.chaosessentials.command;

import de.chaosolymp.chaosessentials.ChaosEssentials;
import de.chaosolymp.chaosessentials.gui.AbstractGui;
import de.chaosolymp.chaosessentials.listener.PlayerListener;
import de.chaosolymp.chaosessentials.util.MessageConverter;
import de.chaosolymp.chaosessentials.util.PlayerDeath;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class DeathsCommand implements CommandExecutor {
    private final PlayerListener playerDeaths;
    private Object currentGui;
    private Object lastGui;

    public DeathsCommand(PlayerListener playerDeaths) {
        this.playerDeaths = playerDeaths;
        Bukkit.getPluginManager().registerEvents(new PlayerCloseInvListener(),ChaosEssentials.getPlugin());
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        final Player player;

        if (sender instanceof Player) {
            player = (Player) sender;

            if (player.hasPermission("ce.deaths")) {
                if(args.length > 0){
                    int length = (int) Math.ceil(ChaosEssentials.getPlugin().getConfig().getInt("deaths-save-amount") / 9.0);
                    DeathGui gui = new DeathGui(length,ChaosEssentials.getPlugin().getConfig().getString("deaths-gui.overview-name").replaceAll("%player%",args[0]));
                    gui.handleDeathGui(args[0],player);
                    currentGui = gui;
                }
            }
        }
        return false;
    }

    private void handleDeathView(String suspect, Player player){

    }

    class DeathGui extends AbstractGui{

        private String suspect;

        protected DeathGui(int size, String name) {
            super(size, name);
            Bukkit.getPluginManager().registerEvents(this,ChaosEssentials.getPlugin());
        }

        protected void handleDeathGui(String suspect, Player player){
            this.suspect = suspect;
            Queue<PlayerDeath> deaths = playerDeaths.getDeaths(suspect);
            FileConfiguration config = ChaosEssentials.getPlugin().getConfig();
            for(PlayerDeath death: deaths){
                ItemStack guiItem = new ItemStack(Material.SKELETON_SKULL);
                ItemMeta meta = guiItem.getItemMeta();
                meta.setDisplayName(ChatColor.translateAlternateColorCodes('&',config.getString("deaths-gui.overview-item.name").replaceAll("%time%",death.getDeathTime().toString())));
                List<String> lore = config.getStringList("deaths-gui.overview-item.lore");
                for(int i = 0; i < lore.size(); i++){
                    String line = lore.get(i);
                    line = line.replaceAll("%world%",death.getLastLocations()[death.getLastLocations().length-1].getWorld().getName());
                    line = line.replaceAll("%lvl%",String.valueOf(death.getNewXp()));
                    line = line.replaceAll("%msg%",death.getDeathMsg());
                    line = ChatColor.translateAlternateColorCodes('&',line);
                    lore.set(i,line);
                }
                meta.setLore(lore);
                guiItem.setItemMeta(meta);
                addItemToGui(guiItem);
            }
            openInventory(player);
            lastGui = currentGui;
            currentGui = this;
        }

        @Override
        public void passInventoryClick(InventoryClickEvent e) {
            int slot = e.getSlot();
            FileConfiguration config = ChaosEssentials.getPlugin().getConfig();
            Queue<PlayerDeath> deaths = playerDeaths.getDeaths(suspect);
            PlayerDeath death = (PlayerDeath) deaths.toArray()[slot];
            DeathDetailGui deathDetailGui = new DeathDetailGui((int) Math.ceil((this.getInv().getSize()+39)/9.0),
                    config.getString("deaths-gui.overview-name").replaceAll("%player%",suspect),death,this);

            ItemStack item = new ItemStack(Material.CHEST);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(ChatColor.translateAlternateColorCodes('&',config.getString("deaths-gui.inv-item")));
            item.setItemMeta(meta);
            deathDetailGui.setItemToGui(item,3);

            item = new ItemStack(Material.IRON_CHESTPLATE);
            meta = item.getItemMeta();
            meta.setDisplayName(ChatColor.translateAlternateColorCodes('&',config.getString("deaths-gui.armor-item")));
            item.setItemMeta(meta);
            deathDetailGui.setItemToGui(item,5);

            for(int i = 9; i < 18;i++){
                deathDetailGui.setItemToGui(new ItemStack(Material.BLACK_STAINED_GLASS_PANE),i);
            }
            
            item = new ItemStack(Material.BARRIER);
            meta = item.getItemMeta();
            meta.setDisplayName(ChatColor.translateAlternateColorCodes('&',"&cZurück"));
            item.setItemMeta(meta);
            deathDetailGui.setItemToGui(item,13);

            for(int i = 18; i < death.getLastLocations().length+18; i++){
                Location loc = death.getLastLocations()[i-18];
                item = new ItemStack(Material.COMPASS);
                meta = item.getItemMeta();
                meta.setDisplayName(ChatColor.translateAlternateColorCodes('&',ChaosEssentials.getPlugin().getConfig().getString("deaths-gui.pos-item")).replaceAll("%index%",""+(i-17)));
                item.setItemMeta(meta);
                deathDetailGui.setItemToGui(item,i);
            }
            e.getWhoClicked().closeInventory();
            e.getHandlers().unregister(this);
            deathDetailGui.openInventory(e.getWhoClicked());
            lastGui = currentGui;
            currentGui = deathDetailGui;
        }

    }

    class DeathDetailGui extends AbstractGui {

        private PlayerDeath death;
        private AbstractGui parent;

        protected DeathDetailGui(int size, String name, PlayerDeath death, AbstractGui parent) {
            super(size, name);
            this.death = death;
            this.parent = parent;
            Bukkit.getPluginManager().registerEvents(this,ChaosEssentials.getPlugin());
        }

        @Override
        public void passInventoryClick(InventoryClickEvent e) {
            Inventory inv = null;
            if(e.getSlot() == 3) {
                inv = Bukkit.createInventory(null, 45, ChatColor.translateAlternateColorCodes('&', "&4Todesinventar"));
                inv.setContents(death.getLostItems());
                e.getWhoClicked().openInventory(inv);
            }

            else if(e.getSlot() == 5) {
                inv = Bukkit.createInventory(null, 9, ChatColor.translateAlternateColorCodes('&', "&4Todesrüstung"));
                inv.setContents(death.getLostArmor());
                e.getWhoClicked().openInventory(inv);
            }
            else if(e.getSlot() == 13) {
                parent.openInventory(e.getWhoClicked());
                Bukkit.getPluginManager().registerEvents(parent,ChaosEssentials.getPlugin());
                }
            else if (e.getSlot() > 17 && e.getSlot() < death.getLastLocations().length+17){
                e.getWhoClicked().closeInventory();
                e.getWhoClicked().teleport(death.getLastLocations()[e.getSlot()-18]);
            }
            if(inv != null){
                lastGui = currentGui;
                currentGui = inv;
            }
        }

    }

    class PlayerCloseInvListener implements Listener {

        @EventHandler
        public void onPlayerInvClose(InventoryCloseEvent e){
            if(currentGui instanceof Inventory && e.getInventory() == currentGui){
                if(lastGui != null && lastGui instanceof AbstractGui)
                    new BukkitRunnable(){
                        @Override
                        public void run() {
                            ((AbstractGui) lastGui).openInventory(e.getPlayer());
                            Bukkit.getPluginManager().registerEvents((AbstractGui) lastGui, ChaosEssentials.getPlugin());
                            currentGui = lastGui;
                            lastGui = e.getInventory();
                        }
                    }.runTaskLater(ChaosEssentials.getPlugin(),2L);
            }
        }

    }

}
