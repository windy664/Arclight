package io.izzel.arclight.common.mixin.core.world.item;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(AxeItem.class)
public class AxeItemMixin {

    @Inject(method = "useOn", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/context/UseOnContext;getItemInHand()Lnet/minecraft/world/item/ItemStack;"), cancellable = true)
    private void arclight$callEntityChangeBlockEvent(UseOnContext context, CallbackInfoReturnable<InteractionResult> cir, @Local Player player, @Local BlockPos pos, @Local Optional<BlockState> newBlock) {
        // Paper start - EntityChangeBlockEvent
        if (!org.bukkit.craftbukkit.event.CraftEventFactory.callEntityChangeBlockEvent(player, pos, newBlock.get())) {
            cir.setReturnValue(InteractionResult.PASS);
        }
        // Paper end
    }
}
