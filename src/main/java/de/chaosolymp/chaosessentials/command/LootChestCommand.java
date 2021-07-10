package de.chaosolymp.chaosessentials.command;

import de.chaosolymp.chaosessentials.ChaosEssentials;
import de.chaosolymp.chaosessentials.config.LootConfig;
import de.chaosolymp.chaosessentials.gui.AbstractGui;
import de.chaosolymp.chaosessentials.listener.GuiManager;
import de.chaosolymp.chaosessentials.tokens.TokenCreator;
import de.chaosolymp.chaosessentials.util.MessageConverter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.time.LocalDate;
import java.util.*;

public class LootChestCommand implements CommandExecutor, Listener {

    private HashMap<String, SubCommand>commands = new HashMap<>();
    private HashMap<String, short[]> boxes = new HashMap<>();
    
    public LootChestCommand(){
        commands.put("create", new AddChest());
        commands.put("play", new OpenChest());

        FileConfiguration config = LootConfig.get();
        Set<String> keys = config.getKeys(false);

        for(String s : keys){
            int totalOddValue = 0;
            Set<String> subKeys = config.getConfigurationSection(s).getKeys(false);
            for(String sKey : subKeys){
                if(!sKey.equals("next")) {
                    totalOddValue += config.getInt(s+"."+sKey+".odds");
                }
            }
            short[] indexes = new short[totalOddValue];
            int i = 0;
            for(String sKey : subKeys){
                int end = config.getInt(s+"."+sKey+".odds");
                for(int j = 0; j < end; j++){
                    indexes[i++] = Short.parseShort(sKey);
                }
            }
            boxes.put(s, indexes);
        }
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(sender.hasPermission("ce.lootchest.open")){
            if(args.length > 0) {
                if(commands.containsKey(args[0].toLowerCase()))
                    commands.get(args[0].toLowerCase()).executeCommand(sender,args);
            }
        }
        return false;
    }

    class OpenChest implements SubCommand {

        @Override
        public void executeCommand(Player player, String[] args) {

        }

        @Override
        public void executeCommand(CommandSender sender, String[] args) {
            Player target = Bukkit.getPlayer(args[1]);
            if (target != null) {
                LootGui gui = new LootGui(5, ChatColor.translateAlternateColorCodes('&',ChaosEssentials.getPlugin().getConfig().getString("lootchest")));
                gui.roll(target, args[2]);
                gui.openInventory(target);
            }
        }
    }

    class AddChest implements SubCommand {

        @Override
        public void executeCommand(Player player, String[] args) {

        }

        @Override
        public void executeCommand(CommandSender sender, String[] args) {
            if(sender instanceof Player) {
                Player player = (Player) sender;
                if (sender.hasPermission("ce.lootchest.add")) {
                    if (args.length > 3) {
                        FileConfiguration config = LootConfig.get();
                        ItemStack item = player.getInventory().getItemInMainHand();
                        int next = config.getInt(args[1]+".next");
                        config.set(args[1]+".next",next+1);

                        String path = args[1]+"."+next+".";
                        config.set(path+"rarity", args[2]);
                        config.set(path+"isToken", Boolean.valueOf(args[3]));
                        config.set(path+"soulbound", Boolean.valueOf(args[4]));
                        config.set(path+"odds", Integer.valueOf(args[5]));
                        config.set(path+"item", item.serialize());

                        if(Boolean.valueOf(args[3])){
                            String cmd = "";
                            for(int i = 7; i < args.length; i++){
                                cmd += " "+args[i];
                            }
                            config.set(path+"token.expire", Integer.parseInt(args[6]));
                            config.set(path+"token.command", cmd.substring(1));
                        }

                        LootConfig.save();
                        LootConfig.reload();
                        MessageConverter.sendMessage(player,"&aItem added to config");
                    }
                }
            }
        }
    }

    class LootGui extends AbstractGui {

        int size;
        private Random rand = new Random();
        int selectedKey;

        protected LootGui(int size, String name) {
            super(size, name);
            this.size = size;
        }

        private ItemStack[] getRandomItems(String chest, boolean setKey){
            short random = boxes.get(chest)[rand.nextInt(boxes.get(chest).length)];
            if(setKey)
                selectedKey = random;
            ItemStack item = ItemStack.deserialize(LootConfig.get().getConfigurationSection(chest+"."+random+".item").getValues(true));
            ItemStack rarity = new ItemStack(Material.getMaterial(LootConfig.get().getString(chest+"."+random+".rarity")));
            return new ItemStack[]{item,rarity};
        }

        protected void roll(Player player, String chest){
            GuiManager.getInstance().registerGui(getInv());
            GuiManager.getInstance().registerOpenGui(player.getUniqueId().toString(),getInv());

            for(int i = 0; i < this.getInv().getSize(); i++) {
                if (i < 9 || i > 35)
                    setItemToGui(new ItemStack(Material.BLACK_STAINED_GLASS_PANE), i);
                else if (i == 8) {
                    i = 36;
                }
                setItemToGui(new ItemStack(Material.WHITE_STAINED_GLASS_PANE),4);
                setItemToGui(new ItemStack(Material.WHITE_STAINED_GLASS_PANE),40);
            }

            new BukkitRunnable(){

                @Override
                public void run() {
                    int rolls = 0;
                    int delay = 50;
                    ItemStack[] item = new ItemStack[0];
                    for(int i = 0; i < 9; i++){
                        item = getRandomItems(chest,false);
                        setItemToGui(item[1],i+9);
                        setItemToGui(item[0],i+18);
                        setItemToGui(item[1].clone(),i+27);
                    }
                    while (delay < 1400){
                        try {
                            Thread.sleep(delay);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        if(rolls == 34) {
                            item = getRandomItems(chest, true);
                            String path = chest+"."+selectedKey;
                            if(LootConfig.get().getBoolean(path+".isToken")){
                                int valid = LootConfig.get().getInt(path+".token.expire");
                                LocalDate localDate;
                                if(valid == -1)
                                    localDate = LocalDate.MAX;
                                else
                                    localDate = LocalDate.ofEpochDay(LocalDate.now().toEpochDay()+valid);
                                    TokenCreator.tokenize(item[0],player,LootConfig.get().getString(path+".token.command"),false, localDate);
                            }

                            if(LootConfig.get().getBoolean(path+".soulbound")){
                                SoulboundCommand.bindToSoul(item[0],player.getUniqueId().toString());
                            }
                        }
                        else {
                            item = getRandomItems(chest, false);
                        }

                        for(int i = 0; i < 8; i++){
                            setItemToGui(getInv().getItem(i+1+9),i+9);
                            setItemToGui(getInv().getItem(i+1+18),i+18);
                            setItemToGui(getInv().getItem(i+1+27),i+27);
                        }
                        setItemToGui(item[1],17);
                        setItemToGui(item[0],26);
                        setItemToGui(item[1].clone(),35);
                        Bukkit.getScheduler().runTask(ChaosEssentials.getPlugin(),
                                () -> player.playSound(player.getLocation(),Sound.ENTITY_CHICKEN_EGG,2f,0.5f));

                        delay = (int) (delay < 350 ? delay * 1.07 : delay * 1.2);
                        rolls++;

                    }
                    GuiManager.getInstance().unregisterOpenGui(player.getUniqueId().toString());
                    HashSet<Integer> enabledSlots = new HashSet<>();
                    enabledSlots.add(22);
                    GuiManager.getInstance().registerGui(getInv(),enabledSlots);
                    GuiManager.getInstance().registerDropGui(getInv(),enabledSlots);
                    try {
                        Thread.sleep(300);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if(getInv().getItem(13).getType() == Material.YELLOW_STAINED_GLASS_PANE) {
                        Bukkit.getScheduler().runTask(ChaosEssentials.getPlugin(),
                                () -> player.playSound(player.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 2f, 0.5f));
                        Bukkit.getServer().broadcastMessage(ChatColor.translateAlternateColorCodes('&',
                                ChaosEssentials.getPlugin().getConfig().getString("titan-item").replaceFirst("%player%",player.getName())));
                    }
                    else
                        Bukkit.getScheduler().runTask(ChaosEssentials.getPlugin(),
                                () -> player.playSound(player.getLocation(),Sound.ENTITY_PLAYER_LEVELUP,2f,0.5f));

                }
            }.runTaskAsynchronously(ChaosEssentials.getPlugin());
        }
    }
}
