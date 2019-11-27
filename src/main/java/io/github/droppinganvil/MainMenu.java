package io.github.droppinganvil;

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
    public MainMenu() {
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
    public void onClick(int slot, ClickType ctype) {
        if (!map.keySet().contains(slot)) {return;}
        Options selected = map.get(slot);
        switch (selected) {
            case Manual:
        }
    }

    public void build() {
        switch (index) {
            case 1:
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
            case 2:
                //Clear out old build memory
                imap.clear();
                map.clear();
                //Generate payload screen
                size = config.getInt("GUI.LayerTwo.Size");
                //Generate increase item
                ItemStack up = new ItemStack(XMaterial.matchXMaterial(config.getString("GUI.LayerTwo.IncreaseItem.Material")).parseMaterial(), 1);
                ItemMeta upmeta = up.getItemMeta();
                upmeta.setDisplayName(Util.getInstance().getString("GUI.LayerTwo.IncreaseItem.Name"));
                upmeta.setLore(Util.getInstance().getStringList("GUI.LayerTwo.IncreaseItem.Lore"));
                up.setItemMeta(upmeta);
                imap.put(config.getInt("GUI.LayerTwo.IncreaseItem.Slot"), up);
                map.put(config.getInt("GUI.LayerTwo.IncreaseItem.Slot"), Options.IncreasePayload);
                //Generate decrease item
                ItemStack down = new ItemStack(XMaterial.matchXMaterial(config.getString("GUI.LayerTwo.DecreaseItem.Material")).parseMaterial(), 1);
                ItemMeta downmeta = down.getItemMeta();
                downmeta.setDisplayName(Util.getInstance().getString("GUI.LayerTwo.DecreaseItem.Name"));
                downmeta.setLore(Util.getInstance().getStringList("GUI.LayerTwo.DecreaseItem.Lore"));
                down.setItemMeta(downmeta);
                imap.put(config.getInt("GUI.LayerTwo.DecreaseItem.Slot"), down);
                map.put(config.getInt("GUI.LayerTwo.DecreaseItem.Slot"), Options.DecreasePayload);
                imap.put(config.getInt("GUI.LayerTwo.BackItem.Slot"), back);
                map.put(config.getInt("GUI.LayerTwo.BackItem.Slot"), Options.Back);
                imap.put(config.getInt("GUI.LayerTwo.ContinueItem.Slot"), advance);
                map.put(config.getInt("GUI.LayerTwo.ContinueItem.Slot"), Options.Advance);
            case 3:
                //Clear out old build memory
                imap.clear();
                map.clear();
                //Generate player screen
                size = config.getInt("GUI.LayerThree.Size");
                int index = 0;
                for (Player p : OrbMain.getInstance().autotargetable) {
                    if (Hook.getInstance().isPlayerVulnerable(p)) {
                        ItemStack head = new ItemStack(Material.SKULL_ITEM, 1);
                        SkullMeta skullMeta = (SkullMeta) head.getItemMeta();
                        skullMeta.setOwner(p.getName());
                        skullMeta.setDisplayName(p.getName());
                        head.setItemMeta(skullMeta);
                        imap.put(index, head.clone());
                        map.put(index++, Options.Head);
                    }
                }
        }

    }

    public Inventory getInventory() {
        return null;
    }
}
