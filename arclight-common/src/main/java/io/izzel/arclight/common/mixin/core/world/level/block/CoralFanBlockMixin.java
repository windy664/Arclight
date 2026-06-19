package io.izzel.arclight.common.mixin.core.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CoralFanBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CoralFanBlock.class)
public class CoralFanBlockMixin {

    @Shadow
    @Final
    private Block deadBlock;

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z"), cancellable = true)
    private void arclight$callBlockFadeEvent(BlockState state, ServerLevel level, BlockPos pos, RandomSource random, CallbackInfo ci) {
        // CraftBukkit start
        if (CraftEventFactory.callBlockFadeEvent(level, pos, this.deadBlock.defaultBlockState().setValue(CoralFanBlock.WATERLOGGED, false)).isCancelled()) {
            ci.cancel();
            return;
        }
        // CraftBukkit end
    }
}
