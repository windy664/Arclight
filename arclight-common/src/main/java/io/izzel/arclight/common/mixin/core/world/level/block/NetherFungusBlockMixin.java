package io.izzel.arclight.common.mixin.core.world.level.block;

import io.izzel.arclight.common.mod.util.ArclightCaptures;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.NetherFungusBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(NetherFungusBlock.class)
public class NetherFungusBlockMixin {

    @Inject(method = "performBonemeal", at = @At(value = "INVOKE", target = "Ljava/util/Optional;ifPresent(Ljava/util/function/Consumer;)V"))
    private void arclight$setTreeType(ServerLevel level, RandomSource random, BlockPos pos, BlockState state, CallbackInfo ci) {
        // CraftBukkit start
        if (((NetherFungusBlock) (Object) this) == Blocks.WARPED_FUNGUS) {
            ArclightCaptures.captureTreeType(org.bukkit.TreeType.WARPED_FUNGUS);
        } else if (((NetherFungusBlock) (Object) this) == Blocks.CRIMSON_FUNGUS) {
            ArclightCaptures.captureTreeType(org.bukkit.TreeType.CRIMSON_FUNGUS);
        }
        // CraftBukkit end
    }
}
