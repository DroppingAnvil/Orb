package io.github.droppinganvil;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.zcore.fperms.Access;
import com.massivecraft.factions.zcore.fperms.PermissableAction;
import org.bukkit.GameMode;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;

public class PlayerListeners implements Listener {
    @EventHandler (ignoreCancelled = true)
    public void onJoin(PlayerJoinEvent e) {
        //No need for UUID checking except for viewing when left as they would have to relog anyways
        if (e.getPlayer().getInventory().contains(Util.getInstance().ghostItem)) {Util.getInstance().ghostPlayers.add(e.getPlayer().getName());}
        if (!Util.getInstance().isGhost(e.getPlayer()) && !Util.getInstance().ghostPlayers.contains(e.getPlayer().getName())) {OrbMain.getInstance().autotargetable.add(e.getPlayer());}
        if (OrbMain.getInstance().leftDuringView.contains(e.getPlayer().getUniqueId().toString())) {
            e.getPlayer().setGameMode(GameMode.SURVIVAL);
            if (OrbMain.getInstance().getConfig().getString("PlayerView.ExploitPrevention.Strategy").equals("FactionHome")) {
                FPlayer f = FPlayers.getInstance().getByPlayer(e.getPlayer());
                Faction fac = f.getFaction();
                if (!fac.isSystemFaction() && fac.getAccess(f, PermissableAction.HOME).equals(Access.ALLOW)) {
                    e.getPlayer().teleport(fac.getHome());
                    return;
                }
            } else {e.getPlayer().teleport(e.getPlayer().getWorld().getSpawnLocation()); return;}
        }
    }
    @EventHandler
    public void onDisconnect(PlayerQuitEvent e) {
        if (Util.getInstance().ghostPlayers.contains(e.getPlayer().getName())) {Util.getInstance().ghostPlayers.remove(e.getPlayer().getName());}
    if (OrbMain.getInstance().autotargetable.contains(e.getPlayer().getName())) {OrbMain.getInstance().autotargetable.remove(e.getPlayer().getName());}
    if (OrbMain.getInstance().viewing.contains(e.getPlayer())) {OrbMain.getInstance().leftDuringView.add(e.getPlayer().getUniqueId().toString());}
    if (OrbMain.getInstance().waitingManual.contains(e.getPlayer())) {OrbMain.getInstance().waitingManual.remove(e.getPlayer());}
    if (OrbMain.getInstance().sO.containsKey(e.getPlayer())) {OrbMain.getInstance().sO.remove(e.getPlayer());}
    }
    @EventHandler (ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onDeath(PlayerDeathEvent e) {
        if (OrbMain.getInstance().death.containsKey(e.getEntity())) {
            String msg = Util.getInstance().getString("Messages.DeathMessage");
            if (msg.contains("%victim%")) {msg = msg.replace("%victim%", e.getEntity().getName());}
            if (msg.contains("%striker%")) {msg = msg.replace("%striker%", OrbMain.getInstance().death.get(e.getEntity()).getName());}
            e.setDeathMessage(msg);
        }
    }
    @EventHandler
    public void onGUI(InventoryClickEvent e) {
        if (!(e.getClickedInventory().getHolder() instanceof Menu)) {return;}
        e.setCancelled(true);
        ((Menu) e.getClickedInventory().getHolder()).onClick(e.getRawSlot(), e.getClick());
    }
    @EventHandler (priority = EventPriority.LOWEST)
    public void onChat(AsyncPlayerChatEvent e) {
        int x = 0;
        int z = 0;
        if (OrbMain.getInstance().waitingManual.contains(e.getPlayer())) {
            if (!e.getMessage().contains(" ")) {Util.getInstance().sendManualInstructionHelp(e.getPlayer()); return;}
            String[] xz = e.getMessage().split(" ");
            if (xz.length != 2) {Util.getInstance().sendManualInstructionHelp(e.getPlayer()); return;}
            try {
                x = Integer.valueOf(xz[0]);
                z = Integer.valueOf(xz[1]);
            } catch (NumberFormatException ex) {Util.getInstance().sendManualInstructionHelp(e.getPlayer()); return;}
            OrbMain.getInstance().sO.get(e.getPlayer()).setX(x);
            OrbMain.getInstance().sO.get(e.getPlayer()).setZ(z);
            Util.getInstance().sendManualCoordsSet(e.getPlayer(), OrbMain.getInstance().sO.get(e.getPlayer()));
            OrbMain.getInstance().waitingManual.remove(e.getPlayer());
        }
    }
    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        if (!OrbMain.getInstance().viewing.contains(e.getPlayer())) {return;}
        e.setCancelled(true);
    }
    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        if (!OrbMain.getInstance().viewing.contains(e.getPlayer())) {return;}
        e.setCancelled(true);
    }
}
