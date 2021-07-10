package de.chaosolymp.chaosessentials.command;

import de.chaosolymp.chaosessentials.ChaosEssentials;
import de.chaosolymp.chaosessentials.util.MessageConverter;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class SpecializeItemCommand implements CommandExecutor {

    private final NamespacedKey key = new NamespacedKey(ChaosEssentials.getPlugin(),"special");
    private final HashMap<String, SubCommand> commands = new HashMap<>();

    public SpecializeItemCommand(){
        commands.put("check", new CheckCommand());
        commands.put("add", new AddCommand());
        commands.put("remove", new RemoveCommand());
    }


    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(sender instanceof Player){
            Player player = (Player) sender;
            if(args.length > 0){
                if(commands.containsKey(args[0])){
                    commands.get(args[0]).executeCommand(player,args);

                }
                else
                    MessageConverter.sendConfMessage(sender,"wrong-subcmd");

                return false;
            }

            if(player.hasPermission("ce.specialize")) {
                ItemStack item = player.getInventory().getItemInMainHand();
                if(item.getType() != Material.AIR){
                    ItemMeta meta = item.getItemMeta();
                    meta.getPersistentDataContainer().set(key, PersistentDataType.BYTE, (byte) 1);
                    item.setItemMeta(meta);
                    MessageConverter.sendMessage(player,"&aItem wurde SPEZIALISIERT!");
                }
            }
            else {
                MessageConverter.sendNoPermission(player);
            }
        }
        return false;
    }

    class CheckCommand implements SubCommand {

        @Override
        public void executeCommand(Player player, String[] args) {
            PersistentDataContainer container = player.getInventory().getItemInMainHand().getItemMeta().getPersistentDataContainer();
            for(NamespacedKey key : container.getKeys()){
                MessageConverter.sendMessage(player,"&7Key: &e"+key.getNamespace() +" &7Value: &e"+container.get(key,PersistentDataType.STRING));
            }
        }

        @Override
        public void executeCommand(CommandSender sender, String[] args) {

        }
    }

    class AddCommand implements SubCommand {

        @Override
        public void executeCommand(Player player, String[] args) {
            ItemMeta meta = player.getInventory().getItemInMainHand().getItemMeta();
            NamespacedKey key = new NamespacedKey(ChaosEssentials.getPlugin(),args[1]);
            meta.getPersistentDataContainer().set(key,PersistentDataType.STRING,args[2]);
            player.getInventory().getItemInMainHand().setItemMeta(meta);
        }

        @Override
        public void executeCommand(CommandSender sender, String[] args) {

        }
    }

    class RemoveCommand implements SubCommand {

        @Override
        public void executeCommand(Player player, String[] args) {
            ItemMeta meta = player.getInventory().getItemInMainHand().getItemMeta();
            NamespacedKey key = new NamespacedKey(ChaosEssentials.getPlugin(),args[1]);
            meta.getPersistentDataContainer().remove(key);
            player.getInventory().getItemInMainHand().setItemMeta(meta);
        }

        @Override
        public void executeCommand(CommandSender sender, String[] args) {

        }
    }
}
