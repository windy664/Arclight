package io.izzel.arclight.common.mixin.core.world.level.block;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.PoweredRailBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PoweredRailBlock.class)
public class PoweredRailBlockMixin {

    @Inject(method = "updateState", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z"), cancellable = true)
    private void arclight$callRedstoneChangeEvent(BlockState state, Level level, BlockPos pos, Block block, CallbackInfo ci, @Local(ordinal = 0) boolean isPowered) {
        // CraftBukkit start
        int power = isPowered ? 15 : 0;
        int newPower = CraftEventFactory.callRedstoneChange(level, pos, power, 15 - power).getNewCurrent();
        if (newPower == power) {
            ci.cancel();
            return;
        }
        // CraftBukkit end
    }
}
