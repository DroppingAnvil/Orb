package io.github.droppinganvil;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.PluginManager;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static org.bukkit.Bukkit.getServer;

class Hook {
    static Hook instance = null;
    private Hook() {
        econ = Bukkit.getServer().getServicesManager().getRegistration(Economy.class).getProvider();
    }
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
    public Economy econ;
    void determinePlugins() {
        PluginManager pm = getServer().getPluginManager();
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
            return OrbMain.ess.getUser(player).isVanished();
        }
        return false;
    }
    boolean isPlayerVulnerable(Player player) {
        //God check
        if (OrbMain.ess.getUser(player).isGodModeEnabled()) {return false;}
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
        return OrbUtil.getInstance().isOnSurface(player);
    }
     boolean chargePlayer(Player p, Integer amount) {
         return econ.withdrawPlayer(Bukkit.getOfflinePlayer(p.getUniqueId()), amount).transactionSuccess();
     }
}
