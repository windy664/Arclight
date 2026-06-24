package io.izzel.arclight.common.mixin.core.world.level;

import io.izzel.arclight.common.bridge.core.world.level.BlockGetterBridge;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(BlockGetter.class)
public interface BlockGetterMixin extends BlockGetterBridge {

    @Shadow
    BlockState getBlockState(BlockPos pos);

    @Shadow
    FluidState getFluidState(BlockPos pos);

    // CraftBukkit start - moved block handling into separate method for use by Block#rayTrace
    @Override
    default BlockHitResult clip(final ClipContext context, BlockPos pos) {
        BlockState blockState = this.getBlockState(pos);
        FluidState fluidState = this.getFluidState(pos);
        Vec3 from = context.getFrom();
        Vec3 to = context.getTo();
        VoxelShape blockShape = context.getBlockShape(blockState, ((BlockGetter) (Object) this), pos);
        BlockHitResult blockResult = ((BlockGetter) (Object) this).clipWithInteractionOverride(from, to, pos, blockShape, blockState);
        VoxelShape fluidShape = context.getFluidShape(fluidState, ((BlockGetter) (Object) this), pos);
        BlockHitResult liquidResult = fluidShape.clip(from, to, pos);
        double blockDistanceSquared = blockResult == null ? Double.MAX_VALUE : context.getFrom().distanceToSqr(blockResult.getLocation());
        double liquidDistanceSquared = liquidResult == null ? Double.MAX_VALUE : context.getFrom().distanceToSqr(liquidResult.getLocation());
        return blockDistanceSquared <= liquidDistanceSquared ? blockResult : liquidResult;
    }
    // CraftBukkit end
}
