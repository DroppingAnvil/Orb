package io.github.droppinganvil;

import com.massivecraft.factions.*;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

class FactionHook {
    public static boolean isLocDenied(Player player) {
        Faction f = Board.getInstance().getFactionAt(new FLocation(player.getLocation()));
        if (!f.isSystemFaction()) {return false;}
        if (f.isSafeZone() && !OrbMain.getInstance().getConfig().getBoolean("FactionsHook.SafeZone")) {return true;}
        if (f.isWarZone() && !OrbMain.getInstance().getConfig().getBoolean("FactionsHook.WarZone")) {return true;}
        if (f.isWilderness() && !OrbMain.getInstance().getConfig().getBoolean("FactionsHook.Wild")) {return true;}
        return false;
    }
    public static void checkIfCanDisableTitles() {
        List<Method> methods = Arrays.asList(FPlayer.class.getDeclaredMethods());
        for (Method m : FPlayer.class.getDeclaredMethods()) {
            if (m.getName().equals("setTitlesEnabled")) {Hook.getInstance().saberFactions = true; return;}
        }
    }
    public static void tempDisableTitles(Player player) {
        FPlayer f = FPlayers.getInstance().getByPlayer(player);
        if (f.hasTitlesEnabled()) {
            f.setTitlesEnabled(false);
            Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(OrbMain.getInstance(), () -> f.setTitlesEnabled(true), 20L);
        }
    }
}
