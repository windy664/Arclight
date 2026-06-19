package io.izzel.arclight.common.mixin.core.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LeverBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LeverBlock.class)
public class LeverBlockMixin {

    @Inject(method = "useWithoutItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/LeverBlock;pull(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/entity/player/Player;)V"), cancellable = true)
    private void arclight$callBlockRedstoneEvent(BlockState stateBefore, Level level, BlockPos pos, Player player, BlockHitResult hitResult, CallbackInfoReturnable<InteractionResult> cir) {
        // CraftBukkit start - Interact Lever
        boolean powered = stateBefore.getValue(LeverBlock.POWERED); // Old powered state
        org.bukkit.block.Block block = level.getWorld().getBlockAt(pos.getX(), pos.getY(), pos.getZ());
        int old = (powered) ? 15 : 0;
        int current = (!powered) ? 15 : 0;

        BlockRedstoneEvent eventRedstone = new BlockRedstoneEvent(block, old, current);
        level.getCraftServer().getPluginManager().callEvent(eventRedstone);

        if ((eventRedstone.getNewCurrent() > 0) != (!powered)) {
            cir.setReturnValue(InteractionResult.SUCCESS);
        }
        // CraftBukkit end
    }
}
