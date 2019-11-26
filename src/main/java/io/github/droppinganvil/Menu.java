package io.github.droppinganvil;

import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.InventoryHolder;

public interface Menu extends InventoryHolder {
    void onClick(int slot, ClickType ctype);
    void build();
}
