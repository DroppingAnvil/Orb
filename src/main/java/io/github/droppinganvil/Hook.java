package io.github.droppinganvil;

import com.earth2me.essentials.Essentials;
import com.massivecraft.factions.Board;
import com.massivecraft.factions.FLocation;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.PluginManager;

public class Hook {
    public VanishPlugins vPlugin = null;
    public boolean fPlugin = false;
    public WorldGuardPlugin wgp = null;

    public void determinePlugins() {
        PluginManager pm = Bukkit.getServer().getPluginManager();
        if (pm.isPluginEnabled("SuperVanish")) {vPlugin = VanishPlugins.SuperVanish;}
        if (pm.isPluginEnabled("PremiumVanish")) {vPlugin = VanishPlugins.PremiumVanish;}
        if (vPlugin == null) {vPlugin = VanishPlugins.Essentials;}
        if (pm.isPluginEnabled("Factions")) {fPlugin = true;}
        logHooks();
    }
    public void logHooks() {
        System.out.print("-------------[" + OrbMain.name + "]--------------\n");
        System.out.print("We have determined what plugins to use!\n");
        System.out.print("Vanish plugin: " + vPlugin.name() + "\n");
        System.out.print("Using Factions: " + fPlugin + "\n");
        System.out.print("-------------[" + OrbMain.name + "]--------------\n");
    }
    public boolean isVanished(Player player) {
        if (vPlugin == VanishPlugins.PremiumVanish || vPlugin == VanishPlugins.SuperVanish) {
            for (MetadataValue meta : player.getMetadata("vanished")) {
                if (meta.asBoolean()) return true;
            }
            return false;
        }
        if (vPlugin == VanishPlugins.Essentials) {
           if (Essentials.getUser(player).isVanished()) {return true;} else {return false;}
        }
        return false;
    }
    public boolean isPlayerVulnerable(Player player) {
        //God check
        if (Essentials.getUser(player).isGodModeEnabled()) {return false;}
        //Vanish check
        if (isVanished(player)) {return false;}
        //Safezone check
        if (fPlugin) {
            if (Board.getInstance().getFactionAt(new FLocation(player.getLocation())).isSafeZone()) {return false;}
        }
        //Vanilla MC Checks
        if (player.isDead()) {return false;}
        if (player.getGameMode() != GameMode.SURVIVAL) {return false;}
        return true;
    }
}
