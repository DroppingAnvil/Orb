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
    private HashMap<Integer, ItemStack> imap = new HashMap<Integer, ItemStack>();
    private HashMap<Integer, Options> map = new HashMap<Integer, Options>();
    public MainMenu(Player p) {
        player = p;
        payload = 1;
        index = 1;
        fill = XMaterial.matchXMaterial(OrbMain.getInstance().getConfig().getString("GUI.FillItem.Material")).parseItem();
        ItemMeta meta = fill.getItemMeta();
        meta.setDisplayName(OrbUtil.getInstance().getString("GUI.FillItem.Name"));
        if (!OrbUtil.getInstance().getStringList("GUI.FillItem.Lore").get(0).equals("")) {
            meta.setLore(OrbUtil.getInstance().getStringList("GUI.FillItem.Lore"));
        }
        fill.setItemMeta(meta);
        //Generate nav items
        back = XMaterial.matchXMaterial(config.getString("GUI.BackItem.Material")).parseItem();
        ItemMeta bmeta = back.getItemMeta();
        bmeta.setDisplayName(OrbUtil.getInstance().getString("GUI.BackItem.Name"));
        back.setItemMeta(bmeta);
        advance = XMaterial.matchXMaterial(config.getString("GUI.ContinueItem.Material")).parseItem();
        ItemMeta ameta = advance.getItemMeta();
        ameta.setDisplayName(OrbUtil.getInstance().getString("GUI.ContinueItem.Name"));
        advance.setItemMeta(ameta);
    }
    public void buildAndRefresh() {
        this.in = null;
        build();
        this.player.openInventory(this.in);
    }
    public void onClick(int slot, ClickType ctype) {
        if (!map.keySet().contains(slot)) {return;}
        Options selected = map.get(slot);
        switch (selected) {
            case Manual:
                type = Options.Manual;
                index = 2;
                buildAndRefresh();
                break;
            case Autotarget:
                type = Options.Autotarget;
                index++;
                buildAndRefresh();
                break;
            case Back:
                index--;
                buildAndRefresh();
                break;
            case Advance:
                if (type.equals(Options.Manual)) {
                    Integer cost = config.getInt("Cost.Manual.StartingPrice") + config.getInt("Cost.Manual.PricePerExtraTNT") * payload;
                    System.out.print(cost);
                    OrbMain.getInstance().sO.put(player, new StrikeOrder(player, 0,0, cost, null, true, payload));
                    OrbMain.getInstance().waitingManual.add(player);
                    player.closeInventory();
                    OrbUtil.getInstance().sendPlayerManualInstructions(player);
                    return;
                }
                index++;
                buildAndRefresh();
                break;
            case DecreasePayload:
                if (payload > 1) {payload--; buildAndRefresh();}
                break;
            case IncreasePayload:
                if (payload < config.getInt("Limits.MaximumTNTPerStrike")) {payload++; buildAndRefresh();}
                break;
            case NonResponsive: {}
            break;
            case Head:
                int cost = config.getInt("Cost.Automatic.StartingPrice") + config.getInt("Cost.Automatic.PricePerExtraTNT") * payload;
                OrbMain.getInstance().sO.put(player, new StrikeOrder(player, 0,0, cost, Bukkit.getPlayer(imap.get(slot).getItemMeta().getDisplayName()), false, payload));
                OrbUtil.getInstance().sendAutomaticTargetSet(player);
                player.closeInventory();
                break;
        }
    }

    public void build() {
        switch (index) {
            case 1:
                //Clear out old build memory
                if (in != null) {in.clear();}
                imap.clear();
                map.clear();
                size = config.getInt("GUI.LayerOne.Size");
                //Generate manual item
                ItemStack manual = XMaterial.matchXMaterial(config.getString("GUI.LayerOne.ManualItem.Material")).parseItem();
                ItemMeta meta = manual.getItemMeta();
                meta.setDisplayName(OrbUtil.getInstance().getString("GUI.LayerOne.ManualItem.Name"));
                meta.setLore(OrbUtil.getInstance().getStringList("GUI.LayerOne.ManualItem.Lore"));
                manual.setItemMeta(meta);
                imap.put(config.getInt("GUI.LayerOne.ManualItem.Slot"), manual);
                map.put(config.getInt("GUI.LayerOne.ManualItem.Slot"), Options.Manual);
                //Generate auto item
                ItemStack auto = XMaterial.matchXMaterial(config.getString("GUI.LayerOne.AutoItem.Material")).parseItem();
                ItemMeta ameta = manual.getItemMeta();
                ameta.setDisplayName(OrbUtil.getInstance().getString("GUI.LayerOne.AutoItem.Name"));
                ameta.setLore(OrbUtil.getInstance().getStringList("GUI.LayerOne.AutoItem.Lore"));
                auto.setItemMeta(ameta);
                imap.put(config.getInt("GUI.LayerOne.AutoItem.Slot"), auto);
                map.put(config.getInt("GUI.LayerOne.AutoItem.Slot"), Options.Autotarget);
                break;
            case 2:
                //Clear out old build memory
                if (in != null) {in.clear();}
                imap.clear();
                map.clear();
                //Generate payload screen
                size = config.getInt("GUI.LayerTwo.Size");
                //Generate payload item
                ItemStack pay = new ItemStack(XMaterial.matchXMaterial(config.getString("GUI.LayerTwo.PayloadItem.Material")).parseMaterial(), payload);
                ItemMeta paymeta = pay.getItemMeta();
                paymeta.setDisplayName(OrbUtil.getInstance().getString("GUI.LayerTwo.PayloadItem.Name"));
                paymeta.setLore(OrbUtil.getInstance().getStringList("GUI.LayerTwo.PayloadItem.Lore"));
                pay.setItemMeta(paymeta);
                imap.put(config.getInt("GUI.LayerTwo.PayloadItem.Slot"), pay);
                map.put(config.getInt("GUI.LayerTwo.PayloadItem.Slot"), Options.NonResponsive);
                //Generate increase item
                ItemStack up = XMaterial.matchXMaterial(config.getString("GUI.LayerTwo.IncreaseItem.Material")).parseItem();
                ItemMeta upmeta = up.getItemMeta();
                upmeta.setDisplayName(OrbUtil.getInstance().getString("GUI.LayerTwo.IncreaseItem.Name"));
                upmeta.setLore(OrbUtil.getInstance().getStringList("GUI.LayerTwo.IncreaseItem.Lore"));
                up.setItemMeta(upmeta);
                imap.put(config.getInt("GUI.LayerTwo.IncreaseItem.Slot"), up);
                map.put(config.getInt("GUI.LayerTwo.IncreaseItem.Slot"), Options.IncreasePayload);
                //Generate decrease item
                ItemStack down = XMaterial.matchXMaterial(config.getString("GUI.LayerTwo.DecreaseItem.Material")).parseItem();
                ItemMeta downmeta = down.getItemMeta();
                downmeta.setDisplayName(OrbUtil.getInstance().getString("GUI.LayerTwo.DecreaseItem.Name"));
                downmeta.setLore(OrbUtil.getInstance().getStringList("GUI.LayerTwo.DecreaseItem.Lore"));
                down.setItemMeta(downmeta);
                imap.put(config.getInt("GUI.LayerTwo.DecreaseItem.Slot"), down);
                map.put(config.getInt("GUI.LayerTwo.DecreaseItem.Slot"), Options.DecreasePayload);
                imap.put(config.getInt("GUI.LayerTwo.BackItem.Slot"), back);
                map.put(config.getInt("GUI.LayerTwo.BackItem.Slot"), Options.Back);
                imap.put(config.getInt("GUI.LayerTwo.ContinueItem.Slot"), advance);
                map.put(config.getInt("GUI.LayerTwo.ContinueItem.Slot"), Options.Advance);
                break;
            case 3:
                //Clear out old build memory
                imap.clear();
                map.clear();
                //Generate player screen
                size = config.getInt("GUI.LayerThree.Size");
                int index = 0;
                for (Player p : Bukkit.getOnlinePlayers()) {
                    if (Hook.getInstance().isPlayerVulnerable(p)) {
                        ItemStack head = new ItemStack(XMaterial.matchXMaterial(Material.LEGACY_SKULL_ITEM).parseMaterial(), 1, (short) 3);
                        SkullMeta skullMeta = (SkullMeta) head.getItemMeta();
                        skullMeta.setOwner(p.getName());
                        skullMeta.setDisplayName(p.getName());
                        head.setItemMeta(skullMeta);
                        imap.put(index, head);
                        map.put(index++, Options.Head);
                }
            }
                break;
        }
            in = Bukkit.createInventory(this, size);
            for (int fill = 0; fill < size; ++fill) {
                in.setItem(fill, this.fill);
            }
            for (Integer i : imap.keySet()) {
                in.setItem(i, imap.get(i));
            }
        }

    public Inventory getInventory() {
        return in;
    }
}
