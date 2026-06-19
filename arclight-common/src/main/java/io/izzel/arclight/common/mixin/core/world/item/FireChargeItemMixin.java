package io.izzel.arclight.common.mixin.core.world.item;

import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.FireChargeItem;
import net.minecraft.world.item.context.UseOnContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FireChargeItem.class)
public class FireChargeItemMixin {

    @Inject(method = "useOn", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/FireChargeItem;playSound(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;)V"), cancellable = true)
    private void arclight$callBlockIgniteEvent(UseOnContext context, CallbackInfoReturnable<InteractionResult> cir) {
        // CraftBukkit start - fire BlockIgniteEvent
        if (org.bukkit.craftbukkit.event.CraftEventFactory.callBlockIgniteEvent(context.getLevel(), context.getClickedPos(), org.bukkit.event.block.BlockIgniteEvent.IgniteCause.FIREBALL, context.getPlayer()).isCancelled()) {
            if (!context.getPlayer().getAbilities().instabuild) {
                context.getItemInHand().shrink(1);
            }
            cir.setReturnValue(InteractionResult.PASS);
        }
        // CraftBukkit end
    }
}
