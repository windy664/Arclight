package io.izzel.arclight.common.mixin.core.world.level.block;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ConcretePowderBlock;
import net.minecraft.world.level.block.FallingBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.bukkit.craftbukkit.block.CraftBlockState;
import org.bukkit.craftbukkit.block.CraftBlockStates;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.bukkit.event.block.BlockFormEvent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ConcretePowderBlock.class)
public abstract class ConcretePowderBlockMixin extends FallingBlock {

    // @formatter: off
    @Shadow private static boolean shouldSolidify(BlockGetter level, BlockPos pos, BlockState replacedBlock) {return false;}
    @Shadow @Final private Block concrete;
    // @formatter: on

    public ConcretePowderBlockMixin(Properties properties) {
        super(properties);
    }

    @Redirect(method = "onLand", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z"))
    private boolean arclight$handleBlockFormEvent(Level instance, BlockPos pos, BlockState blockState, int updateFlags) {
        return CraftEventFactory.handleBlockFormEvent(instance, pos, blockState, updateFlags); // CraftBukkit
    }

    @ModifyReturnValue(method = "getStateForPlacement", at = @At("RETURN"))
    private BlockState arclight$callBlockFormEvent(BlockState original,
                                                   @Local(argsOnly = true) BlockPlaceContext context,
                                                   @Local BlockGetter level,
                                                   @Local BlockPos pos,
                                                   @Local BlockState replacedBlock) {
        // CraftBukkit start
        if (!shouldSolidify(level, pos, replacedBlock)) {
            return super.getStateForPlacement(context);
        }

        // TODO: An event factory call for methods like this
        CraftBlockState blockState = CraftBlockStates.getBlockState(context.getLevel(), pos);
        blockState.setData(this.concrete.defaultBlockState());

        BlockFormEvent event = new BlockFormEvent(blockState.getBlock(), blockState);
        context.getLevel().getServer().bridge$getServer().getPluginManager().callEvent(event);

        if (!event.isCancelled()) {
            return blockState.getHandle();
        }

        return super.getStateForPlacement(context);
        // CraftBukkit end
    }
}
