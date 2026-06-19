package io.izzel.arclight.common.mixin.core.world.level.block;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DecoratedPotBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DecoratedPotBlock.class)
public abstract class DecoratedPotBlockMixin {

    @Shadow
    protected abstract FluidState getFluidState(BlockState state);

    @Inject(method = "onProjectileHit", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z"), cancellable = true)
    private void arclight$callEntityChangeBlockEvent(Level level, BlockState state, BlockHitResult blockHit, Projectile projectile, CallbackInfo ci, @Local BlockPos pos) {
        // CraftBukkit start - call EntityChangeBlockEvent
        if (!org.bukkit.craftbukkit.event.CraftEventFactory.callEntityChangeBlockEvent(projectile, pos, this.getFluidState(state).createLegacyBlock())) {
            ci.cancel();
            return;
        }
        // CraftBukkit end
    }
}
