package de.chaosolymp.chaosessentials.util;

import com.griefcraft.lwc.LWC;
import com.griefcraft.lwc.LWCPlugin;
import com.griefcraft.model.Protection;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Collection;

public class RegionCheck {

    public static boolean canBuild(Player player, Location loc) {
        LocalPlayer localPlayer = WorldGuardPlugin.inst().wrapPlayer(player);
        if (WorldGuard.getInstance().getPlatform().getSessionManager().hasBypass(localPlayer, localPlayer.getWorld()))
            return true;

        RegionQuery query = getQuery();
        if (query.testState(BukkitAdapter.adapt(loc), localPlayer, Flags.BUILD)) {
            LWCPlugin lwcPlugin = (LWCPlugin) Bukkit.getPluginManager().getPlugin("LWC");
            LWC lwc = lwcPlugin.getLWC();
            if (lwc.isProtectable(loc.getBlock())) {
                Protection protection = lwc.findProtection(loc.getBlock());
                if (protection == null) {
                    return true;
                } else {
                    return protection.isOwner(player);
                }
            }
        }
        return false;
    }

    public static ApplicableRegionSet getRegions(Location loc) {
        return getQuery().getApplicableRegions(BukkitAdapter.adapt(loc));
    }

    private static RegionQuery getQuery() {
        return WorldGuard.getInstance().getPlatform().getRegionContainer().createQuery();
    }

    public static boolean hasFlag(Location loc, String flag){
        ApplicableRegionSet regionSet = getRegions(loc);
        StateFlag flyFlag = (StateFlag) WorldGuard.getInstance().getFlagRegistry().get(flag);
        return regionSet.testState(null, flyFlag);
    }
}
