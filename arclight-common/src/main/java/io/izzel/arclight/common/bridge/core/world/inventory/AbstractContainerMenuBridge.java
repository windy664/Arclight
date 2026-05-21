package io.izzel.arclight.common.bridge.core.world.inventory;

import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.AbstractContainerMenu;
import org.bukkit.craftbukkit.entity.CraftHumanEntity;
import org.bukkit.inventory.InventoryView;

public interface AbstractContainerMenuBridge {

    default InventoryView bridge$getBukkitView() {
        return null;
    }

    default void bridge$transferTo(AbstractContainerMenu other, CraftHumanEntity player) {

    }

    default Component bridge$getTitle() {
        return null;
    }

    default void bridge$setTitle(Component title) {

    }

    default boolean bridge$isCheckReachable() {
        return false;
    }
}
