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

    default DamageSource bridge$setDirectBlock(org.bukkit.block.Block block) {
        return null;
    }


    default DamageSource bridge$setDirectBlockState(org.bukkit.block.BlockState blockState) {
        return null;
    }

    default DamageSource bridge$setCustomCausingEntity(Entity customEntityDamager) {
        return null;
    }

    default DamageSource bridge$setCustomCausingEntityDamager(Entity entity) {
        return null;
    }
}
