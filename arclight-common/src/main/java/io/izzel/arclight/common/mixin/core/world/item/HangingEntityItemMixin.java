package io.izzel.arclight.common.mixin.core.world.item;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.decoration.HangingEntity;
import net.minecraft.world.item.HangingEntityItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import org.bukkit.event.hanging.HangingPlaceEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(HangingEntityItem.class)
public class HangingEntityItemMixin {

    @Inject(method = "useOn", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/decoration/HangingEntity;playPlacementSound()V"), cancellable = true)
    private void arclight$callHangingPlaceEvent(UseOnContext context, CallbackInfoReturnable<InteractionResult> cir, @Local Level level, @Local(ordinal = 1) BlockPos blockPos,
                                                @Local Direction clickedFace, @Local ItemStack itemInHand, @Local HangingEntity hangingentity) {
        // CraftBukkit start - fire HangingPlaceEvent
        org.bukkit.entity.Player who = (context.getPlayer() == null) ? null : (org.bukkit.entity.Player) context.getPlayer().getBukkitEntity();
        org.bukkit.block.Block blockClicked = level.getWorld().getBlockAt(blockPos.getX(), blockPos.getY(), blockPos.getZ());
        org.bukkit.block.BlockFace blockFace = org.bukkit.craftbukkit.block.CraftBlock.notchToBlockFace(clickedFace);
        org.bukkit.inventory.EquipmentSlot hand = org.bukkit.craftbukkit.CraftEquipmentSlot.getHand(context.getHand());

        HangingPlaceEvent event = new HangingPlaceEvent((org.bukkit.entity.Hanging) hangingentity.getBukkitEntity(), who, blockClicked, blockFace, hand, org.bukkit.craftbukkit.inventory.CraftItemStack.asBukkitCopy(itemInHand));
        level.getCraftServer().getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            cir.setReturnValue(InteractionResult.FAIL);
        }
        // CraftBukkit end
    }
}
