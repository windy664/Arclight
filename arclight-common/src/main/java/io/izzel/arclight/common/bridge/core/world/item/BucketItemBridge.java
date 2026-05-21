package io.izzel.arclight.common.bridge.core.world.item;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public interface BucketItemBridge {
    @Nullable Direction arclight$getDirection();

    default void arclight$setDirection(@Nullable Direction value) {

    }

    default @Nullable BlockPos arclight$getClick() {
        return null;
    }

    default void arclight$setClick(@Nullable BlockPos value) {

    }

    default @Nullable InteractionHand arclight$getHand() {
        return null;
    }

    default void arclight$setHand(@Nullable InteractionHand value) {

    }

    default @Nullable ItemStack arclight$getStack() {
        return null;
    }

    default void arclight$setStack(@Nullable ItemStack value) {

    }

    default @Nullable org.bukkit.inventory.ItemStack arclight$getCaptureItem() {
        return null;
    }

    default void arclight$setCaptureItem(@Nullable org.bukkit.inventory.ItemStack value) {

    }
}
