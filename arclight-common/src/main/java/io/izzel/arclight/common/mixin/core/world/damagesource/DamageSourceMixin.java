package io.izzel.arclight.common.mixin.core.world.damagesource;

import io.izzel.arclight.common.bridge.core.world.damagesource.DamageSourceBridge;
import net.minecraft.core.Holder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(DamageSource.class)
public class DamageSourceMixin implements DamageSourceBridge {

    // @formmater: off
    @Shadow @Final private @org.jspecify.annotations.Nullable Entity directEntity;
    @Shadow @Final private @org.jspecify.annotations.Nullable Entity causingEntity;
    @Shadow @Final private Holder<DamageType> type;
    @Shadow @Final private @org.jspecify.annotations.Nullable Vec3 damageSourcePosition;
    // @formmater: on

    // CraftBukkit start
    private org.bukkit.block.@Nullable Block directBlock; // The block that caused the damage. damageSourcePosition is not used for all block damages
    private org.bukkit.block.@Nullable BlockState directBlockState; // The block state of the block relevant to this damage source
    private boolean sweep = false;
    private boolean melting = false;
    private boolean poison = false;
    private Entity customEntityDamager = null; // This field is a helper for when direct entity damage is not set by vanilla
    private Entity customCausingEntityDamager = null; // This field is a helper for when causing entity damage is not set by vanilla

    @Override
    public DamageSource sweep() {
        this.sweep = true;
        return ((DamageSource) (Object) this);
    }

    @Override
    public boolean isSweep() {
        return this.sweep;
    }

    @Override
    public DamageSource melting() {
        this.melting = true;
        return ((DamageSource) (Object) this);
    }

    @Override
    public boolean isMelting() {
        return this.melting;
    }

    @Override
    public DamageSource poison() {
        this.poison = true;
        return ((DamageSource) (Object) this);
    }

    @Override
    public boolean isPoison() {
        return this.poison;
    }

    @Override
    public Entity getDamager() {
        return (this.customEntityDamager != null) ? this.customEntityDamager : this.directEntity;
    }

    @Override
    public Entity getCausingDamager() {
        return (this.customCausingEntityDamager != null) ? this.customCausingEntityDamager : this.causingEntity;
    }

    @Override
    public DamageSource customEntityDamager(Entity entity) {
        // This method is not intended for change the causing entity if is already set
        // also is only necessary if the entity passed is not the direct entity or different from the current causingEntity
        if (this.customEntityDamager != null || this.directEntity == entity || this.causingEntity == entity) {
            return ((DamageSource) (Object) this);
        }
        DamageSource damageSource = this.cloneInstance();
        damageSource.bridge$setCustomCausingEntityDamager(customEntityDamager);
        return damageSource;
    }

    @Override
    public DamageSource customCausingEntityDamager(Entity entity) {
        // This method is not intended for change the causing entity if is already set
        // also is only necessary if the entity passed is not the direct entity or different from the current causingEntity
        if (this.customCausingEntityDamager != null || this.directEntity == entity || this.causingEntity == entity) {
            return ((DamageSource) (Object) this);
        }
        DamageSource damageSource = this.cloneInstance();
        damageSource.bridge$setCustomCausingEntityDamager(entity);
        return damageSource;
    }

    @Override
    public org.bukkit.block.Block getDirectBlock() {
        return this.directBlock;
    }

    @Override
    public DamageSource directBlock(net.minecraft.world.level.Level level, net.minecraft.core.BlockPos blockPosition) {
        if (blockPosition == null || level == null) {
            return ((DamageSource) (Object) this);
        }
        return directBlock(org.bukkit.craftbukkit.block.CraftBlock.at(level, blockPosition));
    }

    @Override
    public DamageSource directBlock(org.bukkit.block.Block block) {
        if (block == null) {
            return ((DamageSource) (Object) this);
        }
        // Cloning the instance lets us return unique instances of DamageSource without affecting constants defined in DamageSources
        DamageSource damageSource = this.cloneInstance();
        damageSource.bridge$setDirectBlock(block);
        return damageSource;
    }

    @Override
    public org.bukkit.block.BlockState getDirectBlockState() {
        return this.directBlockState;
    }

    public DamageSource directBlockState(org.bukkit.block.BlockState blockState) {
        if (blockState == null) {
            return ((DamageSource) (Object) this);
        }
        // Cloning the instance lets us return unique instances of DamageSource without affecting constants defined in DamageSources
        DamageSource damageSource = this.cloneInstance();
        damageSource.bridge$setDirectBlockState(blockState);
        return damageSource;
    }

    private DamageSource cloneInstance() {
        DamageSource damageSource = new DamageSource(this.type, this.directEntity, this.causingEntity, this.damageSourcePosition);
        damageSource.bridge$setDirectBlock(this.getDirectBlock());
        damageSource.bridge$setDirectBlockState(this.getDirectBlockState());
        damageSource.bridge$setSweep(this.isSweep());
        damageSource.bridge$setPoison(this.isPoison());
        damageSource.bridge$setMelting(this.isMelting());
        return damageSource;
    }
    // CraftBukkit end


    @Override
    public void bridge$setDirectBlock(Block block) {
        this.directBlock = block;
    }

    @Override
    public void bridge$setDirectBlockState(BlockState blockState) {
        this.directBlockState = blockState;
    }

    @Override
    public void bridge$setCustomCausingEntity(Entity customEntityDamager) {
        this.customEntityDamager = customEntityDamager;
    }

    @Override
    public void bridge$setCustomCausingEntityDamager(Entity entity) {
        this.customCausingEntityDamager = entity;
    }

    @Override
    public void bridge$setSweep(boolean sweep) {
        this.sweep = sweep;
    }

    @Override
    public void bridge$setMelting(boolean melting) {
        this.melting = melting;
    }

    @Override
    public void bridge$setPoison(boolean poison) {
        this.poison = poison;
    }
}
