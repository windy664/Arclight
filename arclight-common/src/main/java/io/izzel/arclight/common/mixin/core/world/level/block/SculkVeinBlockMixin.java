package io.izzel.arclight.common.mixin.core.world.level.block;

import com.llamalad7.mixinextras.sugar.Cancellable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.MultifaceSpreadeableBlock;
import net.minecraft.world.level.block.MultifaceSpreader;
import net.minecraft.world.level.block.SculkBehaviour;
import net.minecraft.world.level.block.SculkSpreader;
import net.minecraft.world.level.block.SculkVeinBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.concurrent.atomic.AtomicReference;

@Mixin(SculkVeinBlock.class)
public abstract class SculkVeinBlockMixin extends MultifaceSpreadeableBlock implements SculkBehaviour {

    // @formatter:off
    @Shadow @Final private MultifaceSpreader veinSpreader;
    @Shadow public abstract void onDischarged(LevelAccessor level, BlockState state, BlockPos pos, RandomSource random);
    // @formatter:on

    public SculkVeinBlockMixin(Properties properties) {
        super(properties);
    }

    @Unique
    private AtomicReference<BlockPos> sourceBlock =
            new AtomicReference<>();

    @Inject(method = "attemptUseCharge", at = @At("HEAD"))
    private void arclight$setSourceBlock(SculkSpreader.ChargeCursor cursor, LevelAccessor level, BlockPos originPos, RandomSource random, SculkSpreader spreader, boolean spreadVeins, CallbackInfoReturnable<Integer> cir) {
        this.sourceBlock.set(originPos);
    }

    @Redirect(method = "attemptPlaceSculk(Lnet/minecraft/world/level/block/SculkSpreader;Lnet/minecraft/world/level/LevelAccessor;Lnet/minecraft/core/BlockPos;Lnet/minecraft/util/RandomSource;)Z", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/LevelAccessor;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z"))
    private boolean arclight$handleBlockSpreadEvent(LevelAccessor instance, BlockPos blockPos, BlockState state, int i, @Cancellable CallbackInfoReturnable<Boolean> cir) {
        // CraftBukkit start - Call BlockSpreadEvent
        if (!CraftEventFactory.handleBlockSpreadEvent(instance, sourceBlock.get(), blockPos, state, i)) {
            cir.setReturnValue(false);
        }
        // CraftBukkit end
        this.sourceBlock.set(null);
        return true;
    }

    // bukkit methods
    private boolean attemptPlaceSculk(final SculkSpreader spreader, final LevelAccessor level, final BlockPos pos, final RandomSource random, BlockPos sourceBlock) {
        BlockState state = level.getBlockState(pos);
        TagKey<Block> replaceTag = spreader.replaceableBlocks();

        for(Direction support : Direction.allShuffled(random)) {
            if (hasFace(state, support)) {
                BlockPos supportPos = pos.relative(support);
                BlockState supportState = level.getBlockState(supportPos);
                if (supportState.is(replaceTag)) {
                    BlockState defaultSculk = Blocks.SCULK.defaultBlockState();
                    // CraftBukkit start - Call BlockSpreadEvent
                    if (!org.bukkit.craftbukkit.event.CraftEventFactory.handleBlockSpreadEvent(level, sourceBlock, supportPos, defaultSculk, 3)) {
                        return false;
                    }
                    // CraftBukkit end
                    Block.pushEntitiesUp(supportState, defaultSculk, level, supportPos);
                    level.playSound(null, supportPos, SoundEvents.SCULK_BLOCK_SPREAD, SoundSource.BLOCKS, 1.0F, 1.0F);
                    this.veinSpreader.spreadAll(defaultSculk, level, supportPos, spreader.isWorldGeneration());
                    Direction skip = support.getOpposite();

                    for(Direction veinBlocks : DIRECTIONS) {
                        if (veinBlocks != skip) {
                            BlockPos veinPos = supportPos.relative(veinBlocks);
                            BlockState possibleVeinBlock = level.getBlockState(veinPos);
                            if (possibleVeinBlock.is(((SculkVeinBlock) (Object) this))) {
                                this.onDischarged(level, possibleVeinBlock, veinPos, random);
                            }
                        }
                    }

                    return true;
                }
            }
        }

        return false;
    }

}
