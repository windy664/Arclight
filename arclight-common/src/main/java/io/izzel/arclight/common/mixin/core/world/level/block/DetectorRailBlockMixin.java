package io.izzel.arclight.common.mixin.core.world.level.block;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DetectorRailBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DetectorRailBlock.class)
public class DetectorRailBlockMixin {

    @Inject(method = "checkPressed", at = @At(value = "INVOKE", target = "Ljava/util/List;isEmpty()Z"))
    private void arclight$callBlockRedstoneEvent(Level level, BlockPos pos, BlockState state, CallbackInfo ci, @Local(ordinal = 0) boolean wasPressed, @Local(ordinal = 1) boolean shouldBePressed) {
        // CraftBukkit start
        if (wasPressed != shouldBePressed) {
            org.bukkit.block.Block block = level.getWorld().getBlockAt(pos.getX(), pos.getY(), pos.getZ());

            BlockRedstoneEvent eventRedstone = new BlockRedstoneEvent(block, wasPressed ? 15 : 0, shouldBePressed ? 15 : 0);
            level.getCraftServer().getPluginManager().callEvent(eventRedstone);

            shouldBePressed = eventRedstone.getNewCurrent() > 0;
        }
        // CraftBukkit end

    }
}
