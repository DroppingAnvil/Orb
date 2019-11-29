package io.github.droppinganvil;

import org.bukkit.*;
import org.bukkit.command.CommandSender;
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

class Util {
    public HashSet<String> ghostPlayers = new HashSet<>();
    static Util instance = null;
    private Util() {}
    static public Util getInstance()
    {
        if (instance == null)
            instance = new Util();
        return instance;
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
        //TODO add back when testing complete
        //if (player.hasPermission(OrbMain.getInstance().name.toLowerCase() + ".Ghost")) {return true;}
        //if (ghostPlayers.contains(player.getName())) {return true;}
        return false;
    }
    public void sendPlayerManualInstructions(Player player) {
        for (String s : getStringList("Messages.InstructionsForManual")) {player.sendMessage(s);}
    }
    public void sendInsufficientFunds(Player p) {p.sendMessage(getString("Messages.NotEnoughMoney"));}
    public void scheduleViewEffectsRemoval(Player p, StrikeOrder so) {
        BukkitScheduler scheduler = getServer().getScheduler();
        scheduler.scheduleSyncDelayedTask(OrbMain.getInstance(), () -> setNormal(p, so), OrbMain.getInstance().getConfig().getInt("PlayerView.Time") * 20);
    }
    public void setNormal(Player player, StrikeOrder so) {
        player.setGameMode(GameMode.SURVIVAL);
        if (player.hasPotionEffect(PotionEffectType.NIGHT_VISION)) {player.removePotionEffect(PotionEffectType.NIGHT_VISION);}
        player.teleport(so.getOldLoc());
        OrbMain.getInstance().viewing.remove(player);
    }
    public void makeView(Player player, Location loc, StrikeOrder so) {
        OrbMain.getInstance().viewing.add(player);
        so.setOldLoc(player.getLocation());
        player.setGameMode(GameMode.SPECTATOR);
        if (OrbMain.getInstance().getConfig().getBoolean("PlayerView.NightVision")) {player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, OrbMain.getInstance().getConfig().getInt("PlayerView.Time") * 20, 1, false));}
        player.teleport(loc);
        scheduleViewEffectsRemoval(player, so);
    }
    public void sendTargetNotFound(Player player) {
        for (String s : getStringList("Messages.TargetNotFound")) {
            player.sendMessage(s);
        }
    }
    public void sendManualInstructionHelp(Player player) {
        for (String s : getStringList("Messages.InstructionsNotFollowedHelp")) {
            player.sendMessage(s);
        }
    }
    public void sendManualCoordsSet(Player player, StrikeOrder so) {
        for (String s : getStringList("Messages.ManualCoordsSet")) {
            String msg = s;
            if (msg.contains("%x%")) {msg = msg.replace("%x%", String.valueOf(so.getX()));}
            if (msg.contains("%z%")) {msg = msg.replace("%z%", String.valueOf(so.getZ()));}
            player.sendMessage(msg);
        }
    }
    public void sendPlayerOnly(CommandSender sender) {sender.sendMessage(getString("Messages.MustBePlayer"));}
    public void sendList(List<String> l, Player player) {
        for (String s : l) {
            player.sendMessage(s);
        }
    }
    public void sendNoMultipleStrikes(Player player) {
        sendList(getStringList("Messages.CannotRunMultipleStrikes"), player);
    }
    public void sendClearedMSG(Player player) {player.sendMessage(getString("Messages.ClearedStrikes"));}
    public void sendNothingToConfirmMSG(Player player) {player.sendMessage(getString("Messages.NothingToConfirm"));}
    public void sendConfirmationRequest(Player player, StrikeOrder so) {
        for (String s : getStringList("Messages.ConfirmMessage")) {
            String msg = s;
            if (msg.contains("%cost%")) {msg = msg.replace("%cost%", String.valueOf(so.getCost()));}
            player.sendMessage(msg);
        }
    }
    public void summonHelix(Location loc) {
        int radius = 1;
        for (double y = 0; y <= 50; y += 0.05) {
            double x = radius * Math.cos(y);
            double z = radius * Math.sin(y);
            Location locc = new Location(loc.getWorld(), loc.getBlockX() + x, loc.getBlockY() + y, loc.getBlockZ() + z);
            loc.getWorld().playEffect(locc, Effect.COLOURED_DUST, 1);
        }
    }
    public void sendForbiddenRegion(Player player) {
        sendList(getStringList("Messages.ForbiddenTargetRegion"), player);
    }
    public void sendNotInWGRegions(Player player) {
        sendList(getStringList("Messages.NotInWGRegion"), player);
    }
    public void sendTitleIfEnabled(Player player) {
        if (!OrbMain.getInstance().getConfig().getBoolean("Title.Enabled")) {return;}
        player.sendTitle(getString("Title.Message"), "");
    }
    public void sendAutomaticTargetSet(Player p) {
        StrikeOrder so = OrbMain.getInstance().sO.get(p);
        for (String s : getStringList("Messages.AutomaticTargetSet")) {
            String msg = s;
            if (msg.contains("%Target%")) {msg = msg.replace("%Target%", so.getTarget().getName());}
            if (msg.contains("%TargetDisplayName%")) {msg = msg.replace("%TargetDisplayName%", so.getTarget().getDisplayName());}
            p.sendMessage(msg);
        }
    }
}
