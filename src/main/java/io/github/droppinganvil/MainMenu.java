package io.github.droppinganvil;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.HashMap;

public class MainMenu implements Menu {
    private Player player;
    private FileConfiguration config = OrbMain.getInstance().getConfig();
    private Integer size;
    private Integer payload;
    private Options type;
    private Integer index;
    private Inventory in;
    private ItemStack fill;
    private ItemStack back;
    private ItemStack advance;
    private HashMap<Integer, ItemStack> imap = new HashMap<>();
    private HashMap<Integer, Options> map = new HashMap<Integer, Options>();
    public MainMenu(Player player) {
        payload = 1;
        index = 1;
        fill = new ItemStack(XMaterial.matchXMaterial(OrbMain.getInstance().getConfig().getString("GUI.FillItem.Material")).parseItem());
        ItemMeta meta = fill.getItemMeta();
        meta.setDisplayName(Util.getInstance().getString("GUI.FillItem.Name"));
        meta.setLore(Util.getInstance().getStringList("GUI.FillItem.Lore"));
        fill.setItemMeta(meta);
        //Generate nav items
        back = new ItemStack(XMaterial.matchXMaterial(config.getString("GUI.BackItem.Material")).parseMaterial(), 1);
        ItemMeta bmeta = back.getItemMeta();
        bmeta.setDisplayName(Util.getInstance().getString("GUI.BackItem.Name"));
        back.setItemMeta(bmeta);
        advance = new ItemStack(XMaterial.matchXMaterial(config.getString("GUI.ContinueItem.Material")).parseMaterial(), 1);
        ItemMeta ameta = advance.getItemMeta();
        ameta.setDisplayName(Util.getInstance().getString("GUI.ContinueItem.Name"));
        advance.setItemMeta(ameta);
    }
    public void buildAndRefresh() {
        build();
        player.openInventory(in);
    }
    public void onClick(int slot, ClickType ctype) {
        if (!map.keySet().contains(slot)) {return;}
        Options selected = map.get(slot);
        switch (selected) {
            case Manual:
                type = Options.Manual;
                index = 2;
                buildAndRefresh();
            case Autotarget:
                type = Options.Autotarget;
                index = 2;
                buildAndRefresh();
            case Back:
                index--;
                buildAndRefresh();
            case Advance:
                if (type.equals(Options.Manual)) {
                    int cost = config.getInt("Costs.Manual.StartingPrice") + config.getInt("Costs.Manual.PricePerExtraTNT") * payload;
                    OrbMain.getInstance().sO.put(player, new StrikeOrder(player, 0,0, cost, null, true, payload));
                    OrbMain.getInstance().waitingManual.add(player);
                    player.closeInventory();
                    Util.getInstance().sendPlayerManualInstructions(player);
                    return;
                }
                index++;
                buildAndRefresh();
            case DecreasePayload:
                if (payload > 1) {payload--; buildAndRefresh();}
            case IncreasePayload:
                if (payload < config.getInt("Limits.MaximumTNTPerStrike")) {payload++; buildAndRefresh();}
            case NonResponsive: {}
            case Head:
                int cost = config.getInt("Costs.Automatic.StartingPrice") + config.getInt("Costs.Automatic.PricePerExtraTNT") * payload;
                OrbMain.getInstance().sO.put(player, new StrikeOrder(player, 0,0, cost, Bukkit.getPlayer(imap.get(slot).getItemMeta().getDisplayName()), false, payload));

                player.closeInventory();
        }
    }

    public void build() {
        switch (index) {
            case 1: {
                size = config.getInt("GUI.LayerOne.Size");
                //Generate manual item
                ItemStack manual = new ItemStack(XMaterial.matchXMaterial(config.getString("GUI.LayerOne.ManualItem.Material")).parseMaterial(), 1);
                ItemMeta meta = manual.getItemMeta();
                meta.setDisplayName(Util.getInstance().getString("GUI.LayerOne.ManualItem.Name"));
                meta.setLore(Util.getInstance().getStringList("GUI.LayerOne.ManualItem.Lore"));
                manual.setItemMeta(meta);
                imap.put(config.getInt("GUI.LayerOne.ManualItem.Slot"), manual);
                map.put(config.getInt("GUI.LayerOne.ManualItem.Slot"), Options.Manual);
                //Generate auto item
                ItemStack auto = new ItemStack(XMaterial.matchXMaterial(config.getString("GUI.LayerOne.AutoItem.Material")).parseMaterial(), 1);
                ItemMeta ameta = manual.getItemMeta();
                ameta.setDisplayName(Util.getInstance().getString("GUI.LayerOne.AutoItem.Name"));
                ameta.setLore(Util.getInstance().getStringList("GUI.LayerOne.AutoItem.Lore"));
                manual.setItemMeta(ameta);
                imap.put(config.getInt("GUI.LayerOne.AutoItem.Slot"), auto);
                map.put(config.getInt("GUI.LayerOne.AutoItem.Slot"), Options.Autotarget);
            }
        }
        System.out.print(index);
        System.out.print(size);
        in = Bukkit.createInventory(this, size);
        for (Integer i : imap.keySet()) {
            in.setItem(i, imap.get(i));
        }
    }

    public Inventory getInventory() {
        return in;
    }
}
