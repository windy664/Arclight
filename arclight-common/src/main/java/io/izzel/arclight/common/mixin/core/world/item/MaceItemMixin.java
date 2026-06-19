package io.izzel.arclight.common.mixin.core.world.item;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.MaceItem;
import net.minecraft.world.phys.Vec3;
import org.bukkit.craftbukkit.util.CraftVector;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(MaceItem.class)
public class MaceItemMixin {

    @WrapOperation(method = "lambda$knockback$0", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;push(DDD)V"))
    private static void arclight$callEntityKnockbackEvent(LivingEntity instance,
                                                          double x, double y, double z,
                                                          Operation<Void> original,
                                                          @Local(ordinal = 1) Vec3 knockbackVector,
                                                          @Local(ordinal = 0, argsOnly = true) Entity attacker,
                                                          @Local double knockbackPower) {
        // entityliving.push(vec3d1.x, 0.7F, vec3d1.z); // CraftBukkit - moved below
        // CraftBukkit start - EntityKnockbackEvent
        Vec3 vec3Push = new Vec3(knockbackVector.x, 0.7F, knockbackVector.z);
        Vec3 result = attacker.getDeltaMovement().add(vec3Push);
        org.bukkit.event.entity.EntityKnockbackEvent event = org.bukkit.craftbukkit.event.CraftEventFactory.callEntityKnockbackEvent((org.bukkit.craftbukkit.entity.CraftLivingEntity) instance.getBukkitEntity(), attacker, org.bukkit.event.entity.EntityKnockbackEvent.KnockbackCause.ENTITY_ATTACK, knockbackPower, result, vec3Push.x, vec3Push.y, vec3Push.z);
        if (!event.isCancelled()) {
            instance.push(CraftVector.toNMS(event.getFinalKnockback()));
        }
        // CraftBukkit end
    }
}
