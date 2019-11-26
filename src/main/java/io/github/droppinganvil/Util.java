package io.github.droppinganvil;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import java.util.List;

public class Util {
    public ItemStack ghostItem = null;
    public String getString(String path) {
        String string = OrbMain.getConfig().getString(path);
        string = ChatColor.translateAlternateColorCodes('&', string);
        if (string.contains("%plugin%")) {string = string.replace("%plugin%", OrbMain.name);}
        return string;
    }
    public List<String> getStringList(String path) {
        List<String> stringList = OrbMain.getConfig().getStringList(path);
        int index = 0;
        for (String string : stringList) {
            String editString = string;
            editString = ChatColor.translateAlternateColorCodes('&', string);
            if (editString.contains("%plugin%")) {editString = editString.replace("%plugin%", OrbMain.name);}
            stringList.set(index, editString);
            index++;
        }
        return stringList;

    }
    public ItemStack getGhostItem() {
        ItemStack x = new ItemStack(Material.matchMaterial(OrbMain.getConfig().getString("GhostItem.Material")), 1);
        ItemMeta meta = x.getItemMeta();
        meta.setLore(getStringList("GhostItem.Lore"));
        meta.setDisplayName(getString("GhostItem.Name"));
        if (OrbMain.getConfig().getBoolean("GhostItem.Glow")) {
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
        if (player.hasPermission(OrbMain.name.toLowerCase() + ".Ghost")) {return true;}
        //TODO add item check
        return false;
    }
}
