package de.chaosolymp.chaosessentials.listener;

import de.chaosolymp.chaosessentials.ChaosEssentials;
import de.chaosolymp.chaosessentials.DatabaseController;
import de.chaosolymp.chaosessentials.tokens.TagEditor;
import de.chaosolymp.chaosessentials.tokens.Token;
import de.chaosolymp.chaosessentials.tokens.TokenCreator;
import de.chaosolymp.chaosessentials.util.MessageConverter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;

public class TokenListener implements Listener {

    private HashSet<Player> lockedPlayers = new HashSet<>();

    @EventHandler
    public void onRightClick(PlayerInteractEvent e) {
        if (e.getAction() == Action.RIGHT_CLICK_BLOCK || e.getAction() == Action.RIGHT_CLICK_AIR) {
            ItemStack item = e.getPlayer().getInventory().getItemInMainHand();
            if (new TagEditor(item).getTag() != null) {
                if (item.getType() != Material.AIR && !lockedPlayers.contains(e.getPlayer())) {
                    if (e.useItemInHand().equals(Event.Result.DENY)) {
                        return;
                    }
                    lockedPlayers.add(e.getPlayer());
                    new BukkitRunnable() {

                        @Override
                        public void run() {
                            handleToken(e, item);
                        }
                    }.runTaskAsynchronously(ChaosEssentials.getPlugin());
                }
                e.setCancelled(true);
            }
        }
    }

    private synchronized void handleToken(PlayerInteractEvent e, ItemStack item){
        TagEditor tagEditor = new TagEditor(e.getPlayer().getInventory().getItemInMainHand());
        if(tagEditor != null) {
            String tag = tagEditor.getTag();
            if (tag != null) {
                if (!e.getPlayer().hasPermission("ce.token.click")) {
                    MessageConverter.sendNoPermission(e.getPlayer());
                    lockedPlayers.remove(e.getPlayer());
                    return;
                }
                e.setCancelled(true);
                DatabaseController db = new DatabaseController();
                Token token = db.getToken(tag);
                if (TokenCreator.isValid(token)) {
                    String command = db.getToken(tag).getCommand();
                    if (command == null || command.equals("")) {
                        return;
                    }
                    String finalCommand = (command.charAt(0) == ' ' ? command.substring(1) : command).replaceAll("%player%", e.getPlayer().getName());
                    Bukkit.getScheduler().runTask(ChaosEssentials.getPlugin(), () -> Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), finalCommand));
                    e.getPlayer().getInventory().remove(item);
                    db.devalidateToken(token);
                    MessageConverter.sendConfMessage(e.getPlayer(), "token.redeemed");
                } else {
                    MessageConverter.sendConfMessage(e.getPlayer(), "token.is-invalid");
                }
            }
        }
        lockedPlayers.remove(e.getPlayer());
    }
}
