package de.chaosolymp.chaosessentials.util;

import com.griefcraft.lwc.LWC;
import com.griefcraft.lwc.LWCPlugin;
import com.griefcraft.model.Protection;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class RegionCheck {

    public static boolean canBuild(Player player, Location loc) {
        LocalPlayer localPlayer = WorldGuardPlugin.inst().wrapPlayer(player);
        if (WorldGuard.getInstance().getPlatform().getSessionManager().hasBypass(localPlayer, localPlayer.getWorld()))
            return true;

        com.sk89q.worldedit.util.Location wgLoc = new com.sk89q.worldedit.util.Location(localPlayer.getWorld(), loc.getX(), loc.getY(), loc.getZ());
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionQuery query = container.createQuery();

        if (query.testState(wgLoc, localPlayer, Flags.BUILD)) {
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
}
