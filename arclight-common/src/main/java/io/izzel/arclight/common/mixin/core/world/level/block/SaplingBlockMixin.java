package io.izzel.arclight.common.mixin.core.world.level.block;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.izzel.arclight.common.mod.mixins.annotation.TransformAccess;
import io.izzel.arclight.common.mod.util.ArclightCaptures;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.SaplingBlock;
import net.minecraft.world.level.block.grower.TreeGrower;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import org.bukkit.Location;
import org.bukkit.TreeType;
import org.bukkit.craftbukkit.block.CapturedBlockState;
import org.bukkit.craftbukkit.util.CraftLocation;
import org.bukkit.event.world.StructureGrowEvent;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(SaplingBlock.class)
public class SaplingBlockMixin {

    @Shadow
    @Final
    protected TreeGrower treeGrower;
    @TransformAccess(Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC)
    private static TreeType treeType; // CraftBukkit

    @WrapOperation(method = "advanceTree", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/grower/TreeGrower;growTree(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/level/chunk/ChunkGenerator;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/util/RandomSource;)Z"))
    private boolean arclight$callStructureGrowEvent(TreeGrower instance, ServerLevel level, ChunkGenerator dz, BlockPos pos, BlockState state, RandomSource random, Operation<Boolean> original) {
        // CraftBukkit start
        if (level.bridge$isCaptureTreeGeneration()) {
            return original.call(instance, level, dz, pos, state, random);
        } else {
            level.bridge$setCaptureTreeGeneration(true);
            this.treeGrower.growTree(level, level.getChunkSource().getGenerator(), pos, state, random);
            level.bridge$setCaptureTreeGeneration(false);
            if (level.bridge$getCapturedBlockStates().size() > 0) {
                TreeType treeType = ArclightCaptures.getTreeType();
                ArclightCaptures.captureTreeType(null);
                Location location = CraftLocation.toBukkit(pos, level.getWorld());
                java.util.List<org.bukkit.block.BlockState> blocks = new java.util.ArrayList<>(level.bridge$getCapturedBlockStates().values());
                level.bridge$getCapturedBlockStates().clear();
                StructureGrowEvent event = null;
                if (treeType != null) {
                    event = new StructureGrowEvent(location, treeType, false, null, blocks);
                    org.bukkit.Bukkit.getPluginManager().callEvent(event);
                }
                if (event == null || !event.isCancelled()) {
                    for (org.bukkit.block.BlockState capturedBlockState : blocks) {
                        CapturedBlockState.setBlockState(capturedBlockState);
                    }
                }
            }
        }
        // CraftBukkit end
        return true;
    }
}
