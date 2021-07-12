package de.chaosolymp.chaosessentials.command;

import com.nisovin.shopkeepers.api.ShopkeepersAPI;
import com.nisovin.shopkeepers.api.shopkeeper.Shopkeeper;
import de.chaosolymp.chaosessentials.ChaosEssentials;
import de.chaosolymp.chaosessentials.DatabaseController;
import de.chaosolymp.chaosessentials.config.LootConfig;
import de.chaosolymp.chaosessentials.config.ShopConfig;
import de.chaosolymp.chaosessentials.config.TokenPresetConfig;
import de.chaosolymp.chaosessentials.tokens.TagEditor;
import de.chaosolymp.chaosessentials.tokens.Token;
import de.chaosolymp.chaosessentials.tokens.TokenCreator;
import de.chaosolymp.chaosessentials.util.ItemGiver;
import de.chaosolymp.chaosessentials.util.MessageConverter;
import de.chaosolymp.chaosessentials.util.MojangApiRequest;
import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.StringUtil;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class TokenCommand implements CommandExecutor, TabCompleter {
    private final HashMap<String, SubCommand> subcommands = new HashMap<>();

    public TokenCommand() {
        //register subcommands
        subcommands.put("create", new CreateToken());
        subcommands.put("check", new CheckToken());
        subcommands.put("redeem", new RedeemToken());
        subcommands.put("clone", new CloneToken());
        subcommands.put("give", new GiveToken());
        subcommands.put("preset", new CreatePreset());
        subcommands.put("shop", new ShopToken());
        //Register Tabcomplete
        ChaosEssentials.getPlugin().getCommand("token").setTabCompleter(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        final Player player;

        if (sender instanceof Player) {
            player = (Player) sender;
            if (args.length > 0) {
                if (subcommands.containsKey(args[0]))
                    subcommands.get(args[0]).executeCommand(player, args);
                else
                    MessageConverter.sendConfMessage(sender, "wrong-subcmd");
            }
        } else {
            if (args.length > 0) {
                if (subcommands.containsKey(args[0]))
                    subcommands.get(args[0]).executeCommand(sender, args);
                else
                    MessageConverter.sendConfMessage(sender, "wrong-subcmd");
            }
        }

        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        switch (args.length) {
            case 1: {
                if (sender.hasPermission("ce.token.create"))
                    completions.add("create");
                if (sender.hasPermission("ce.token.check"))
                    completions.add("check");
                if (sender.hasPermission("ce.token.redeem"))
                    completions.add("redeem");
                if (sender.hasPermission("ce.token.clone"))
                    completions.add("clone");
                if (sender.hasPermission("ce.token.give"))
                    completions.add("give");
                if (sender.hasPermission("ce.token.preset"))
                    completions.add("preset");
                if (sender.hasPermission("ce.token.shop"))
                    completions.add("shop");
                break;
            }
            case 2: {
                if (args[0].equalsIgnoreCase("create") && sender.hasPermission("ce.token.create")) {
                    completions.add("<IsMultiUse>");
                    completions.add("true");
                    completions.add("false");
                }
                else if(args[0].equalsIgnoreCase("give") && sender.hasPermission("ce.token.create")) {
                    for(Player p : Bukkit.getOnlinePlayers()){
                        completions.add(p.getName());
                    }
                }
                else if(args[0].equalsIgnoreCase("shop") && sender.hasPermission("ce.token.shop")) {
                    completions.addAll(TokenPresetConfig.get().getKeys(false));
                }
                break;
            }
            case 3: {
                if (args[0].equalsIgnoreCase("create") && sender.hasPermission("ce.token.create")) {
                    completions.add("<ValidUntil>");
                    completions.add("JJJJ-MM-DD");
                    completions.add("2021-10-06");
                }
                else if(args[0].equalsIgnoreCase("give") && sender.hasPermission("ce.token.create")) {
                    completions.addAll(TokenPresetConfig.get().getKeys(false));
                }
                break;
            }
            case 4: {
                if (args[0].equalsIgnoreCase("create") && sender.hasPermission("ce.token.create")) {
                    completions.add("<command>");
                }
                break;
            }
        }

        List<String> filteredCompletions = new ArrayList<>();
        StringUtil.copyPartialMatches(args[args.length - 1], completions, filteredCompletions);
        Collections.sort(filteredCompletions);
        return filteredCompletions;
    }

    class CreateToken implements SubCommand {
        @Override
        public void executeCommand(Player player, String[] args) {
            if (player.hasPermission("ce.token.create")) {
                String cmd = "";
                for (int i = 3; i < args.length; i++) {
                    cmd += " " + args[i];
                }
                DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.GERMANY);
                TokenCreator.tokenize(player.getInventory().getItemInMainHand(), player, cmd, Boolean.getBoolean(args[1]), LocalDate.parse(args[2], df));
            }
        }

        @Override
        public void executeCommand(CommandSender sender, String[] args) {

        }
    }

    class CheckToken implements SubCommand {

        @Override
        public void executeCommand(Player player, String[] args) {
            if (player.hasPermission("ce.token.check")) {
                ItemStack item = player.getInventory().getItemInMainHand();
                if (item.getType() != Material.AIR) {
                    new BukkitRunnable(){

                        @Override
                        public void run() {
                            ConfigurationSection section = ChaosEssentials.getPlugin().getConfig().getConfigurationSection("token");
                            Token token = TokenCreator.getToken(item);
                            if(token == null){
                                MessageConverter.sendConfMessage(player,"token.not-token");
                                return;
                            }

                            if(TokenCreator.isValid(token)){
                                MessageConverter.sendConfMessage(player,"token.is-valid");
                            } else {
                                MessageConverter.sendConfMessage(player, "token.is-invalid");

                                if (player.hasPermission("ce.token.check.detail")) {
                                    MessageConverter.sendMessage(player, section.getString("used-date").replaceAll("%date%", token.getRedeem().toString()));
                                    String name;
                                    try {
                                        name = MojangApiRequest.requestName(token.getRedeemUuid());
                                    } catch (IOException | ParseException e) {
                                        name = token.getRedeemUuid();
                                        e.printStackTrace();
                                    }
                                    MessageConverter.sendMessage(player, section.getString("used-player").replaceAll("%player%", name));
                                }
                            }

                            if(player.hasPermission("ce.token.check.detail")) {
                                MessageConverter.sendMessage(player, section.getString("uuid").replaceAll("%uuid%", token.getUuid()));
                                String cmd = token.getCommand();
                                if(cmd == null || cmd.length() == 0){
                                    cmd = "no command";
                                }
                                MessageConverter.sendMessage(player, section.getString("command").replaceAll("%cmd%", cmd));
                                MessageConverter.sendMessage(player, section.getString("multi-use").replaceAll("%use%", String.valueOf(token.isMultiUse())));
                            }
                            MessageConverter.sendMessage(player,section.getString("valid-until").replaceAll("%date%",token.getValidUntil().toString()));
                        }
                    }.runTaskAsynchronously(ChaosEssentials.getPlugin());
                }
            }
        }

        @Override
        public void executeCommand(CommandSender sender, String[] args) {

        }
    }

    class RedeemToken implements SubCommand {

        @Override
        public void executeCommand(Player player, String[] args) {
            if (player.hasPermission("ce.token.redeem")) {
                ItemStack item = player.getInventory().getItemInMainHand();
                if (item.getType() != Material.AIR) {
                    new BukkitRunnable() {

                        @Override
                        public void run() {
                            if(new TagEditor(item).getTag() != null) {
                                Token token = TokenCreator.getToken(item);
                                if (TokenCreator.isValid(token)) {
                                    DatabaseController db = new DatabaseController();
                                    db.devalidateToken(token);
                                    player.getInventory().remove(item);
                                }
                                else {
                                    MessageConverter.sendConfMessage(player,"token.is-invalid");
                                }
                            }
                        }
                    }.runTaskAsynchronously(ChaosEssentials.getPlugin());
                }
            }
        }

        @Override
        public void executeCommand(CommandSender sender, String[] args) {

        }
    }

    class CloneToken implements SubCommand {

        @Override
        public void executeCommand(Player player, String[] args) {
            if (player.hasPermission("ce.token.clone")) {
                ItemStack item = player.getInventory().getItemInMainHand();
                if (item.getType() != Material.AIR) {
                    new BukkitRunnable(){

                        @Override
                        public void run() {
                            if(new TagEditor(item).getTag() != null){
                                Token token = TokenCreator.getToken(item);
                                if(token != null) {
                                    ItemStack newTokenItem = item.clone();
                                    TokenCreator.tokenize(newTokenItem, player, token.getCommand(), token.isMultiUse(), token.getValidUntil());
                                    ItemGiver.giveItemSave(player, newTokenItem);
                                }
                            }
                        }
                    }.runTaskAsynchronously(ChaosEssentials.getPlugin());
                }
            } else {
                MessageConverter.sendNoPermission(player);
            }
        }

        @Override
        public void executeCommand(CommandSender sender, String[] args) {

        }
    }

    class CreatePreset implements SubCommand {

        @Override
        public void executeCommand(Player player, String[] args) {
            if (player.hasPermission("ce.token.preset")) {
                ItemStack item = player.getInventory().getItemInMainHand();
                if (item.getType() != Material.AIR) {
                    new BukkitRunnable(){

                        @Override
                        public void run() {
                            if(new TagEditor(item).getTag() != null){
                                Token token = TokenCreator.getToken(item);
                                if(token != null) {
                                    if(TokenPresetConfig.get().get(args[1]) == null){
                                        TokenPresetConfig.get().set(args[1]+".item",item.serialize());
                                        TokenPresetConfig.get().set(args[1]+".valid-days", Integer.parseInt(args[2]));
                                        TokenPresetConfig.get().set(args[1]+".soulbound", SoulboundCommand.isSoulBound(item));
                                        TokenPresetConfig.get().set(args[1]+".command", token.getCommand());
                                        TokenPresetConfig.get().set(args[1]+".is-multiuse", token.isMultiUse());
                                        TokenPresetConfig.save();
                                        TokenPresetConfig.reload();

                                        MessageConverter.sendMessage(player,"&aPreset "+ args[1] +" created!");
                                    }
                                }
                            }

                        }
                    }.runTaskAsynchronously(ChaosEssentials.getPlugin());
                }
                else{
                    MessageConverter.sendNoPermission(player);
                }
            }
        }

        @Override
        public void executeCommand(CommandSender sender, String[] args) {

        }
    }

    class GiveToken implements SubCommand {

        @Override
        public void executeCommand(Player player, String[] args) {
            executeCommand((CommandSender) player, args);
        }

        @Override
        public void executeCommand(CommandSender sender, String[] args) {
            if(sender.hasPermission("ce.token.give")){
                if(args.length < 3 || !NumberUtils.isNumber(args[2])){
                    MessageConverter.sendConfMessage(sender,"token.preset-usage");
                }
                new BukkitRunnable() {

                    @Override
                    public void run() {
                        Player target = Bukkit.getPlayer(args[1]);
                        if(target !=null) {
                            ItemStack item = ItemStack.deserialize(TokenPresetConfig.get().getConfigurationSection(args[2] + ".item").getValues(true));
                            if (item != null) {
                                if(args.length < 4) {
                                    LocalDate expiry = LocalDate.ofEpochDay(LocalDate.now().toEpochDay() + TokenPresetConfig.get().getInt(args[2] + ".valid-days"));
                                    TokenCreator.tokenize(item, target,
                                            TokenPresetConfig.get().getString(args[2] + ".command"),
                                            TokenPresetConfig.get().getBoolean(args[2] + ".is-multiuse"),expiry);

                                            List<String> lore = item.getItemMeta().getLore();
                                            for(int i = 0; i < lore.size(); i++) {
                                                lore.set(i,lore.get(i).replaceFirst("%date%", expiry.getDayOfMonth()+"."+expiry.getMonthValue()+"."+expiry.getYear()));
                                            }
                                            ItemMeta meta = item.getItemMeta();
                                            meta.setLore(lore);
                                            item.setItemMeta(meta);
                                }

                                System.out.println("sb: "+TokenPresetConfig.get().getBoolean(args[2] + ".soulbound"));

                                if(TokenPresetConfig.get().getBoolean(args[2] + ".soulbound")) {
                                    SoulboundCommand.bindToSoul(item,target.getUniqueId().toString());
                                    System.out.println("overwriting"+target.getUniqueId().toString());
                                }

                                ItemGiver.giveItemSave(target, item);
                            } else {
                                MessageConverter.sendConfMessage(sender, "token.no-preset");
                            }
                        } else {
                            MessageConverter.sendConfMessage(sender, "playernotfound");
                        }
                    }

                }.runTaskAsynchronously(ChaosEssentials.getPlugin());
            } else {
                MessageConverter.sendNoPermission(sender);
            }
        }
    }

    class ShopToken implements SubCommand {

        @Override
        public void executeCommand(Player player, String[] args) {
            List<Entity> entities = player.getNearbyEntities(5, 5, 5);
            for (Entity e : entities) {
                if (ShopkeepersAPI.getShopkeeperRegistry().isShopkeeper(e)) {
                    Shopkeeper shopkeeper = ShopkeepersAPI.getShopkeeperRegistry().getShopkeeperByEntity(e);
                    int next = ShopConfig.get().getInt(shopkeeper.getIdString() + ".next");
                    ShopConfig.get().set(shopkeeper.getIdString() +"."+next+".token", args[1]);
                    ShopConfig.get().set(shopkeeper.getIdString() +"."+next+".item", player.getInventory().getItemInMainHand().serialize());
                    ShopConfig.save();
                    ShopConfig.reload();
                    return;
                }
            }
            MessageConverter.sendMessage(player, "&cKein Shopkeeper gefunden!");
        }

        @Override
        public void executeCommand(CommandSender sender, String[] args) {

        }
    }
}
