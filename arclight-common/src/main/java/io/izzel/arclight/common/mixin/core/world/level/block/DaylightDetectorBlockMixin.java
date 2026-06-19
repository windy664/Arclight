package io.izzel.arclight.common.mixin.core.world.level.block;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DaylightDetectorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DaylightDetectorBlock.class)
public class DaylightDetectorBlockMixin {

    @Shadow
    @Final
    public static IntegerProperty POWER;

    @Inject(method = "updateSignalStrength", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z"))
    private static void arclight$callRedstoneChange(BlockState state, Level level, BlockPos pos, CallbackInfo ci, @Local(ordinal = 0) int target) {
        target = org.bukkit.craftbukkit.event.CraftEventFactory.callRedstoneChange(level, pos, ((Integer) state.getValue(POWER)), target).getNewCurrent(); // CraftBukkit - Call BlockRedstoneEvent
    }
}
