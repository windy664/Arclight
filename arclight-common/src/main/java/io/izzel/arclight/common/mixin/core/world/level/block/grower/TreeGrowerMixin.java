package io.izzel.arclight.common.mixin.core.world.level.block.grower;

import com.llamalad7.mixinextras.sugar.Local;
import io.izzel.arclight.common.mod.ArclightConstants;
import io.izzel.arclight.common.mod.util.ArclightCaptures;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.features.TreeFeatures;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.grower.TreeGrower;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import org.bukkit.TreeType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TreeGrower.class)
public class TreeGrowerMixin {

    @Inject(method = "growTree", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/RegistryAccess;lookupOrThrow(Lnet/minecraft/resources/ResourceKey;)Lnet/minecraft/core/Registry;", ordinal = 0))
    private void arclight$setTreeTypeMega(ServerLevel level, ChunkGenerator generator, BlockPos pos, BlockState state, RandomSource random, CallbackInfoReturnable<Boolean> cir, @Local ResourceKey<ConfiguredFeature<?, ?>> megaFeatureKey) {
        var arclight$holder = level.registryAccess().lookupOrThrow(Registries.CONFIGURED_FEATURE).get(megaFeatureKey).orElse(null);
        if (arclight$holder !=null) {
            this.setTreeType(arclight$holder);
        }
    }

    @Inject(method = "growTree", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/Holder;value()Ljava/lang/Object;", ordinal = 1))
    private void arclight$setTreeType(ServerLevel level, ChunkGenerator generator, BlockPos pos, BlockState state, RandomSource random, CallbackInfoReturnable<Boolean> cir, @Local(ordinal = 1) ResourceKey<ConfiguredFeature<?, ?>> featureKey) {
        var arclight$holder1 = level.registryAccess().lookupOrThrow(Registries.CONFIGURED_FEATURE).get(featureKey).orElse(null);
        if (arclight$holder1 != null) {
            this.setTreeType(arclight$holder1);
        }
    }

    // CraftBukkit start
    private void setTreeType(Holder<ConfiguredFeature<?, ?>> holder) {
        ResourceKey<ConfiguredFeature<?, ?>> worldgentreeabstract = holder.unwrapKey().get();
        if (worldgentreeabstract == TreeFeatures.OAK || worldgentreeabstract == TreeFeatures.OAK_BEES_005) {
            ArclightCaptures.captureTreeType(TreeType.TREE);
        } else if (worldgentreeabstract == TreeFeatures.HUGE_RED_MUSHROOM) {
            ArclightCaptures.captureTreeType(TreeType.RED_MUSHROOM);
        } else if (worldgentreeabstract == TreeFeatures.HUGE_BROWN_MUSHROOM) {
            ArclightCaptures.captureTreeType(TreeType.BROWN_MUSHROOM);
        } else if (worldgentreeabstract == TreeFeatures.JUNGLE_TREE) {
            ArclightCaptures.captureTreeType(TreeType.COCOA_TREE);
        } else if (worldgentreeabstract == TreeFeatures.JUNGLE_TREE_NO_VINE) {
            ArclightCaptures.captureTreeType(TreeType.SMALL_JUNGLE);
        } else if (worldgentreeabstract == TreeFeatures.PINE) {
            ArclightCaptures.captureTreeType(TreeType.TALL_REDWOOD);
        } else if (worldgentreeabstract == TreeFeatures.SPRUCE) {
            ArclightCaptures.captureTreeType(TreeType.REDWOOD);
        } else if (worldgentreeabstract == TreeFeatures.ACACIA) {
            ArclightCaptures.captureTreeType(TreeType.ACACIA);
        } else if (worldgentreeabstract == TreeFeatures.BIRCH || worldgentreeabstract == TreeFeatures.BIRCH_BEES_005) {
            ArclightCaptures.captureTreeType(TreeType.BIRCH);
        } else if (worldgentreeabstract == TreeFeatures.SUPER_BIRCH_BEES_0002) {
            ArclightCaptures.captureTreeType(TreeType.TALL_BIRCH);
        } else if (worldgentreeabstract == TreeFeatures.SWAMP_OAK) {
            ArclightCaptures.captureTreeType(TreeType.SWAMP);
        } else if (worldgentreeabstract == TreeFeatures.FANCY_OAK || worldgentreeabstract == TreeFeatures.FANCY_OAK_BEES_005) {
            ArclightCaptures.captureTreeType(TreeType.BIG_TREE);
        } else if (worldgentreeabstract == TreeFeatures.JUNGLE_BUSH) {
            ArclightCaptures.captureTreeType(TreeType.JUNGLE_BUSH);
        } else if (worldgentreeabstract == TreeFeatures.DARK_OAK) {
            ArclightCaptures.captureTreeType(TreeType.DARK_OAK);
        } else if (worldgentreeabstract == TreeFeatures.MEGA_SPRUCE) {
            ArclightCaptures.captureTreeType(TreeType.MEGA_REDWOOD);
        } else if (worldgentreeabstract == TreeFeatures.MEGA_PINE) {
            ArclightCaptures.captureTreeType(TreeType.MEGA_PINE);
        } else if (worldgentreeabstract == TreeFeatures.MEGA_JUNGLE_TREE) {
            ArclightCaptures.captureTreeType(TreeType.JUNGLE);
        } else if (worldgentreeabstract == TreeFeatures.AZALEA_TREE) {
            ArclightCaptures.captureTreeType(TreeType.AZALEA);
        } else if (worldgentreeabstract == TreeFeatures.MANGROVE) {
            ArclightCaptures.captureTreeType(TreeType.MANGROVE);
        } else if (worldgentreeabstract == TreeFeatures.TALL_MANGROVE) {
            ArclightCaptures.captureTreeType(TreeType.TALL_MANGROVE);
        } else if (worldgentreeabstract == TreeFeatures.CHERRY || worldgentreeabstract == TreeFeatures.CHERRY_BEES_005) {
            ArclightCaptures.captureTreeType(TreeType.CHERRY);
        } else if (worldgentreeabstract == TreeFeatures.PALE_OAK || worldgentreeabstract == TreeFeatures.PALE_OAK_BONEMEAL) {
            ArclightCaptures.captureTreeType(TreeType.PALE_OAK);
        } else if (worldgentreeabstract == TreeFeatures.PALE_OAK_CREAKING) {
            ArclightCaptures.captureTreeType(TreeType.PALE_OAK_CREAKING);
        } else {
            ArclightCaptures.captureTreeType(ArclightConstants.MOD);
        }
    }
    // CraftBukkit end
}
