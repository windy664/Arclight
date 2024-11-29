package io.izzel.arclight.common.mixin.core.world.level;

import io.izzel.arclight.api.ArclightPlatform;
import io.izzel.arclight.common.bridge.core.world.WorldBridge;
import io.izzel.arclight.common.mod.mixins.annotation.OnlyInPlatform;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelWriter;
import net.minecraft.world.level.block.state.BlockState;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v.block.CraftBlock;
import org.bukkit.craftbukkit.v.block.data.CraftBlockData;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Level.class)
@OnlyInPlatform(value = {ArclightPlatform.VANILLA, ArclightPlatform.FABRIC})
public abstract class LevelMixin_Vanilla implements WorldBridge, LevelAccessor, LevelWriter {
    @Inject(method = "setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;II)Z", require = 0, cancellable = true, at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;updateNeighbourShapes(Lnet/minecraft/world/level/LevelAccessor;Lnet/minecraft/core/BlockPos;II)V"))
    private void arclight$callBlockPhysics(BlockPos pos, BlockState state, int i, int j, CallbackInfoReturnable<Boolean> cir) {
        try {
            if (this.bridge$getWorld() != null) {
                BlockPhysicsEvent event = new BlockPhysicsEvent(CraftBlock.at(this, pos), CraftBlockData.fromData(state));
                Bukkit.getPluginManager().callEvent(event);
                if (event.isCancelled()) {
                    cir.setReturnValue(true);
                }
            }
        } catch (StackOverflowError e) {
            bridge$setLastPhysicsProblem(pos);
        }
    }

    @Inject(method = "setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;II)Z", require = 0, cancellable = true, at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;onBlockStateChange(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/block/state/BlockState;)V"))
    private void arclight$preventPoiUpdate(BlockPos blockPos, BlockState blockState, int i, int j, CallbackInfoReturnable<Boolean> cir) {
        if (bridge$preventPoiUpdated()) {
            cir.setReturnValue(true);
        }
    }
}
