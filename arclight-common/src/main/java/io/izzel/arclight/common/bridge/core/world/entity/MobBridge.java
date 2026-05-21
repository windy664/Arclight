package io.izzel.arclight.common.bridge.core.world.entity;

import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.EntityTransformEvent;
import org.jetbrains.annotations.Nullable;

public interface MobBridge extends LivingEntityBridge {

    default void bridge$pushGoalTargetReason(EntityTargetEvent.TargetReason reason, boolean fireEvent) {

    }

    default void bridge$pushTransformReason(EntityTransformEvent.TransformReason transformReason) {

    }

    default boolean bridge$setGoalTarget(LivingEntity livingEntity, EntityTargetEvent.TargetReason reason, boolean fireEvent) {
        return false;
    }

    default boolean bridge$lastGoalTargetResult() {
        return false;
    }

    default boolean bridge$isPersistenceRequired() {
        return false;
    }

    default void bridge$setPersistenceRequired(boolean value) {

    }

    default void bridge$setAware(boolean aware) {

    }

    default void bridge$captureItemDrop(ItemEntity itemEntity) {

    }

    default AgeableMob bridge$forge$onBabyEntitySpawn(Mob partner, @Nullable AgeableMob proposedChild) {
        return proposedChild;
    }

    default boolean bridge$common$animalTameEvent(Player player) {
        return false;
    }
}
