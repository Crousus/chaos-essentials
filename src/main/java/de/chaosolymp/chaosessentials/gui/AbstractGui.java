package de.chaosolymp.chaosessentials.gui;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public abstract class AbstractGui implements Listener {
    private final Inventory inv;

    protected AbstractGui(int size, String name) {
        size *= 9;
        final int rest = size % 9;
        if (rest != 0)
            size += rest;
        if (size > 54)
            size = 54;
        else if (size <= 0)
            size = 9;

        this.inv = Bukkit.createInventory(null, size, ChatColor.translateAlternateColorCodes('&', name));

    }

    public void addItemToGui(ItemStack item) {
        inv.addItem(item);
    }

    public void setItemToGui(ItemStack item, int pos) {
        inv.setItem(pos, item);
    }


    protected ItemStack createGuiItem(final Material material, final String name, final List<String> lore) {
        final ItemStack item = new ItemStack(material, 1);
        final ItemMeta meta = item.getItemMeta();

        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));

        meta.setLore(lore);

        item.setItemMeta(meta);

        return item;
    }

    @EventHandler
    public void onInventoryClick(final InventoryClickEvent e) {
        if (e.getInventory() != inv) return;

        e.setCancelled(true);

        final ItemStack clickedItem = e.getCurrentItem();

        if (clickedItem == null || clickedItem.getType() == Material.AIR)
            return;
        passInventoryClick(e);
    }

    public void openInventory(final HumanEntity ent) {
        ent.openInventory(inv);
    }

    public void passInventoryClick(InventoryClickEvent e) {
    }

    @EventHandler
    public void onInventoryClick(final InventoryDragEvent e) {
        if (e.getInventory() == inv) {
            e.setCancelled(true);
        }
    }
}
