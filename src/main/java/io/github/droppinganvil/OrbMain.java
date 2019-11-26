package io.github.droppinganvil;

import org.bukkit.plugin.java.JavaPlugin;

public class OrbMain  extends JavaPlugin {
    public String name = "SaberCannon";
    @Override
    public void onEnable() {
        Util.ghostItem = Util.getGhostItem();
        Hook.bR.addAll(getConfig().getStringList("WorldGuard.BlacklistedRegions"));
    }
}
