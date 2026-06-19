package io.izzel.arclight.common.mixin.core.world.effect;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.level.Level;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "net.minecraft.world.effect.OozingMobEffect")
public class OozingMobEffectMixin {

    @Inject(method = "spawnSlimeOffspring", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;addFreshEntity(Lnet/minecraft/world/entity/Entity;)Z"))
    private void arclight$pushSpawnReason(Level level, double x, double y, double z, CallbackInfo ci, @Local Slime slime) {
        slime.arclight$pushAddEntityReason(CreatureSpawnEvent.SpawnReason.POTION_EFFECT);
    }
}
