package io.izzel.arclight.common.mixin.bukkit.inventory;

import net.minecraft.world.item.ItemStack;
import org.bukkit.craftbukkit.v.inventory.CraftItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(CraftItemStack.class)
public abstract class CraftItemStackMixin {
    @Redirect(method = "asCraftMirror", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;isEmpty()Z"))
    private static boolean arclight$asCraftMirror$ItemStack$isEmpty(ItemStack instance) {
        return false;
    }
}
