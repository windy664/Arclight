package io.izzel.arclight.common.mixin.core.world.inventory;

import io.izzel.arclight.common.bridge.core.world.ContainerBridge;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(CraftingContainer.class)
public interface CraftingContainerMixin extends ContainerBridge {

    @Override
    default RecipeHolder<?> getCurrentRecipe() {
        return null;
    }

    @Override
    default void setCurrentRecipe(RecipeHolder<?> recipe) {
    }
}
