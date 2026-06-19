package io.izzel.arclight.common.mixin.core.world.level.block;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.VegetationBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(VegetationBlock.class)
public abstract class VegetationBlockMixin extends Block {

    public VegetationBlockMixin(Properties properties) {
        super(properties);
    }

    @ModifyReturnValue(method = "updateShape", at = @At("RETURN"))
    private BlockState arclight$callBlockPhysicsEvent(BlockState original,
                                                      @Local(argsOnly = true, ordinal = 0) BlockState state,
                                                      @Local(argsOnly = true) LevelReader level,
                                                      @Local(argsOnly = true) ScheduledTickAccess ticks,
                                                      @Local(ordinal = 0, argsOnly = true) BlockPos pos,
                                                      @Local(argsOnly = true) Direction directionToNeighbour,
                                                      @Local(ordinal = 1, argsOnly = true) BlockPos neighbourPos,
                                                      @Local(ordinal = 1, argsOnly = true) BlockState neighbourState,
                                                      @Local(argsOnly = true) RandomSource random) {
        // CraftBukkit start
        if (!state.canSurvive(level, pos)) {
            // Suppress during worldgen
            if (!(level instanceof Level actualLevel) || !CraftEventFactory.callBlockPhysicsEvent(actualLevel, pos).isCancelled()) {
                return Blocks.AIR.defaultBlockState();
            }
        }
        return super.updateShape(state, level, ticks, pos, directionToNeighbour, neighbourPos, neighbourState, random);
        // CraftBukkit end
    }
}
