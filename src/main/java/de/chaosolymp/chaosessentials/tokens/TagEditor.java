package de.chaosolymp.chaosessentials.tokens;

import com.fasterxml.uuid.Generators;
import de.chaosolymp.chaosessentials.ChaosEssentials;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.sql.Timestamp;
import java.util.UUID;

public class TagEditor {

    private final ItemStack item;

    public TagEditor(ItemStack item) {
        this.item = item;
    }

    public String getTag() {
        NamespacedKey key = new NamespacedKey(ChaosEssentials.getPlugin(), "id");
        ItemMeta itemMeta = item.getItemMeta();
        PersistentDataContainer container = itemMeta.getPersistentDataContainer();

        if (container.has(key, PersistentDataType.STRING)) {
            return container.get(key, PersistentDataType.STRING);
        } else {
            return null;
        }
    }

    public String setTag() {
        NamespacedKey key = new NamespacedKey(ChaosEssentials.getPlugin(), "id");
        ItemMeta itemMeta = item.getItemMeta();
        UUID uuid = Generators.timeBasedGenerator().generate();
        System.out.println(new Timestamp(uuid.timestamp()).toString());
        itemMeta.getPersistentDataContainer().set(key, PersistentDataType.STRING, uuid.toString());
        item.setItemMeta(itemMeta);
        return uuid.toString();
    }
}
    