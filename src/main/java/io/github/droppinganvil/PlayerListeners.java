package io.github.droppinganvil;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerListeners implements Listener {
    @EventHandler (ignoreCancelled = true)
    public void onJoin(PlayerJoinEvent e) {
        //No need for UUID checking as they would have to relog anyways
        if (e.getPlayer().getInventory().contains(Util.getInstance().ghostItem)) {Util.getInstance().ghostPlayers.add(e.getPlayer().getName());}
        if (!Util.getInstance().isGhost(e.getPlayer()) && !Util.getInstance().ghostPlayers.contains(e.getPlayer().getName()) && Hook.getInstance().isPlayerVulnerable(e.getPlayer())) {OrbMain.getInstance().autotargetable.add(e.getPlayer().getName());}
    }
    @EventHandler
    public void onDisconnect(PlayerQuitEvent e) {
        if (Util.getInstance().ghostPlayers.contains(e.getPlayer().getName())) {Util.getInstance().ghostPlayers.remove(e.getPlayer().getName());}
    if (OrbMain.getInstance().autotargetable.contains(e.getPlayer().getName())) {OrbMain.getInstance().autotargetable.remove(e.getPlayer().getName());}
    }
}
