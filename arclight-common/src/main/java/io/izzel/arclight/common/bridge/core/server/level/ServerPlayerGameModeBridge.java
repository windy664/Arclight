package io.izzel.arclight.common.bridge.core.server.level;

import io.izzel.arclight.common.mod.util.ArclightCaptures;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;

public interface ServerPlayerGameModeBridge {

    default boolean bridge$isFiredInteract() {
        return false;
    }

    default void bridge$setFiredInteract(boolean b) {

    }

    default boolean bridge$getInteractResult() {
        return false;
    }

    default void bridge$setInteractResult(boolean b) {

    }

    default void bridge$handleBlockDrop(ArclightCaptures.BlockBreakEventContext breakEventContext, BlockPos pos) {

    }

    default BlockPos bridge$getInteractPosition() {
        return null;
    }

    default InteractionHand bridge$getInteractHand() {
        return null;
    }

    default ItemStack bridge$getInteractItemStack() {
        return null;
    }
}
