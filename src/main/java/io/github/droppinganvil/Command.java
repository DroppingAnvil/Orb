package io.github.droppinganvil;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Iterator;
import java.util.List;

public class Command implements CommandExecutor {
    List<String> regions = OrbMain.getInstance().getConfig().getStringList("WorldGuard.ForcedRegions.Regions");
    @Override
    public boolean onCommand(CommandSender sender, org.bukkit.command.Command command, String s, String[] args) {
        if (!(sender instanceof Player)) {Util.getInstance().sendPlayerOnly(sender);}
        Player player = (Player) sender;
        if (OrbMain.getInstance().getConfig().getBoolean("WorldGuard.ForcedRegions.Enabled")) {
            Iterator<ProtectedRegion> i = Hook.getInstance().wgp.getRegionManager(player.getWorld()).getApplicableRegions(player.getLocation()).getRegions().iterator();
            Boolean r = true;
            while (i.hasNext()) {
                if (regions.contains(i.next().getId())) {r = false;}
                i.remove();
            }
            if (r) {Util.getInstance().sendNotInWGRegions(player); return false;}
        }

        if (args.length == 1) {
            switch (args[0].toLowerCase()) {
                case "clear": {
                    if (OrbMain.getInstance().sO.containsKey(player)) {
                        OrbMain.getInstance().sO.remove(player);
                    }
                    if (OrbMain.getInstance().waitingManual.contains(player)) {
                        OrbMain.getInstance().waitingManual.remove(player);
                    }
                    Util.getInstance().sendClearedMSG(player);
                    return false;
                }
                case "confirm": {
                    if (!(OrbMain.getInstance().sO.containsKey(player))) {Util.getInstance().sendNothingToConfirmMSG(player); return false;}
                    StrikeOrder so = OrbMain.getInstance().sO.get(player);
                    if (so.isManual() && !so.isXZSet()) {Util.getInstance().sendPlayerManualInstructions(player); return false;}
                    if (so.isConfirmed()) {
                        ConfirmMenu confirm = new ConfirmMenu(player);
                        confirm.build();
                        player.openInventory(confirm.getInventory());
                    } else {
                        Util.getInstance().sendConfirmationRequest(player, so);
                        so.setConfirmed(true);
                    }
                }
            }
        }
        if (args.length == 0) { if (OrbMain.getInstance().sO.containsKey(player)) {
            Util.getInstance().sendNoMultipleStrikes(player);} else {
            MainMenu menu = new MainMenu(player);
            menu.build();
            player.openInventory(menu.getInventory());
        }
        }
        return false;
    }
}
