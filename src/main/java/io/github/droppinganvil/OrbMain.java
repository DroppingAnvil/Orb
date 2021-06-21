package io.github.droppinganvil;

import com.earth2me.essentials.Essentials;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class OrbMain extends JavaPlugin {
    public String name = "AnvilCannon";
    public static OrbMain instance;
    public OrbMain() {
        instance = this;
    }
    public static OrbMain getInstance() {
        return instance;
    }
    public HashMap<Player, StrikeOrder> sO = new HashMap<Player, StrikeOrder>();
    public HashSet<Player> waitingManual = new HashSet<Player>();
    public HashSet<Player> viewing = new HashSet<>();
    public HashSet<Player> s = new HashSet<>();
    public List<String> leftDuringView = new ArrayList<>();
    public File left;
    public FileConfiguration leavers;
    public static Essentials ess;
    public File configfile = new File(getDataFolder() + "/config.yml");
    @Override
    public void onEnable() {
        if (!configfile.exists()) {
            this.saveDefaultConfig();
            this.reloadConfig();
        }
        getCommand("Strike").setExecutor(new Command());
        Bukkit.getServer().getPluginManager().registerEvents(new PlayerListeners(), this);
        Hook.getInstance().determinePlugins();
        name = getConfig().getString("Name");
        ess = (Essentials) getServer().getPluginManager().getPlugin("Essentials");
        left = new File(getDataFolder() + "/data.yml");
        if (!left.exists()) {
            try {
                left.createNewFile();
                leavers = YamlConfiguration.loadConfiguration(left);
                List<String> dummyList = new ArrayList<>();
                leavers.set("LeftInView", dummyList);
                leavers.save(left);
            } catch (IOException e) {e.printStackTrace();}
        }
        leavers = YamlConfiguration.loadConfiguration(left);
        leftDuringView = leavers.getStringList("LeftInView");
        OrbUtil.getInstance().ghostItem = OrbUtil.getInstance().getGhostItem();
        Hook.getInstance().bR.addAll(getConfig().getStringList("WorldGuard.BlacklistedRegions"));
    }
    @Override
    public void onDisable() {
        leavers.set("LeftInView", leftDuringView);
        try {
            leavers.save(left);
        } catch (IOException e) {e.printStackTrace();}
    }
}
