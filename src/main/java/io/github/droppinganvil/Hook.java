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

class Hook {
    static Hook instance = null;
    private Hook() {  }
    private VanishPlugins vPlugin = null;
    private boolean fPlugin = false;
    public WorldGuardPlugin wgp = null;
    public List<String> bR = new ArrayList<String>();
    public boolean saberFactions = false;
    static public Hook getInstance()
    {
        if (instance == null)
            instance = new Hook();
        return instance;
    }
    void determinePlugins() {
        PluginManager pm = Bukkit.getServer().getPluginManager();
        if (pm.isPluginEnabled("SuperVanish")) {vPlugin = VanishPlugins.SuperVanish;}
        if (pm.isPluginEnabled("PremiumVanish")) {vPlugin = VanishPlugins.PremiumVanish;}
        if (vPlugin == null) {vPlugin = VanishPlugins.Essentials;}
        if (pm.isPluginEnabled("Factions") && OrbMain.getInstance().getConfig().getBoolean("FactionsHook.Enabled")) {fPlugin = true; FactionHook.checkIfCanDisableTitles();}
        if (saberFactions )
        logHooks();
    }
    String getFactionsString() {
        if (saberFactions) {return "Using Factions: True\nSaberFactions found! Using advanced titles!";}
        if (fPlugin) {return "Using Factions: True\n";} else {return "Using Factions: False\n";}
    }
    void logHooks() {
        System.out.print("-------------[" + OrbMain.getInstance().name + "]--------------\n");
        System.out.print("We have determined what plugins to use!\n");
        System.out.print("Vanish plugin: " + vPlugin.name() + "\n");
        System.out.print(getFactionsString());
        System.out.print("-------------[" + OrbMain.getInstance().name + "]--------------\n");
    }
    boolean isVanished(Player player) {
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
    boolean isPlayerVulnerable(Player player) {
        //God check
        if (OrbMain.getInstance().ess.getUser(player).isGodModeEnabled()) {return false;}
        //Vanish check
        if (isVanished(player)) {return false;}
        //Safezones check
        if (fPlugin) {
            if (FactionHook.isLocDenied(player)) {return false;}
        }
        //WorldGuard check
            Iterator<ProtectedRegion> i = wgp.getRegionManager(player.getWorld()).getApplicableRegions(player.getLocation()).getRegions().iterator();
            while (i.hasNext()) {
                if (bR.contains(i.next().getId())) {
                    return false;
                }
                i.remove();
            }
        //Vanilla MC Checks
        if (player.isDead()) {return false;}
        if (player.getGameMode() != GameMode.SURVIVAL) {return false;}
        if (!Util.getInstance().isOnSurface(player)) {return false;}
        return true;
    }
     boolean chargePlayer(Player p, Integer amount) {
        User u = OrbMain.getInstance().ess.getUser(p);
        if (!u.canAfford(BigDecimal.valueOf(amount))) {return false;}
        u.takeMoney(BigDecimal.valueOf(amount));
        return true;
    }
}
