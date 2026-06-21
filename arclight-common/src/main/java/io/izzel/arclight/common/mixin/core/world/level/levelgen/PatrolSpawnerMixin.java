package io.izzel.arclight.common.mixin.core.world.level.levelgen;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.monster.PatrollingMonster;
import net.minecraft.world.level.levelgen.PatrolSpawner;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PatrolSpawner.class)
public class PatrolSpawnerMixin {

    @Inject(method = "spawnPatrolMember", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;addFreshEntityWithPassengers(Lnet/minecraft/world/entity/Entity;)V"))
    private void arclight$addSpawnReason(ServerLevel level, BlockPos pos, RandomSource random, boolean bl, CallbackInfoReturnable<Boolean> cir, @Local PatrollingMonster mob) {
        mob.arclight$pushAddEntityReason(CreatureSpawnEvent.SpawnReason.PATROL);
    }
}
