package io.izzel.arclight.common.bridge.core.world.entity.player;

import net.minecraft.world.item.ItemStack;

public interface InventoryBridge {

    default int bridge$canHold(ItemStack stack) {
        return 0;
    }
}
