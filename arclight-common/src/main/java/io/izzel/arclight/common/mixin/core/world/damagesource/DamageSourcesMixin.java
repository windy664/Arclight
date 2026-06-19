package io.izzel.arclight.common.mixin.core.world.damagesource;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import io.izzel.arclight.common.bridge.core.world.damagesource.DamageSourcesBridge;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageSources;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(DamageSources.class)
public abstract class DamageSourcesMixin implements DamageSourcesBridge {

    @Shadow
    @Final
    private Registry<DamageType> damageTypes;

    @Shadow
    protected abstract DamageSource source(ResourceKey<DamageType> key, @org.jspecify.annotations.Nullable Entity directEntity, @org.jspecify.annotations.Nullable Entity causingEntity);

    // CraftBukkit start
    private DamageSource melting;
    private DamageSource poison;

    // CraftBukkit start
    @Override
    public DamageSource melting() {
        return this.melting;
    }

    @Override
    public DamageSource poison() {
        return this.poison;
    }
    // CraftBukkit end

    @WrapMethod(method = "explosion(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/entity/Entity;)Lnet/minecraft/world/damagesource/DamageSource;")
    private DamageSource arclight$wrapExplosion(Entity entity, Entity cause, Operation<DamageSource> original) {
        return this.explosion(entity, cause, cause != null && entity != null ? DamageTypes.PLAYER_EXPLOSION : DamageTypes.EXPLOSION);
    }

    @Override
    public DamageSource explosion(@Nullable Entity entity, @Nullable Entity entity1, ResourceKey<DamageType> resourceKey) {
        return this.source(resourceKey, entity, entity1);
        // CraftBukkit end
    }

    @WrapMethod(method = "badRespawnPointExplosion")
    private DamageSource arlight$wrapBadRespawnPointExplosion(Vec3 boomPos, Operation<DamageSource> original) {
        return original.call(boomPos).directBlockState(null);
    }

    @Override
    public DamageSource badRespawnPointExplosion(Vec3 vec3, org.bukkit.block.BlockState blockState) {
        return new DamageSource(this.damageTypes.getOrThrow(DamageTypes.BAD_RESPAWN_POINT), vec3).directBlockState(blockState);
        // CraftBukkit end
    }
}
