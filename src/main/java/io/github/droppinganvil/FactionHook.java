package io.github.droppinganvil;

import com.massivecraft.factions.Board;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.Faction;
import org.bukkit.entity.Player;

class FactionHook {
    public static boolean isLocDenied(Player player) {
        Faction f = Board.getInstance().getFactionAt(new FLocation(player.getLocation()));
        if (f.isSafeZone() && !OrbMain.getInstance().getConfig().getBoolean("FactionsHook.SafeZone")) {return true;}
        if (f.isWarZone() && !OrbMain.getInstance().getConfig().getBoolean("FactionsHook.WarZone")) {return true;}
        if (f.isWilderness() && !OrbMain.getInstance().getConfig().getBoolean("FactionsHook.Wild")) {return true;}
        return false;
    }
}
