package io.izzel.arclight.common.mixin.core.world.level.block;

import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.VineBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(VineBlock.class)
public class VineBlockMixin {

    @Inject(method = "randomTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/VineBlock;isAcceptableNeighbour(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/Direction;)Z", ordinal = 0))
    private void arclight$setSource(BlockState state, ServerLevel level, BlockPos pos, RandomSource random, CallbackInfo ci, @Share("arclight$source") LocalRef<BlockPos> arclight$source) {
        arclight$source.set(pos);
    }

    @Redirect(
            method = "randomTick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/level/ServerLevel;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z",
                    ordinal = 0
            )
    )
    private boolean arclight$handleBlockSpreadEvent0(ServerLevel instance, BlockPos blockPos, BlockState blockState, int i, @Share("arclight$source") LocalRef<BlockPos> arclight$source) {
        return CraftEventFactory.handleBlockSpreadEvent(instance, arclight$source.get(), blockPos, blockState, i);
    }

    @Redirect(
            method = "randomTick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/level/ServerLevel;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z",
                    ordinal = 1
            )
    )
    private boolean arclight$handleBlockSpreadEvent1(ServerLevel instance, BlockPos blockPos, BlockState blockState, int i, @Share("arclight$source") LocalRef<BlockPos> arclight$source) {
        return CraftEventFactory.handleBlockSpreadEvent(instance, arclight$source.get(), blockPos, blockState, i);
    }

    @Redirect(
            method = "randomTick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/level/ServerLevel;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z",
                    ordinal = 2
            )
    )
    private boolean arclight$handleBlockSpreadEvent2(ServerLevel instance, BlockPos blockPos, BlockState blockState, int i, @Share("arclight$source") LocalRef<BlockPos> arclight$source) {
        return CraftEventFactory.handleBlockSpreadEvent(instance, arclight$source.get(), blockPos, blockState, i);
    }

    @Redirect(
            method = "randomTick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/level/ServerLevel;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z",
                    ordinal = 3
            )
    )
    private boolean arclight$handleBlockSpreadEvent3(ServerLevel instance, BlockPos blockPos, BlockState blockState, int i, @Share("arclight$source") LocalRef<BlockPos> arclight$source) {
        return CraftEventFactory.handleBlockSpreadEvent(instance, arclight$source.get(), blockPos, blockState, i);
    }

    @Redirect(
            method = "randomTick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/level/ServerLevel;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z",
                    ordinal = 4
            )
    )
    private boolean arclight$handleBlockSpreadEvent4(ServerLevel instance, BlockPos blockPos, BlockState blockState, int i, @Share("arclight$source") LocalRef<BlockPos> arclight$source) {
        return CraftEventFactory.handleBlockSpreadEvent(instance, arclight$source.get(), blockPos, blockState, i);
    }

    @Redirect(
            method = "randomTick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/level/ServerLevel;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z",
                    ordinal = 7
            )
    )
    private boolean arclight$handleBlockSpreadEvent5(ServerLevel instance, BlockPos blockPos, BlockState blockState, int i, @Share("arclight$source") LocalRef<BlockPos> arclight$source) {
        return CraftEventFactory.handleBlockSpreadEvent(instance, arclight$source.get(), blockPos, blockState, i);
    }

    @Redirect(
            method = "randomTick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/level/ServerLevel;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z",
                    ordinal = 8
            )
    )
    private boolean arclight$handleBlockSpreadEvent6(ServerLevel instance, BlockPos blockPos, BlockState blockState, int i, @Share("arclight$source") LocalRef<BlockPos> arclight$source) {
        return CraftEventFactory.handleBlockSpreadEvent(instance, arclight$source.get(), blockPos, blockState, i);
    }

    @Redirect(
            method = "randomTick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/level/ServerLevel;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z",
                    ordinal = 5
            )
    )
    private boolean arclight$handleBlockGrowEvent0(ServerLevel instance, BlockPos blockPos, BlockState blockState, int i) {
        return CraftEventFactory.handleBlockGrowEvent(instance, blockPos, blockState, i);
    }

    @Redirect(
            method = "randomTick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/level/ServerLevel;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z",
                    ordinal = 6
            )
    )
    private boolean arclight$handleBlockGrowEvent1(ServerLevel instance, BlockPos blockPos, BlockState blockState, int i) {
        return CraftEventFactory.handleBlockGrowEvent(instance, blockPos, blockState, i);
    }
}
