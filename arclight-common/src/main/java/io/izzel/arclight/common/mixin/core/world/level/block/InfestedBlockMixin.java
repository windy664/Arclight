package io.izzel.arclight.common.mixin.core.world.level.block;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.monster.Silverfish;
import net.minecraft.world.level.block.InfestedBlock;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InfestedBlock.class)
public class InfestedBlockMixin {

    @Inject(method = "spawnInfestation", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;addFreshEntity(Lnet/minecraft/world/entity/Entity;)Z"))
    private void arclight$pushSpawnReason(ServerLevel level, BlockPos pos, CallbackInfo ci, @Local Silverfish silverfish) {
        silverfish.arclight$pushAddEntityReason(CreatureSpawnEvent.SpawnReason.SILVERFISH_BLOCK);
    }
}
