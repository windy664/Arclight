package io.izzel.arclight.common.mixin.core.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.TripWireHookBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.bukkit.craftbukkit.block.CraftBlock;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TripWireHookBlock.class)
public class TripWireHookBlockMixin {

    @Inject(method = "calculateState", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/TripWireHookBlock;emitState(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;ZZZZ)V", ordinal = 1), cancellable = true)
    private static void arclight$callBlockRedstoneEvent(Level level, BlockPos pos, BlockState state, boolean isBeingDestroyed, boolean canUpdate, int wireSource, BlockState wireSourceState, CallbackInfo ci) {
        // CraftBukkit start
        BlockRedstoneEvent eventRedstone = new BlockRedstoneEvent(CraftBlock.at(level, pos), 15, 0);
        level.getCraftServer().getPluginManager().callEvent(eventRedstone);

        if (eventRedstone.getNewCurrent() > 0) {
            ci.cancel();
            return;
        }
        // CraftBukkit end
    }
}
