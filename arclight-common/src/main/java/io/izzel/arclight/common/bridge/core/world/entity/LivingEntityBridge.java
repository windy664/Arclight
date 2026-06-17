package io.izzel.arclight.common.bridge.core.world.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.bukkit.craftbukkit.entity.CraftLivingEntity;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityKnockbackEvent;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;

import javax.annotation.Nullable;
import java.util.Optional;

public interface LivingEntityBridge extends EntityBridge {

    default void bridge$setSlot(EquipmentSlot slotIn, ItemStack stack, boolean silent) {

    }

    default void bridge$playEquipSound(EquipmentSlot slot, ItemStack oldItem, ItemStack newItem, boolean silent) {

    }

    default boolean bridge$canPickUpLoot() {
        return false;
    }

    default int bridge$getExpReward(Entity entity) {
        return 0;
    }

    default void bridge$setExpToDrop(int amount) {

    }

    default int bridge$getExpToDrop() {
        return 0;
    }

    default void bridge$pushHealReason(EntityRegainHealthEvent.RegainReason regainReason) {

    }

    default void bridge$heal(float healAmount, EntityRegainHealthEvent.RegainReason regainReason) {

    }

    default void bridge$pushEffectCause(EntityPotionEffectEvent.Cause cause) {

    }

    default boolean bridge$addEffect(MobEffectInstance effect, EntityPotionEffectEvent.Cause cause) {
        return false;
    }

    default boolean bridge$removeEffect(Holder<MobEffect> effect, EntityPotionEffectEvent.Cause cause) {
        return false;
    }

    default boolean bridge$removeAllEffects(EntityPotionEffectEvent.Cause cause) {
        return false;
    }

    default Optional<EntityPotionEffectEvent.Cause> bridge$getEffectCause() {
        return null;
    }

    default void bridge$pushKnockbackCause(Entity attacker, EntityKnockbackEvent.KnockbackCause cause) {

    }

    @Override
    default CraftLivingEntity getBukkitEntity() {
        return null;
    }

    default int bridge$forge$getExperienceDrop(LivingEntity entity, Player attackingPlayer, int originalExperience) {
        return originalExperience;
    }

    default boolean bridge$forge$onLivingUseTotem(LivingEntity entity, DamageSource damageSource, ItemStack totem, InteractionHand hand) {
        return true;
    }

    enum LivingTargetType {
        BEHAVIOR_TARGET,
        MOB_TARGET
    }

    default void bridge$forge$onLivingConvert(LivingEntity entity, LivingEntity outcome) {}

    default boolean bridge$forge$canEntityDestroy(Level level, BlockPos pos, LivingEntity entity) {
        return true;
    }

    @Nullable
    default EntityDamageEvent arclight$fireEntityDamageEvent(DamageSource source, float original) {
        return null;
    }
}
