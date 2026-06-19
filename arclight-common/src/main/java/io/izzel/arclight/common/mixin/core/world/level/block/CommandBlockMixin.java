package io.izzel.arclight.common.mixin.core.world.level.block;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.CommandBlock;
import net.minecraft.world.level.block.entity.CommandBlockEntity;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CommandBlock.class)
public class CommandBlockMixin {

    @Definition(id = "isPowered", local = @Local(type = boolean.class, ordinal = 0, argsOnly = true))
    @Definition(id = "wasPowered", local = @Local(type = boolean.class, ordinal = 1))
    @Expression("isPowered != wasPowered")
    @Inject(method = "setPoweredAndUpdate", at = @At("MIXINEXTRAS:EXPRESSION"))
    private void arclight$callBlockRedstoneEvent(Level level, BlockPos pos, CommandBlockEntity commandBlock, boolean isPowered, CallbackInfo ci, @Local(ordinal = 1) boolean wasPowered) {
        // CraftBukkit start
        org.bukkit.block.Block bukkitBlock = level.getWorld().getBlockAt(pos.getX(), pos.getY(), pos.getZ());
        int old = wasPowered ? 15 : 0;
        int current = isPowered ? 15 : 0;

        BlockRedstoneEvent eventRedstone = new BlockRedstoneEvent(bukkitBlock, old, current);
        level.getCraftServer().getPluginManager().callEvent(eventRedstone);
        isPowered = eventRedstone.getNewCurrent() > 0;
        // CraftBukkit end
    }
}
