package de.chaosolymp.chaosessentials.listener;

import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import de.chaosolymp.chaosessentials.ChaosEssentials;
import de.chaosolymp.chaosessentials.config.QuestConfig;
import de.chaosolymp.chaosessentials.quests.QuestSettings;
import de.chaosolymp.chaosessentials.util.MessageConverter;
import de.chaosolymp.chaosessentials.util.RegionCheck;
import fr.skytasul.quests.api.stages.AbstractStage;
import fr.skytasul.quests.players.PlayerAccount;
import fr.skytasul.quests.players.PlayerQuestDatas;
import fr.skytasul.quests.players.PlayersManager;
import fr.skytasul.quests.structure.Quest;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class HarvestListener implements Listener {

    private final HashMap<String, QuestSettings> settings = new HashMap<>();
    private final HashSet<Replant> replants = new HashSet<>();

    public HarvestListener() {
        cacheSettings();
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        String block = e.getBlock().getType().toString();
        System.out.println(settings.entrySet().toArray().toString());
        if (settings.containsKey(block)) {
            Player player = e.getPlayer();
            QuestSettings setting = settings.get(e.getBlock().getType().toString());
            System.out.println(setting.getItem().getType().toString());
            ApplicableRegionSet regionSet = RegionCheck.getRegions(e.getBlock().getLocation());
            RegionFetch:
            for (ProtectedRegion rg : regionSet.getRegions()) {
                if (rg.getId().equals(setting.getRegion())) {
                    if (player.hasPermission("ce.harvest")) {
                        PlayerAccount account = PlayersManager.getPlayerAccount(player);
                        Collection<PlayerQuestDatas> quests = account.getQuestsDatas();
                        for (PlayerQuestDatas data : quests) {
                            Quest quest = data.getQuest();

                            if (quest.getBranchesManager().getPlayerBranch(account) != null) {
                                AbstractStage stage = quest.getBranchesManager().getPlayerBranch(account).getRegularStage(data.getStage());
                                if (stage.getID() == setting.getStage() && quest.getName().equals(setting.getQuest())) {

                                    e.setDropItems(false);
                                    if (setting.getSound() != null)
                                        player.playSound(player.getLocation(), setting.getSound(), 1f, 2f);

                                    switch (setting.getType()) {
                                        case 1:
                                            replant(e.getBlock(), e.getBlock().getType(), setting.getReplant_time());
                                            System.out.println(1);
                                            break;
                                        case 2:
                                            replant(e.getBlock(), e.getBlock().getType(), setting.getReplant_time());
                                            e.getBlock().getWorld().dropItemNaturally(e.getBlock().getLocation(), setting.getItem());
                                            System.out.println(2);
                                            break;
                                        case 3:
                                            e.getBlock().getWorld().dropItemNaturally(e.getBlock().getLocation(), setting.getItem());
                                            System.out.println(3);
                                    }
                                    break RegionFetch;
                                }
                            }
                        }
                        sendQuestRequired(player, setting.getQuest());
                        e.setCancelled(true);
                    } else {
                        e.setCancelled(true);
                    }
                    break;
                }
            }
        }
    }

    @EventHandler
    private void onCollect(EntityPickupItemEvent e) {
        if (e.getEntityType() == EntityType.PLAYER) {
            Material m = e.getItem().getItemStack().getType();
            Player player = (Player) e.getEntity();
            QuestSettings setting;
            if (settings.containsKey(m.toString())) {
                setting = settings.get(m.toString());
                if (player.hasPermission("ce.harvest")) {
                    ApplicableRegionSet regionSet = RegionCheck.getRegions(e.getItem().getLocation());
                    RegionFetch:
                    for (ProtectedRegion rg : regionSet.getRegions()) {
                        if (rg.getId().equals(setting.getRegion())) {
                            if (setting.getType() == 3 || setting.getType() == 4) {
                                PlayerAccount account = PlayersManager.getPlayerAccount(player);
                                if (account == null)
                                    ChaosEssentials.log("Accout is null");
                                Collection<PlayerQuestDatas> quests = account.getQuestsDatas();

                                for (PlayerQuestDatas data : quests) {
                                    Quest quest = data.getQuest();

                                    if (quest.getBranchesManager().getPlayerBranch(account) != null) {
                                        AbstractStage stage = quest.getBranchesManager().getPlayerBranch(account).getRegularStage(data.getStage());
                                        if (stage.getID() == setting.getStage() && quest.getName().equals(setting.getQuest())) {
                                            if (setting.getType() == 4) {
                                                e.getItem().remove();
                                                e.setCancelled(true);
                                                e.getItem().setPickupDelay(100);
                                            }
                                            player.playSound(player.getLocation(), setting.getSound(), 2f, 2f);
                                            break RegionFetch;
                                        }
                                    }
                                }
                                e.setCancelled(true);
                                e.getItem().setPickupDelay(100);
                                sendQuestRequired(player, setting.getQuest());
                            }
                        }
                    }
                } else {
                    MessageConverter.sendNoPermission(player);
                    e.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onFishCaught(PlayerFishEvent e) {
        if (e.getState() == PlayerFishEvent.State.CAUGHT_FISH) {

            Player player = e.getPlayer();
            ApplicableRegionSet regionSet = RegionCheck.getRegions(e.getCaught().getLocation());
            QuestSettings setting = settings.get("FISHING");
            RegionFetch:
            for (ProtectedRegion rg : regionSet.getRegions()) {
                if (rg.getId().equals(setting.getRegion())) {
                    if (setting.getType() == 5) {
                        PlayerAccount account = PlayersManager.getPlayerAccount(player);
                        Collection<PlayerQuestDatas> quests = account.getQuestsDatas();
                        for (PlayerQuestDatas data : quests) {
                            Quest quest = data.getQuest();
                            if (quest.getBranchesManager().getPlayerBranch(account) != null) {
                                AbstractStage stage = quest.getBranchesManager().getPlayerBranch(account).getRegularStage(data.getStage());
                                if (stage.getID() == setting.getStage() && quest.getName().equals(setting.getQuest())) {
                                    double rand = Math.random();
                                    if (rand < setting.getReplant_time() / 100.0) {
                                        Item item = (Item) e.getCaught();
                                        item.setItemStack(setting.getItem());
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private void sendQuestRequired(Player player, String quest) {
        MessageConverter.sendMessage(player, ChaosEssentials.getPlugin().getConfig()
                .getString("quest_required").replaceAll("%questname%", quest));
    }

    private void replant(Block block, Material m, long time) {
        Replant replant = new Replant(block, m);
        replant.runTaskLater(ChaosEssentials.getPlugin(),time);
        replants.add(replant);
    }

    public synchronized void cacheSettings() {
        ConfigurationSection section = QuestConfig.get().getConfigurationSection("item");
        if (section != null) {
            Set<String> keys = section.getKeys(false);
            FileConfiguration config = QuestConfig.get();
            for (String key : keys) {
                String path = "item." + key;
                QuestSettings setting = new QuestSettings();
                setting.setItemType(key);
                setting.setQuest(config.getString(path + ".quest"));
                setting.setStage(config.getInt(path + ".stage"));
                setting.setType(config.getInt(path + ".type"));
                setting.setReplant_time(config.getLong(path + ".replant_time"));
                setting.setRegion(config.getString(path + ".region"));
                setting.setSound(config.getString(path + ".sound"));
                setting.setItem(ItemStack.deserialize(config.getConfigurationSection(path + ".item_stack").getValues(true)));
                settings.put(key, setting);
            }
        }
    }

    public void replantAll(){
        Replant[] array = new Replant[0];
        for(Replant replant : replants.toArray(array)){
            replant.cancel();
            replant.run();
        }
    }

    private class Replant extends BukkitRunnable{
        private Block block;
        private Material m;
        public Replant(Block block, Material m){
            this.block = block;
            this.m = m;
        }

        @Override
        public void run() {
            block.setType(m, false);
            replants.remove(this);
        }
    }
}
