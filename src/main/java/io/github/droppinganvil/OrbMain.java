package io.github.droppinganvil;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class OrbMain extends JavaPlugin {
    public String name = "SaberCannon";
    private static OrbMain maininstance;
    public static OrbMain getInstance() {
        return maininstance;
    }
    public HashSet<String> autotargetable = new HashSet<String>();
    public HashMap<Player, StrikeOrder> sO = new HashMap<Player, StrikeOrder>();
    public HashSet<Player> waitingManual = new HashSet<Player>();
    public HashSet<Player> viewing = new HashSet<Player>();
    @Override
    public void onEnable() {
        Util.getInstance().ghostItem = Util.getInstance().getGhostItem();
        Hook.getInstance().bR.addAll(getConfig().getStringList("WorldGuard.BlacklistedRegions"));
    }
}
