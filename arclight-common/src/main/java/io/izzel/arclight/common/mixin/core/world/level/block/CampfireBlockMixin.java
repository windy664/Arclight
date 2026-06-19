package io.izzel.arclight.common.mixin.core.world.level.block;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageSources;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CampfireBlock.class)
public class CampfireBlockMixin {

    @WrapOperation(method = "entityInside", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/damagesource/DamageSources;campfire()Lnet/minecraft/world/damagesource/DamageSource;"))
    private DamageSource arclight$putDmgSource(DamageSources instance, Operation<DamageSource> original, @Local(argsOnly = true) Level level, @Local(argsOnly = true) BlockPos pos) {
        return original.call(instance).directBlock(level, pos);
    }

    @Inject(method = "onProjectileHit", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z"), cancellable = true)
    private void arclight$callBlockIgniteEvent(Level level, BlockState state, BlockHitResult blockHit, Projectile projectile, CallbackInfo ci, @Local BlockPos pos) {
        // CraftBukkit start
        if (CraftEventFactory.callBlockIgniteEvent(level, pos, projectile).isCancelled()) {
            ci.cancel();
            return;
        }
        // CraftBukkit end
    }
}
