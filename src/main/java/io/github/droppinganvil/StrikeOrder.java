package io.github.droppinganvil;

import jdk.internal.jline.internal.Nullable;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.lang.reflect.Array;

public class StrikeOrder {
    private Player owner;
    private Player target;
    private String xz;
    private Integer money;
    private Boolean ma;
    private Location loc;
    public StrikeOrder(Player p, @Nullable String sloc, Integer cost, @Nullable Player victim) {
        owner = p;
        xz = sloc;
        money = cost;
        target = victim;
        loc = p.getLocation().clone();
        OrbMain.getInstance().sO.put(owner, this);
    }
    public void doStrike() {
        if (Hook.getInstance().chargePlayer(owner, money)) {
            String[] xZ = xz.split(",");

        } else {Util.getInstance().sendInsufficientFunds(owner); }
    }
    public Player getTarget() {return target;}
    public Player getOwner() {return target;}
    public Integer getCost() {return money;}
    public Location getStartLoc() {return loc;}

}
