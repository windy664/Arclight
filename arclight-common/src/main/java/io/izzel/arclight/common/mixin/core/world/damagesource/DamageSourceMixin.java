package io.izzel.arclight.common.mixin.core.world.damagesource;

import io.izzel.arclight.common.bridge.core.world.damagesource.DamageSourceBridge;
import net.minecraft.core.Holder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import javax.annotation.Nullable;

@Mixin(DamageSource.class)
public abstract class DamageSourceMixin implements DamageSourceBridge {

    // @formatter:off
    @Shadow @Nullable public Entity getEntity() { return null; }
    @Shadow @Final @org.jetbrains.annotations.Nullable private Entity causingEntity;
    @Shadow @Final private Holder<DamageType> type;
    @Shadow @Final @org.jetbrains.annotations.Nullable private Entity directEntity;
    @Shadow @Final @org.jetbrains.annotations.Nullable private Vec3 damageSourcePosition;
    // @formatter:on

    private org.bukkit.block.Block directBlock;
    private org.bukkit.block.BlockState directBlockState;
    private boolean withSweep;
    private boolean melting;
    private boolean poison;
    private Entity customEntityDamager = null;
    private Entity customCausingEntityDamager = null;

    public boolean isSweep() {
        return withSweep;
    }

    public DamageSource sweep() {
        withSweep = true;
        return (DamageSource) (Object) this;
    }

    public boolean isMelting() {
        return melting;
    }

    public DamageSource melting() {
        this.melting = true;
        return (DamageSource) (Object) this;
    }

    public boolean isPoison() {
        return poison;
    }

    public DamageSource poison() {
        this.poison = true;
        return (DamageSource) (Object) this;
    }

    public Entity getDamager() {
        // Arclight: Blame: used causingEntity instead of directEntity and bump into problems :(
        return this.customEntityDamager == null ? this.directEntity : this.customEntityDamager;
    }

    public DamageSource customEntityDamager(Entity entity) {
        // This method is not intended for change the causing entity if is already set
        // also is only necessary if the entity passed is not the direct entity or different from the current causingEntity
        if (this.customEntityDamager != null || this.directEntity == entity || this.causingEntity == entity) {
            return (DamageSource) (Object) this;
        }
        var src = cloneInstance();
        return ((DamageSourceBridge) src).bridge$setCustomCausingEntity(entity);
    }

    public Entity getCausingDamager() {
        return (this.customCausingEntityDamager != null) ? this.customCausingEntityDamager : this.causingEntity;
    }

    @Override
    public DamageSource bridge$setCustomCausingEntity(Entity entity) {
        this.customEntityDamager = entity;
        return (DamageSource) (Object) this;
    }

    public DamageSource customCausingEntityDamager(Entity entity) {
        // This method is not intended for change the causing entity if is already set
        // also is only necessary if the entity passed is not the direct entity or different from the current causingEntity
        if (this.customCausingEntityDamager != null || this.directEntity == entity || this.causingEntity == entity) {
            return (DamageSource) (Object) this;
        }
        var src = cloneInstance();
        return ((DamageSourceBridge) src).bridge$setCustomCausingEntityDamager(entity);
    }

    @Override
    public DamageSource bridge$setCustomCausingEntityDamager(Entity entity) {
        this.customCausingEntityDamager = entity;
        return (DamageSource) (Object) this;
    }

    public Block getDirectBlock() {
        return this.directBlock;
    }

    @Override
    public DamageSource bridge$setDirectBlock(Block block) {
        this.directBlock = block;
        return (DamageSource) (Object) this;
    }

    public org.bukkit.block.BlockState getDirectBlockState() {
        return this.directBlockState;
    }

    public DamageSource directBlockState(org.bukkit.block.BlockState blockState) {
        if (blockState == null) {
            return (DamageSource) (Object) this;
        }
        // Cloning the instance lets us return unique instances of DamageSource without affecting constants defined in DamageSources
        DamageSource damageSource = this.cloneInstance();
        ((DamageSourceBridge) damageSource).bridge$setDirectBlockState(blockState);
        return damageSource;
    }

    @Override
    public DamageSource bridge$setDirectBlockState(BlockState block) {
        this.directBlockState = block;
        return (DamageSource) (Object) this;
    }

    private DamageSource cloneInstance() {
        var damageSource = new DamageSource(this.type, this.directEntity, this.causingEntity, this.damageSourcePosition);
        var br = (DamageSourceBridge) damageSource;
        br.bridge$setDirectBlock(this.getDirectBlock());
        br.bridge$setDirectBlockState(this.getDirectBlockState());
        br.bridge$setCustomCausingEntity(this.customEntityDamager);
        if (this.withSweep) {
            br.sweep();
        }
        if (this.poison) {
            br.poison();
        }
        if (this.melting) {
            br.melting();
        }
        return damageSource;
    }
}
