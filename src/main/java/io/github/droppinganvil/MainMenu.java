package io.github.droppinganvil;

import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;

import java.util.HashMap;

public class MainMenu implements Menu {
    private Inventory in;
    private HashMap<Integer, Options> map = new HashMap<Integer, Options>();
    public MainMenu() {}
    public void onClick(int slot, ClickType ctype) {
        if (!map.keySet().contains(slot)) {return;}
        Options selected = map.get(slot);
        switch (selected) {
            case Manual:
        }
    }

    public void build() {

    }

    public Inventory getInventory() {
        return null;
    }
}
