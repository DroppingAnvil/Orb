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
    private ItemStack fill;

    public ConfirmMenu(Player player) {
        map = new HashMap<>();
        imap = new HashMap<>();
        user = player;
        fill = XMaterial.matchXMaterial(OrbMain.getInstance().getConfig().getString("GUI.FillItem.Material")).parseItem();
        ItemMeta meta = fill.getItemMeta();
        meta.setDisplayName(OrbUtil.getInstance().getString("GUI.FillItem.Name"));
        if (!OrbUtil.getInstance().getStringList("GUI.FillItem.Lore").get(0).equals("")) {
            meta.setLore(OrbUtil.getInstance().getStringList("GUI.FillItem.Lore"));
        }
        fill.setItemMeta(meta);
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
        ItemStack confirm = XMaterial.matchXMaterial(conf.getString("GUI.ConfirmationMenu.ConfirmItem.Material")).parseItem();
        ItemMeta confirmmeta = confirm.getItemMeta();
        confirmmeta.setDisplayName(OrbUtil.getInstance().getString("GUI.ConfirmationMenu.ConfirmItem.Name"));
        confirmmeta.setLore(OrbUtil.getInstance().getStringList("GUI.ConfirmationMenu.ConfirmItem.Lore"));
        confirm.setItemMeta(confirmmeta);
        map.put(conf.getInt("GUI.ConfirmationMenu.ConfirmItem.Slot"), Options.DoStrike);
        imap.put(conf.getInt("GUI.ConfirmationMenu.ConfirmItem.Slot"), confirm);
        ItemStack cancel = XMaterial.matchXMaterial(conf.getString("GUI.ConfirmationMenu.CancelItem.Material")).parseItem();
        ItemMeta cancelmeta = cancel.getItemMeta();
        cancelmeta.setDisplayName(OrbUtil.getInstance().getString("GUI.ConfirmationMenu.CancelItem.Name"));
        cancelmeta.setLore(OrbUtil.getInstance().getStringList("GUI.ConfirmationMenu.CancelItem.Lore"));
        cancel.setItemMeta(cancelmeta);
        map.put(conf.getInt("GUI.ConfirmationMenu.CancelItem.Slot"), Options.Cancel);
        imap.put(conf.getInt("GUI.ConfirmationMenu.CancelItem.Slot"), cancel);
        in = Bukkit.createInventory(this, size);
        for (int fill = 0; fill < size; ++fill) {
            in.setItem(fill, this.fill);
        }
        for (Integer i : imap.keySet()) {
            in.setItem(i, imap.get(i));
        }
    }

    @Override
    public Inventory getInventory() {
        return in;
    }
}
