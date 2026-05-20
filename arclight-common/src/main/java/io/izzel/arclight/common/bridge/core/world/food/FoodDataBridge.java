package io.izzel.arclight.common.bridge.core.world.food;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;

public interface FoodDataBridge {

    default int bridge$getSaturatedRegenRate() {
        return 0;
    }

    default void bridge$setSaturatedRegenRate(int saturatedRegenRate) {

    }

    default int bridge$getUnsaturatedRegenRate() {
        return 0;
    }

    default void bridge$setUnsaturatedRegenRate(int unsaturatedRegenRate) {

    }

    default int bridge$getStarvationRate() {
        return 0;
    }

    default void bridge$setStarvationRate(int starvationRate) {

    }

    default void eat(FoodProperties foodproperties, ItemStack itemstack, ServerPlayer serverplayer) {

    }


    default void bridge$setEntityHuman(Player playerEntity) {

    }

    default Player bridge$getEntityHuman() {
        return null;
    }

    default void bridge$pushEatStack(ItemStack stack) {

    }
}
