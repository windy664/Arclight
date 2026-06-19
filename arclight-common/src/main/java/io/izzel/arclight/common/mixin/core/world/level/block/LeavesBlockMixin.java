package io.izzel.arclight.common.mixin.core.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.bukkit.event.block.LeavesDecayEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LeavesBlock.class)
public class LeavesBlockMixin {

    @Inject(method = "randomTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/LeavesBlock;dropResources(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;)V"), cancellable = true)
    private void arclight$callLeavesDecayEvent(BlockState state, ServerLevel level, BlockPos pos, RandomSource random, CallbackInfo ci) {
        // CraftBukkit start
        LeavesDecayEvent event = new LeavesDecayEvent(level.getWorld().getBlockAt(pos.getX(), pos.getY(), pos.getZ()));
        level.getCraftServer().getPluginManager().callEvent(event);

        if (event.isCancelled() || level.getBlockState(pos).getBlock() != ((LeavesBlock) (Object) this)) {
            ci.cancel();
            return;
        }
        // CraftBukkit end
    }
}
