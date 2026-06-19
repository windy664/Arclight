package io.izzel.arclight.common.mixin.core.world.level.block;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ScaffoldingBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ScaffoldingBlock.class)
public class ScaffoldingBlockMixin {

    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;getValue(Lnet/minecraft/world/level/block/state/properties/Property;)Ljava/lang/Comparable;", ordinal = 0))
    private Comparable<Integer> arclight$callBlockFadeEvent(BlockState state, Property<Integer> property, @Local(argsOnly = true) ServerLevel serverLevel, @Local(argsOnly = true) BlockPos pos) {
        Integer integer = state.getValue(property);
        if (integer == 7) {
            if (CraftEventFactory.callBlockFadeEvent(serverLevel, pos, Blocks.AIR.defaultBlockState()).isCancelled()) {
                return 6;
            } else {
                return integer;
            }
        } else {
            return integer;
        }
    }
}
