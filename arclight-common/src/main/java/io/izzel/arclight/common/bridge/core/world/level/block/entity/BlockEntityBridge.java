package io.izzel.arclight.common.bridge.core.world.level.block.entity;

import org.bukkit.inventory.InventoryHolder;

public interface BlockEntityBridge {

    default InventoryHolder bridge$getOwner() {
        return null;
    }
}
