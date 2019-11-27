package io.github.droppinganvil;

import com.earth2me.essentials.User;
import com.massivecraft.factions.Board;
import com.massivecraft.factions.FLocation;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.PluginManager;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Hook {
    private static Hook maininstance;
    public static Hook getInstance() {
        return maininstance;
    }
    public VanishPlugins vPlugin = null;
    public boolean fPlugin = false;
    public WorldGuardPlugin wgp = null;
    public List<String> bR = new ArrayList<String>();

    public void determinePlugins() {
        PluginManager pm = Bukkit.getServer().getPluginManager();
        if (pm.isPluginEnabled("SuperVanish")) {vPlugin = VanishPlugins.SuperVanish;}
        if (pm.isPluginEnabled("PremiumVanish")) {vPlugin = VanishPlugins.PremiumVanish;}
        if (vPlugin == null) {vPlugin = VanishPlugins.Essentials;}
        if (pm.isPluginEnabled("Factions")) {fPlugin = true;}
        logHooks();
    }
    public void logHooks() {
        System.out.print("-------------[" + OrbMain.getInstance().name + "]--------------\n");
        System.out.print("We have determined what plugins to use!\n");
        System.out.print("Vanish plugin: " + vPlugin.name() + "\n");
        System.out.print("Using Factions: " + fPlugin + "\n");
        System.out.print("-------------[" + OrbMain.getInstance().name + "]--------------\n");
    }
    public boolean isVanished(Player player) {
        if (vPlugin == VanishPlugins.PremiumVanish || vPlugin == VanishPlugins.SuperVanish) {
            for (MetadataValue meta : player.getMetadata("vanished")) {
                if (meta.asBoolean()) return true;
            }
            return false;
        }
        if (vPlugin == VanishPlugins.Essentials) {
            if (OrbMain.getInstance().ess.getUser(player).isVanished()) {return true;} else {return false;}
        }
        return false;
    }
    public boolean isPlayerVulnerable(Player player) {
        //God check
        if (OrbMain.getInstance().ess.getUser(player).isGodModeEnabled()) {return false;}
        //Vanish check
        if (isVanished(player)) {return false;}
        //Safezone check
        if (fPlugin) {
            if (Board.getInstance().getFactionAt(new FLocation(player.getLocation())).isSafeZone()) {return false;}
        }
        //WorldGuard check
        Iterator<ProtectedRegion> i = wgp.getRegionManager(player.getWorld()).getApplicableRegions(player.getLocation()).getRegions().iterator();
        while (i.hasNext()) {
            if (bR.contains(i.next().getId())) {return false;}
            i.remove();
        }
        //Vanilla MC Checks
        if (player.isDead()) {return false;}
        if (player.getGameMode() != GameMode.SURVIVAL) {return false;}
        return true;
    }
    public boolean chargePlayer(Player p, Integer amount) {
        User u = OrbMain.getInstance().ess.getUser(p);
        if (!u.canAfford(BigDecimal.valueOf(amount))) {return false;}
        u.takeMoney(BigDecimal.valueOf(amount));
        return true;
    }
}
