package io.github.droppinganvil;

import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Command implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, org.bukkit.command.Command command, String s, String[] args) {
        if (!(sender instanceof Player)) {Util.getInstance().sendPlayerOnly(sender);}
        Player player = (Player) sender;
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
