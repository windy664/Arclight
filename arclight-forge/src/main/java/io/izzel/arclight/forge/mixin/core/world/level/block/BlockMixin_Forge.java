package io.izzel.arclight.forge.mixin.core.world.level.block;

import io.izzel.arclight.common.bridge.core.world.level.block.BlockBridge;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.extensions.IForgeBlock;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Block.class)
public abstract class BlockMixin_Forge implements BlockBridge, IForgeBlock {

    @Override
    public boolean bridge$forge$onCropsGrowPre(Level level, BlockPos pos, BlockState state, boolean def) {
        return ForgeHooks.onCropsGrowPre(level, pos, state, def);
    }

    @Override
    public void bridge$forge$onCropsGrowPost(Level level, BlockPos pos, BlockState state) {
        ForgeHooks.onCropsGrowPost(level, pos, state);
    }

    @Override
    public void bridge$forge$onCaughtFire(BlockState state, Level level, BlockPos pos, @Nullable Direction direction, @Nullable LivingEntity igniter) {
        this.onCaughtFire(state, level, pos, direction, igniter);
    }

}
