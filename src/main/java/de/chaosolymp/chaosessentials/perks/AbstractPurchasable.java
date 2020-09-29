package de.chaosolymp.chaosessentials.perks;

import de.chaosolymp.chaosessentials.ChaosEssentials;
import de.chaosolymp.chaosessentials.DatabaseController;
import de.chaosolymp.chaosessentials.command.BuyCommand;
import de.chaosolymp.chaosessentials.command.GiftCommand;
import de.chaosolymp.chaosessentials.config.BuyConfig;
import de.chaosolymp.chaosessentials.gui.AbstractGui;
import de.chaosolymp.chaosessentials.util.EditPermission;
import de.chaosolymp.chaosessentials.util.MessageConverter;
import de.chaosolymp.chaosessentials.util.PlaceholderReplacer;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;

public abstract class AbstractPurchasable extends AbstractGui {

    private final ChaosEssentials plugin = ChaosEssentials.getPlugin();
    private final FileConfiguration buyConfig = BuyConfig.get();
    private Purchase purchase;
    private final HashMap items;

    protected AbstractPurchasable() {
        super(ChaosEssentials.getPlugin().getConfig().getInt("Inv_size"), ChaosEssentials.getPlugin().getConfig().getString("Inv_name"));
        items = new HashMap();
    }

    public void processPurchase(Player sender, Player target, String command) {
        purchase = new Purchase();
        if (this instanceof BuyCommand)
            target = sender;

        String path = "commandlist." + command;

        purchase.setSender(sender);
        purchase.setTarget(target);
        purchase.setPrice(buyConfig.getDouble(path + ".price"));
        purchase.setCooldown(buyConfig.getInt(path + ".cooldown"));
        purchase.setDuration(buyConfig.getInt(path + ".duration"));
        purchase.setCommand(command);
        if (this instanceof BuyCommand)
            purchase.setType("purchased");
        else if (this instanceof GiftCommand)
            purchase.setType("gifted");

        EditPermission permEdit = new EditPermission();
        for (String node : buyConfig.getStringList(path + ".permission_node")) {
            if (permEdit.hasPermission(target, node)) {
                if (this instanceof BuyCommand)
                    MessageConverter.sendConfMessage(sender, "has_perm_already");
                else
                    MessageConverter.sendConfMessage(sender, "target_has_perm_already");
                return;
            }
        }

        DatabaseController db = new DatabaseController(purchase);
        db.getPurchase();
        long remaining = purchase.getRemainingCooldown();
        if (remaining <= 0) {

            if (!plugin.getEconomy().has(sender, purchase.getPrice())) {
                sendMessage(sender, "notenoughmoney");
                return;
            }

            plugin.getEconomy().withdrawPlayer(sender, purchase.getPrice());
            List<String> servers = buyConfig.getStringList(path + ".servers");
            List<String> worlds = buyConfig.getStringList(path + ".worlds");

            sender.playSound(sender.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 0.5f);

            int duration = -1;
            if (buyConfig.getBoolean(path + ".type"))
                duration = buyConfig.getInt(path + ".duration");
            for (String node : buyConfig.getStringList(path + ".permission_node")) {
                System.out.println(node);
                permEdit.addPermission(target, node, duration, servers, worlds);
            }

            db.addPurchase();

            if (sender != target) {
                sendMessage(sender, "giftconfirmation_self");
                sendMessage(target, "giftconfirmation_receiver");
            } else {
                sendMessage(sender, "buyconfirmation");
            }
        } else {
            if (sender != target)
                sendMessage(sender, "oncooldown_self");
            else
                sendMessage(sender, "oncooldown");
        }
    }

    public void buildGui() {
        FileConfiguration buyConfig = BuyConfig.get();
        for (String c : buyConfig.getStringList("commands")) {
            Material m = Material.matchMaterial(buyConfig.getString("commandlist." + c + ".item_type"));

            List<String> lore = plugin.getConfig().getStringList("item.lore");

            String servers = buyConfig.getStringList("commandlist." + c + ".servers").toString();
            String worlds = buyConfig.getStringList("commandlist." + c + ".worlds").toString();

            servers = servers.replace("[", "").replace("]", "");
            worlds = worlds.replace("[", "").replace("]", "");

            String[] loreReplace = new String[]{
                    buyConfig.getString("commandlist." + c + ".duration"),
                    buyConfig.getString("commandlist." + c + ".price"),
                    servers,
                    worlds,
                    buyConfig.getString("commandlist." + c + ".cooldown"),
                    ChatColor.translateAlternateColorCodes('&', buyConfig.getString("commandlist." + c + ".additionalLore"))};

            if (!buyConfig.getBoolean("commandlist." + c + ".type")) {
                lore.set(0, ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("item.lore_uses")));
            } else if (Integer.parseInt(loreReplace[0]) % 24 == 0) {
                loreReplace[0] = String.valueOf(Integer.parseInt(loreReplace[0]) / 24);
                lore.set(0, plugin.getConfig().getString("item.lore_days"));
            }

            String[] placeholders = new String[]{"%duration%", "%price%", "%servers%", "%worlds%", "%cooldown%", "%additional_lore%"};
            for (int i = 0; i < lore.size(); i++)
                lore.set(i, ChatColor.translateAlternateColorCodes('&', StringUtils.replaceEach(lore.get(i), placeholders, loreReplace)));


            String name = ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("item.name").
                    replaceAll("%command%", BuyConfig.get().getString("commandlist." + c + ".item_name")));

            setItemToGui(createGuiItem(m, name, lore), buyConfig.getInt("commandlist." + c + ".item_pos"));
            items.put(buyConfig.getInt("commandlist." + c + ".item_pos"), c);

        }
    }

    public HashMap getItems() {
        return items;
    }

    private void sendMessage(Player player, String path) {
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', PlaceholderReplacer.replaceAll(purchase, ChaosEssentials.getPlugin().getConfig().getString(path))));
    }

}
