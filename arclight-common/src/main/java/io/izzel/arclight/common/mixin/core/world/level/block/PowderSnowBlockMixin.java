package io.izzel.arclight.common.mixin.core.world.level.block;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.PowderSnowBlock;
import net.minecraft.world.level.gamerules.GameRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PowderSnowBlock.class)
public class PowderSnowBlockMixin {

    @Inject(method = "lambda$entityInside$0", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;destroyBlock(Lnet/minecraft/core/BlockPos;Z)Z"), cancellable = true)
    private static void arclight$callEntityChangeBlockEvent(Level level, BlockPos position, Entity entity1, CallbackInfo ci, @Local ServerLevel serverLevel) {
        if (!org.bukkit.craftbukkit.event.CraftEventFactory.callEntityChangeBlockEvent(entity1, position, Blocks.AIR.defaultBlockState(), !((Boolean) serverLevel.getGameRules().get(GameRules.MOB_GRIEFING) || entity1 instanceof Player))) {
            ci.cancel();
            return;
        }
        // CraftBukkit end
    }
}
