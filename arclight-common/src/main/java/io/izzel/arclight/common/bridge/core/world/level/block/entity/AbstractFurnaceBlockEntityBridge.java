package io.izzel.arclight.common.bridge.core.world.level.block.entity;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;

import java.util.List;

public interface AbstractFurnaceBlockEntityBridge {

    default List<RecipeHolder<?>> bridge$dropExp(ServerPlayer entity, ItemStack itemStack, int amount) {
        return null;
    }

    default int bridge$getBurnDuration(ItemStack stack) {
        return 0;
    }

    default boolean bridge$isLit() {
        return false;
    }
}
