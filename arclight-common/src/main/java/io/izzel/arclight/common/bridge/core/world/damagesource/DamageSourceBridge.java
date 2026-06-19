package io.izzel.arclight.common.bridge.core.world.damagesource;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;

public interface DamageSourceBridge {

    default DamageSource sweep() {
        return null;
    }

    default boolean isSweep() {
        return false;
    }

    default DamageSource melting() {
        return null;
    }

    default boolean isMelting() {
        return false;
    }

    default DamageSource poison() {
        return null;
    }

    default boolean isPoison() {
        return false;
    }

    default Entity getDamager() {
        return null;
    }

    default Entity getCausingDamager() {
        return null;
    }

    default DamageSource customEntityDamager(Entity entity) {
        return null;
    }

    default DamageSource customCausingEntityDamager(Entity entity) {
        return null;
    }

    default org.bukkit.block.Block getDirectBlock() {
        return null;
    }

    default DamageSource directBlock(net.minecraft.world.level.Level level, net.minecraft.core.BlockPos blockPosition) {
        return null;
    }

    default DamageSource directBlock(org.bukkit.block.Block block) {
        return null;
    }

    default org.bukkit.block.BlockState getDirectBlockState() {
        return null;
    }

    default DamageSource directBlockState(org.bukkit.block.BlockState blockState) {
        return null;
    }

    default void bridge$setDirectBlock(org.bukkit.block.Block block) {
    }


    default void bridge$setDirectBlockState(org.bukkit.block.BlockState blockState) {
    }

    default void bridge$setCustomCausingEntity(Entity customEntityDamager) {
    }

    default void bridge$setCustomCausingEntityDamager(Entity entity) {
    }

    default void bridge$setSweep(boolean sweep) {
    }

    default void bridge$setMelting(boolean melting) {
    }

    default void bridge$setPoison(boolean poison) {
    }
}
