package de.chaosolymp.chaosessentials.listener;

import com.Acrobot.ChestShop.ORMlite.stmt.query.In;
import de.chaosolymp.chaosessentials.ChaosEssentials;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class GuiManager implements Listener {
    private HashMap<Inventory, HashSet<Integer>> blockedGuis = new HashMap<>();
    private HashMap<String, Inventory> keepOpenGuis = new HashMap<>();
    private HashMap<String, Inventory> scheduledPlayers = new HashMap<>();
    private HashMap<Inventory, HashSet<Integer>> dropOnCloseGuis = new HashMap<>();
    private static GuiManager instance;

    private GuiManager(){
        Bukkit.getPluginManager().registerEvents(this,ChaosEssentials.getPlugin());
    }

    public static GuiManager getInstance(){
        if(instance == null)
            instance = new GuiManager();
        return instance;
    }

    public void registerGui(Inventory inv){
        blockedGuis.put(inv, null);
    }

    public void registerGui(Inventory inv, HashSet<Integer> enabledSlots){
        blockedGuis.put(inv, enabledSlots);
    }

    public void registerOpenGui(String uuid, Inventory inv){
        keepOpenGuis.put(uuid,inv);
    }

    public void unregisterGui(Inventory inv){
        blockedGuis.remove(inv);
    }

    public void unregisterOpenGui(String uuid){
        keepOpenGuis.remove(uuid);
    }

    public void registerDropGui(Inventory inv, HashSet<Integer> slots) {
        dropOnCloseGuis.put(inv,slots);
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e){
        if(keepOpenGuis.containsKey(e.getPlayer().getUniqueId().toString()) && keepOpenGuis.get(e.getPlayer().getUniqueId().toString()) == e.getInventory()){
            Bukkit.getScheduler().runTaskLater(ChaosEssentials.getPlugin(), () -> e.getPlayer().openInventory(e.getInventory()),2L);
        }
        else {
            blockedGuis.remove(e.getInventory());
        }

        //Drop Items on Ground if the Player did not take them
        if (dropOnCloseGuis.containsKey(e.getInventory())) {
            Inventory inv = e.getInventory();
            for (int i : dropOnCloseGuis.get(inv)) {
                if(inv.getItem(i) != null && inv.getItem(i).getType() != Material.AIR)
                    e.getPlayer().getLocation().getWorld().dropItem(e.getPlayer().getLocation(), inv.getItem(i));
            }
            dropOnCloseGuis.remove(inv);
        }
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent e){
        if(blockedGuis.containsKey(e.getInventory())){
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e){
        if(blockedGuis.containsKey(e.getClickedInventory())){
            if(blockedGuis.get(e.getClickedInventory()) == null || !blockedGuis.get(e.getClickedInventory()).contains(e.getSlot()))
                e.setCancelled(true);
        }
        else if(blockedGuis.containsKey(e.getWhoClicked().getOpenInventory().getTopInventory()) && (e.getAction() == InventoryAction.COLLECT_TO_CURSOR || e.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY))
            e.setCancelled(true);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e){
        if(keepOpenGuis.containsKey(e.getPlayer().getUniqueId().toString())){
            scheduledPlayers.put(e.getPlayer().getUniqueId().toString(),keepOpenGuis.get(e.getPlayer().getUniqueId().toString()));
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e){
        if(scheduledPlayers.containsKey(e.getPlayer().getUniqueId().toString())){
            e.getPlayer().openInventory(scheduledPlayers.get(e.getPlayer().getUniqueId().toString()));
            scheduledPlayers.remove(e.getPlayer().getUniqueId().toString());
        }
    }

}
