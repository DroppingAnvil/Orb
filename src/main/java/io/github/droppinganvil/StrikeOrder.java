package io.github.droppinganvil;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import javax.annotation.Nullable;

public class StrikeOrder {
    private Player owner;
    private Player target;
    private int x;
    private int z;
    private int money;
    private Boolean ma;
    private World w;
    private int booms;
    private Boolean xzset;
    private Boolean confirmed;
    private Location oldloc;
    public StrikeOrder(Player p, Integer xX, Integer zZ, Integer cost, @Nullable Player victim, Boolean manual, Integer explosions) {
        x = xX;
        z = zZ;
        ma = manual;
        owner = p;
        money = cost;
        target = victim;
        booms = explosions;
        w = p.getWorld();
        //Doesnt mean its actually confirmed just first confirmation
        confirmed = false;
        xzset = false;
        OrbMain.getInstance().sO.put(owner, this);
    }
    public void doStrike() {
            if (ma) {
                if (Hook.getInstance().chargePlayer(owner, money)) {
                    Location strikeloc = new Location(w, x, w.getHighestBlockYAt(x, z), z);
                    Location viewLoc = new Location(w, x, w.getHighestBlockYAt(x, z) + OrbMain.getInstance().getConfig().getInt("PlayerView.BlocksAboveExplosion"), z);
                    viewLoc.setPitch(90);
                    OrbUtil.getInstance().makeView(owner, viewLoc, this);
                    OrbUtil.getInstance().summonHelix(strikeloc);
                    while (booms > 0) {
                        strikeloc = new Location(w, x, w.getHighestBlockYAt(x, z), z);
                        w.createExplosion(strikeloc, OrbMain.getInstance().getConfig().getInt("Limits.TNTPower"));
                        booms--;
                    }
                    OrbMain.getInstance().sO.remove(owner);
                } else {
                    OrbUtil.getInstance().sendInsufficientFunds(owner); return;}
            } else {
                if (!Hook.getInstance().isPlayerVulnerable(target)) {
                    OrbUtil.getInstance().sendTargetNotFound(owner); return;}
                if (Hook.getInstance().chargePlayer(owner, money)) {
                OrbUtil.getInstance().summonHelix(target.getLocation());
                FactionHook.tempDisableTitles(target);
                OrbMain.getInstance().s.add(target);
                target.setHealth(0.0);
                Bukkit.broadcastMessage(getDeathMesaage());
                OrbUtil.getInstance().sendTitleIfEnabled(target);
                while (booms > 0) {
                    w.createExplosion(target.getLocation(), OrbMain.getInstance().getConfig().getInt("Limits.TNTPower"));
                    booms--;
                }
                OrbMain.getInstance().sO.remove(owner);
                } else {
                    OrbUtil.getInstance().sendInsufficientFunds(owner); return;}
            }
    }
    public boolean isManual() {return ma;}
    public boolean isXZSet() {return xzset;}
    public void setXZSet(Boolean b) {xzset = b;}
    public boolean isConfirmed() {return confirmed;}
    public void setConfirmed(Boolean b) {confirmed = b;}
    public int getX() {return x;}
    public int getZ() {return z;}
    public void setX(int i) {x = i;}
    public void setZ(int i) {z = i;}
    public Player getTarget() {return target;}
    public Player getOwner() {return target;}
    public Integer getCost() {return money;}
    public Location getOldLoc() {return oldloc;}
    public void setOldLoc(Location loc) {oldloc = loc.clone();}
    public String getDeathMesaage() {
        String msg = OrbUtil.getInstance().getString("Messages.DeathMessage");
        if (msg.contains("%victim%")) {msg = msg.replace("%victim%", target.getName());}
        if (msg.contains("%striker%")) {msg = msg.replace("%striker%", owner.getName());}
        return msg;
    }

}
