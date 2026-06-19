package io.izzel.arclight.common.mixin.core.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.ObserverBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ObserverBlock.class)
public class ObserverBlockMixin {

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z", ordinal = 0), cancellable = true)
    private void arclight$callRedstoneChangeStart(BlockState state, ServerLevel level, BlockPos pos, RandomSource random, CallbackInfo ci) {
        // CraftBukkit start
        if (CraftEventFactory.callRedstoneChange(level, pos, 15, 0).getNewCurrent() != 0) {
            ci.cancel();
            return;
        }
        // CraftBukkit end
    }

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z", ordinal = 1), cancellable = true)
    private void arclight$callRedstoneChangeEnd(BlockState state, ServerLevel level, BlockPos pos, RandomSource random, CallbackInfo ci) {
        // CraftBukkit start
        if (CraftEventFactory.callRedstoneChange(level, pos, 0, 15).getNewCurrent() != 15) {
            ci.cancel();
            return;
        }
        // CraftBukkit end
    }
}
