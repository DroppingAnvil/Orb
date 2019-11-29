package io.github.droppinganvil;

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
                    Util.getInstance().makeView(owner, viewLoc, this);
                    Util.getInstance().summonHelix(strikeloc);
                    while (booms > 0) {
                        w.createExplosion(strikeloc, OrbMain.getInstance().getConfig().getInt("Limits.TNTPower"));
                        booms--;
                    }
                    OrbMain.getInstance().sO.remove(owner);
                } else {Util.getInstance().sendInsufficientFunds(owner); return;}
            } else {
                if (!Hook.getInstance().isPlayerVulnerable(target)) {Util.getInstance().sendTargetNotFound(owner); return;}
                if (Hook.getInstance().chargePlayer(owner, money)) {
                OrbMain.getInstance().death.put(target, owner);
                Util.getInstance().summonHelix(target.getLocation());
                FactionHook.tempDisableTitles(target);
                target.setHealth(0.0);
                Util.getInstance().sendTitleIfEnabled(target);
                while (booms > 0) {
                    w.createExplosion(target.getLocation(), OrbMain.getInstance().getConfig().getInt("Limits.TNTPower"));
                    booms--;
                }
                OrbMain.getInstance().sO.remove(owner);
                } else {Util.getInstance().sendInsufficientFunds(owner); return;}
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

}
