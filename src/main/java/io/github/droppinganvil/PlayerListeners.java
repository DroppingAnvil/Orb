package io.github.droppinganvil;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.zcore.fperms.Access;
import com.massivecraft.factions.zcore.fperms.PermissableAction;
import org.bukkit.Bukkit;
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
        if (e.getPlayer().getInventory().contains(OrbUtil.getInstance().ghostItem)) {
            OrbUtil.getInstance().ghostPlayers.add(e.getPlayer().getName());}
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
        if (OrbUtil.getInstance().ghostPlayers.contains(e.getPlayer().getName())) {
            OrbUtil.getInstance().ghostPlayers.remove(e.getPlayer().getName());}
    if (OrbMain.getInstance().viewing.contains(e.getPlayer())) {OrbMain.getInstance().leftDuringView.add(e.getPlayer().getUniqueId().toString());}
    if (OrbMain.getInstance().waitingManual.contains(e.getPlayer())) {OrbMain.getInstance().waitingManual.remove(e.getPlayer());}
    if (OrbMain.getInstance().sO.containsKey(e.getPlayer())) {OrbMain.getInstance().sO.remove(e.getPlayer());}
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
            if (!e.getMessage().contains(" ")) {
                OrbUtil.getInstance().sendManualInstructionHelp(e.getPlayer()); return;}
            String[] xz = e.getMessage().split(" ");
            if (xz.length != 2) {
                OrbUtil.getInstance().sendManualInstructionHelp(e.getPlayer()); return;}
            try {
                x = Integer.valueOf(xz[0]);
                z = Integer.valueOf(xz[1]);
            } catch (NumberFormatException ex) {
                OrbUtil.getInstance().sendManualInstructionHelp(e.getPlayer()); return;}
            OrbMain.getInstance().sO.get(e.getPlayer()).setX(x);
            OrbMain.getInstance().sO.get(e.getPlayer()).setZ(z);
            OrbMain.getInstance().sO.get(e.getPlayer()).setXZSet(true);
            OrbUtil.getInstance().sendManualCoordsSet(e.getPlayer(), OrbMain.getInstance().sO.get(e.getPlayer()));
            OrbMain.getInstance().waitingManual.remove(e.getPlayer());
            e.setCancelled(true);
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
    @EventHandler
    public void onDeath(PlayerDeathEvent e) {
        if (OrbMain.getInstance().s.contains(e.getEntity())) {
            e.setDeathMessage(null);
            OrbMain.getInstance().s.remove(e.getEntity());
            if (OrbMain.getInstance().getConfig().getBoolean("AutoRespawn")) {
                Bukkit.getScheduler().scheduleSyncDelayedTask(OrbMain.getInstance(), () -> e.getEntity().spigot().respawn(), 1L);
            }
        }
    }
}
