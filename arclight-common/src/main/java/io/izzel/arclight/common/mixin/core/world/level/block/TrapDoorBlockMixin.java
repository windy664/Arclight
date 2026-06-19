package io.izzel.arclight.common.mixin.core.world.level.block;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.redstone.Orientation;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TrapDoorBlock.class)
public class TrapDoorBlockMixin {

    @Shadow
    @Final
    public static BooleanProperty OPEN;

    @Inject(method = "neighborChanged", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;getValue(Lnet/minecraft/world/level/block/state/properties/Property;)Ljava/lang/Comparable;", ordinal = 0))
    private void arclight$callBlockRedstoneEvent(BlockState state, Level level, BlockPos pos, Block block, Orientation orientation, boolean movedByPiston, CallbackInfo ci, @Local(ordinal = 1) boolean signal) {
        // CraftBukkit start
        org.bukkit.World bworld = level.getWorld();
        org.bukkit.block.Block bblock = bworld.getBlockAt(pos.getX(), pos.getY(), pos.getZ());

        int power = bblock.getBlockPower();
        int oldPower = (Boolean) state.getValue(OPEN) ? 15 : 0;

        if (oldPower == 0 ^ power == 0 || block.defaultBlockState().isSignalSource()) {
            BlockRedstoneEvent eventRedstone = new BlockRedstoneEvent(bblock, oldPower, power);
            level.getCraftServer().getPluginManager().callEvent(eventRedstone);
            signal = eventRedstone.getNewCurrent() > 0;
        }
        // CraftBukkit end
    }
}
