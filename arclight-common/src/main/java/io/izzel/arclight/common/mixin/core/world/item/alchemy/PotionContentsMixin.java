package io.izzel.arclight.common.mixin.core.world.item.alchemy;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.alchemy.PotionContents;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PotionContents.class)
public class PotionContentsMixin {

    @Inject(method = "lambda$applyToLivingEntity$0", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;addEffect(Lnet/minecraft/world/effect/MobEffectInstance;)Z"))
    private static void arclight$pushEffectCause(ServerLevel serverLevel, Player player, LivingEntity entity, MobEffectInstance effect, CallbackInfo ci) {
        entity.bridge$pushEffectCause(EntityPotionEffectEvent.Cause.POTION_DRINK);
    }
}
