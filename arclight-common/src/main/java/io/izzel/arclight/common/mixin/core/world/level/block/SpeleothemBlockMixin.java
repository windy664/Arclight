package io.izzel.arclight.common.mixin.core.world.level.block;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SpeleothemBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.concurrent.atomic.AtomicReference;

@Mixin(SpeleothemBlock.class)
public class SpeleothemBlockMixin {

    @Unique
    private AtomicReference<BlockPos> arclight$pos = new AtomicReference<>();

    @Inject(method = "onProjectileHit", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;destroyBlock(Lnet/minecraft/core/BlockPos;Z)Z"), cancellable = true)
    private void arclight$callEntityChangeBlockEvent(Level level, BlockState state, BlockHitResult blockHit, Projectile projectile, CallbackInfo ci, @Local(ordinal = 0) BlockPos blockPos) {
        // CraftBukkit start
        if (!CraftEventFactory.callEntityChangeBlockEvent(projectile, blockPos, Blocks.AIR.defaultBlockState())) {
            ci.cancel();
            return;
        }
        // CraftBukkit end
    }

    @Inject(method = "grow", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/SpeleothemBlock;createSpeleothem(Lnet/minecraft/world/level/LevelAccessor;Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/Direction;Lnet/minecraft/world/level/block/state/properties/SpeleothemThickness;)V"))
    private void arclight$putPos0(ServerLevel level, BlockPos growFromPos, Direction growToDirection, CallbackInfo ci) {
        arclight$pos.set(growFromPos);
    }

    @Inject(method = "grow", at = @At("TAIL"))
    private void arclight$removePos(ServerLevel level, BlockPos growFromPos, Direction growToDirection, CallbackInfo ci) {
        arclight$pos.set(null);
    }

    @Inject(method = "createMergedTips", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/SpeleothemBlock;createSpeleothem(Lnet/minecraft/world/level/LevelAccessor;Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/Direction;Lnet/minecraft/world/level/block/state/properties/SpeleothemThickness;)V"))
    private void arclight$putPos1(BlockState tipState, LevelAccessor level, BlockPos tipPos, CallbackInfo ci) {
        arclight$pos.set(tipPos);
    }

    @Inject(method = "createMergedTips", at = @At("TAIL"))
    private void arclight$removePos1(BlockState tipState, LevelAccessor level, BlockPos tipPos, CallbackInfo ci) {
        arclight$pos.set(null);
    }


    @WrapOperation(method = "createSpeleothem", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/LevelAccessor;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z"))
    private boolean arclight$cancelSetBlock(LevelAccessor instance, BlockPos blockPos, BlockState blockState, int i, Operation<Boolean> original) {
        if (arclight$pos.get() != null) {
            return CraftEventFactory.handleBlockSpreadEvent(instance, arclight$pos.get(), blockPos, blockState, 3); // CraftBukkit
        }else {
            return original.call(instance, blockPos, blockState, i);
        }
    }
}
