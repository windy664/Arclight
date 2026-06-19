package io.izzel.arclight.common.mixin.core.world.level.block;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DragonEggBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.bukkit.event.block.BlockFromToEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DragonEggBlock.class)
public class DragonEggBlockMixin {

    @Inject(method = "teleport", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;isClientSide()Z"), cancellable = true)
    private void arclight$callBlockFromToEvent(BlockState state, Level level, BlockPos pos, CallbackInfo ci, @Local(ordinal = 1) BlockPos testPos) {
        // CraftBukkit start
        org.bukkit.block.Block from = level.getWorld().getBlockAt(pos.getX(), pos.getY(), pos.getZ());
        org.bukkit.block.Block to = level.getWorld().getBlockAt(testPos.getX(), testPos.getY(), testPos.getZ());
        BlockFromToEvent event = new BlockFromToEvent(from, to);
        org.bukkit.Bukkit.getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            ci.cancel();
            return;
        }

        testPos = new BlockPos(event.getToBlock().getX(), event.getToBlock().getY(), event.getToBlock().getZ());
        // CraftBukkit end
    }
}
