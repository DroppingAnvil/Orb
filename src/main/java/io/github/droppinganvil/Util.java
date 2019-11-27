package io.github.droppinganvil;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.HashSet;
import java.util.List;

import static org.bukkit.Bukkit.getServer;

public class Util {
    public HashSet<String> ghostPlayers = new HashSet<String>();
    private static Util maininstance;
    public static Util getInstance() {
        return maininstance;
    }
    public ItemStack ghostItem = null;
    public String getString(String path) {
        String string = OrbMain.getInstance().getConfig().getString(path);
        string = ChatColor.translateAlternateColorCodes('&', string);
        if (string.contains("%plugin%")) {string = string.replace("%plugin%", OrbMain.getInstance().name);}
        return string;
    }
    public List<String> getStringList(String path) {
        List<String> stringList = OrbMain.getInstance().getConfig().getStringList(path);
        int index = 0;
        for (String string : stringList) {
            String editString = string;
            editString = ChatColor.translateAlternateColorCodes('&', string);
            if (editString.contains("%plugin%")) {editString = editString.replace("%plugin%", OrbMain.getInstance().name);}
            stringList.set(index, editString);
            index++;
        }
        return stringList;
    }
    public ItemStack getGhostItem() {
        ItemStack x = new ItemStack(Material.matchMaterial(OrbMain.getInstance().getConfig().getString("GhostItem.Material")), 1);
        ItemMeta meta = x.getItemMeta();
        meta.setLore(getStringList("GhostItem.Lore"));
        meta.setDisplayName(getString("GhostItem.Name"));
        if (OrbMain.getInstance().getConfig().getBoolean("GhostItem.Glow")) {
            meta.addEnchant(Enchantment.DURABILITY, 1, true);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }
        x.setItemMeta(meta);
        return x;
    }
    public boolean isOnSurface(Player player) {
        if (player.getLocation() == null) {return false;}
        if (player.getLocation().getBlockY() - 1 >= player.getWorld().getHighestBlockYAt(player.getLocation())) {return true;}
        return false;
    }
    public boolean isGhost(Player player) {
        if (player.hasPermission(OrbMain.getInstance().name.toLowerCase() + ".Ghost")) {return true;}
        if (ghostPlayers.contains(player.getName())) {return true;}
        return false;
    }
    public void serveMenu(Player player) {
    }
    public void sendPlayerManualInstructions(Player player) {
        for (String s : getStringList("Messages.InstructionsForManual")) {player.sendMessage(s);}
    }
    public void sendInsufficientFunds(Player p) {p.sendMessage(getString("Messages.NotEnoughMoney"));}
    public void scheduleViewEffectsRemoval(Player p) {
        BukkitScheduler scheduler = getServer().getScheduler();
        scheduler.scheduleSyncDelayedTask(OrbMain.getInstance(), () -> setNormal(p), OrbMain.getInstance().getConfig().getInt("PlayerView.Time") * 20);
    }
    public void setNormal(Player player) {
        player.setGameMode(GameMode.SURVIVAL);
        if (player.hasPotionEffect(PotionEffectType.NIGHT_VISION)) {player.removePotionEffect(PotionEffectType.NIGHT_VISION);}
        OrbMain.getInstance().viewing.remove(player);
    }
    public void makeView(Player player, Location loc) {
        OrbMain.getInstance().viewing.add(player);
        player.setGameMode(GameMode.SPECTATOR);
        if (OrbMain.getInstance().getConfig().getBoolean("PlayerView.NightVision")) {player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, OrbMain.getInstance().getConfig().getInt("PlayerView.Time") * 20, 1, false));}
        player.teleport(loc);
        scheduleViewEffectsRemoval(player);
    }
    public void sendTargetNotFound(Player player) {
        for (String s : getStringList("Messages.TargetNotFound")) {
            player.sendMessage(s);
        }
    }
}
