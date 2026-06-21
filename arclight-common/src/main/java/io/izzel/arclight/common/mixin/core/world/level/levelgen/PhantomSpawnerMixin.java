package io.izzel.arclight.common.mixin.core.world.level.levelgen;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.monster.Phantom;
import net.minecraft.world.level.levelgen.PhantomSpawner;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PhantomSpawner.class)
public class PhantomSpawnerMixin {

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;addFreshEntityWithPassengers(Lnet/minecraft/world/entity/Entity;)V"))
    private void arclight$addSpawnReason(ServerLevel level, boolean spawnEnemies, CallbackInfo ci, @Local Phantom phantom) {
        phantom.arclight$pushAddEntityReason(CreatureSpawnEvent.SpawnReason.NATURAL);
    }
}
