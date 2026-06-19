package io.izzel.arclight.common.mixin.core.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DoublePlantBlock.class)
public class DoublePlantBlockMixin {

    @Inject(method = "preventDropFromBottomPart", at = @At("HEAD"), cancellable = true)
    private static void arclight$callBlockPhysicsEvent(Level level, BlockPos pos, BlockState state, Player player, CallbackInfo ci) {
        // CraftBukkit start
        if (org.bukkit.craftbukkit.event.CraftEventFactory.callBlockPhysicsEvent(level, pos).isCancelled()) {
            ci.cancel();
            return;
        }
        // CraftBukkit end
    }
}
