package de.chaosolymp.chaosessentials.util;

import de.chaosolymp.chaosessentials.ChaosEssentials;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;

public class BackPack implements Listener {

    private static HashSet<Player> lockedPlayers = new HashSet<>();

    private Inventory inv;
    private ItemStack backPack;
    private Player owner;
    private NamespacedKey backpackKey;
    private NamespacedKey contentKey;

    public BackPack(ItemStack backPack, Player owner){

        this.owner = owner;
        this.backPack = backPack;

        if(lockedPlayers.contains(owner))
            return;
        lockedPlayers.add(owner);

        Bukkit.getScheduler().runTaskAsynchronously(ChaosEssentials.getPlugin(), () -> {
            backpackKey = new NamespacedKey(ChaosEssentials.getPlugin(),"backpack");
            contentKey = new NamespacedKey(ChaosEssentials.getPlugin(),"content");

            int size = Integer.parseInt(backPack.getItemMeta().getPersistentDataContainer().get(backpackKey,PersistentDataType.STRING));
            if(size == 0)
                return;

            ItemMeta meta = owner.getInventory().getItemInMainHand().getItemMeta();
            this.inv = Bukkit.createInventory(owner, size*9, ChatColor.translateAlternateColorCodes('&', meta.getDisplayName()));

            try {
                if (meta.getPersistentDataContainer().get(contentKey, PersistentDataType.STRING) != null) {
                    ItemStack[] contents = itemStackArrayFromBase64(meta.getPersistentDataContainer().get(contentKey, PersistentDataType.STRING));
                    int i = 0;
                    for (ItemStack stack : contents) {
                        if (stack != null)
                            inv.setItem(i, stack);
                        i++;
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
            Bukkit.getScheduler().runTask(ChaosEssentials.getPlugin(),() -> {
                owner.playSound(owner.getLocation(), Sound.BLOCK_WOOL_BREAK,2,0.5f);
                Bukkit.getPluginManager().registerEvents(this, ChaosEssentials.getPlugin());
                owner.openInventory(inv);
            });
        });

    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e){
        if(e.getPlayer() == owner){
            e.getHandlers().unregister(this);
            saveInv();
            lockedPlayers.remove(e.getPlayer());
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e){
        if(e.getCursor() == backPack){
            e.setCancelled(true);
            return;
        }
        if(e.getClickedInventory() == inv){
            saveInv();
        }
    }

    private void saveInv() {
        Bukkit.getScheduler().runTaskAsynchronously(ChaosEssentials.getPlugin(),()->{
            synchronized (this) {
                System.out.println("saving");
                ItemMeta meta = backPack.getItemMeta();
                NamespacedKey key = new NamespacedKey(ChaosEssentials.getPlugin(), "backpack");

                if (meta.getPersistentDataContainer().get(key, PersistentDataType.STRING) != null) {
                    NamespacedKey contentKey = new NamespacedKey(ChaosEssentials.getPlugin(), "content");
                    meta.getPersistentDataContainer().set(contentKey, PersistentDataType.STRING, BackPack.toBase64(inv));
                    backPack.setItemMeta(meta);
                }
            }
        });
    }

    public static String toBase64(Inventory inventory) throws IllegalStateException {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);

            // Write the size of the inventory
            dataOutput.writeInt(inventory.getSize());

            // Save every element in the list
            for (int i = 0; i < inventory.getSize(); i++) {
                dataOutput.writeObject(inventory.getItem(i));
            }

            // Serialize that array
            dataOutput.close();
            return Base64Coder.encodeLines(outputStream.toByteArray());
        } catch (Exception e) {
            throw new IllegalStateException("Unable to save item stacks.", e);
        }
    }

    public static ItemStack[] itemStackArrayFromBase64(String data) throws IOException {
        try {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decodeLines(data));
            BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);
            ItemStack[] items = new ItemStack[dataInput.readInt()];

            // Read the serialized inventory
            for (int i = 0; i < items.length; i++) {
                items[i] = (ItemStack) dataInput.readObject();
            }

            dataInput.close();
            return items;
        } catch (ClassNotFoundException e) {
            throw new IOException("Unable to decode class type.", e);
        }
    }


}
