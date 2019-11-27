package io.github.droppinganvil;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;

public class ConfirmMenu implements Menu {
    private HashMap<Integer, Options> map;
    private HashMap<Integer, ItemStack> imap;
    private Player user;
    private Inventory in;
    private int size = OrbMain.getInstance().getConfig().getInt("GUI.ConfirmationMenu.Size");
    private FileConfiguration conf = OrbMain.getInstance().getConfig();

    public ConfirmMenu(Player player) {
        map = new HashMap<>();
        imap = new HashMap<>();
        user = player;
    }
    @Override
    public void onClick(int slot, ClickType ctype) {
        Options option = map.get(slot);
        switch (option) {
            case DoStrike:
                user.closeInventory();
                OrbMain.getInstance().sO.get(user).doStrike();
            case Cancel:
                user.closeInventory();
                OrbMain.getInstance().sO.remove(user);
        }
    }

    @Override
    public void build() {
        ItemStack confirm = new ItemStack(XMaterial.matchXMaterial(conf.getString("GUI.ConfirmationMenu.ConfirmItem.Material")).parseMaterial());
        ItemMeta confirmmeta = confirm.getItemMeta();
        confirmmeta.setDisplayName(Util.getInstance().getString("GUI.ConfirmationMenu.ConfirmItem.Name"));
        confirmmeta.setLore(Util.getInstance().getStringList("GUI.ConfirmationMenu.ConfirmItem.Lore"));
        confirm.setItemMeta(confirmmeta);
        map.put(conf.getInt("GUI.ConfirmationMenu.ConfirmItem.Slot"), Options.DoStrike);
        imap.put(conf.getInt("GUI.ConfirmationMenu.ConfirmItem.Slot"), confirm);
        ItemStack cancel = new ItemStack(XMaterial.matchXMaterial(conf.getString("GUI.ConfirmationMenu.CancelItem.Material")).parseMaterial());
        ItemMeta cancelmeta = cancel.getItemMeta();
        cancelmeta.setDisplayName(Util.getInstance().getString("GUI.ConfirmationMenu.CancelItem.Name"));
        cancelmeta.setLore(Util.getInstance().getStringList("GUI.ConfirmationMenu.CancelItem.Lore"));
        cancel.setItemMeta(cancelmeta);
        map.put(conf.getInt("GUI.ConfirmationMenu.CancelItem.Slot"), Options.Cancel);
        imap.put(conf.getInt("GUI.ConfirmationMenu.CancelItem.Slot"), cancel);
        in = Bukkit.createInventory(this, size);
        for (Integer i : imap.keySet()) {
            in.setItem(i, imap.get(i));
        }
    }

    @Override
    public Inventory getInventory() {
        return in;
    }
}
