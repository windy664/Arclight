package io.izzel.arclight.common.mixin.core.world.level.block;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gamerules.GameRules;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(CropBlock.class)
public class CropBlockMixin {

    @Redirect(method = "randomTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z"))
    private boolean arclight$handleBlockGrowEventRandTick(ServerLevel instance, BlockPos blockPos, BlockState state, int i) {
        return CraftEventFactory.handleBlockGrowEvent(instance, blockPos, state, i);
    }

    @Redirect(method = "growCrops", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z"))
    private boolean arclight$handleBlockGrowEventGrow(Level instance, BlockPos pos, BlockState blockState, int updateFlags) {
        return CraftEventFactory.handleBlockGrowEvent(instance, pos, blockState, updateFlags);
    }

    @WrapOperation(method = "entityInside", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;destroyBlock(Lnet/minecraft/core/BlockPos;ZLnet/minecraft/world/entity/Entity;)Z"))
    private boolean arclight$callEntityChangeBlockEvent(ServerLevel instance, BlockPos blockPos, boolean b, Entity entity, Operation<Boolean> original) {
        if (CraftEventFactory.callEntityChangeBlockEvent(entity, blockPos, Blocks.AIR.defaultBlockState(), !(Boolean) instance.getGameRules().get(GameRules.MOB_GRIEFING))) {
            return original.call(instance, blockPos, b, entity);
        } else {
            return false;
        }
    }
}
