package io.izzel.arclight.common.mixin.core.world.level.block;

import com.llamalad7.mixinextras.sugar.Cancellable;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.EyeblossomBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EyeblossomBlock.class)
public class EyeblossomBlockMixin {

    @Redirect(method = "tryChangingState", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z"))
    private boolean arclight$handleBlockFormEvent(ServerLevel instance, BlockPos blockPos, BlockState state, int i, @Cancellable CallbackInfoReturnable<Boolean> cir) {
        // CraftBukkit start - BlockFormEvent
        if (!CraftEventFactory.handleBlockFormEvent(instance, blockPos, state, i)) {
            cir.setReturnValue(false);
        }
        // CraftBukkit end
        return true;
    }
}
