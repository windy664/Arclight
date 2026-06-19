package io.izzel.arclight.common.mixin.core.world.level.block;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.redstone.Orientation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FenceGateBlock.class)
public class FenceGateBlockMixin {

    @Inject(method = "neighborChanged", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;getValue(Lnet/minecraft/world/level/block/state/properties/Property;)Ljava/lang/Comparable;", ordinal = 0))
    private void arclight$callBlockRedstoneEvent(BlockState state, Level level, BlockPos pos, Block block, Orientation orientation, boolean movedByPiston, CallbackInfo ci, @Local(ordinal = 1) boolean hasPower) {
        // CraftBukkit start
        boolean oldPowered = state.getValue(FenceGateBlock.POWERED);
        if (oldPowered != hasPower) {
            int newPower = hasPower ? 15 : 0;
            int oldPower = oldPowered ? 15 : 0;
            org.bukkit.block.Block bukkitBlock = org.bukkit.craftbukkit.block.CraftBlock.at(level, pos);
            org.bukkit.event.block.BlockRedstoneEvent eventRedstone = new org.bukkit.event.block.BlockRedstoneEvent(bukkitBlock, oldPower, newPower);
            level.getCraftServer().getPluginManager().callEvent(eventRedstone);
            hasPower = eventRedstone.getNewCurrent() > 0;
        }
        // CraftBukkit end
    }
}
