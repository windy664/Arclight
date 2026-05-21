package io.izzel.arclight.common.bridge.core.world.item.crafting;

import com.google.gson.JsonElement;
import net.minecraft.world.item.crafting.RecipeHolder;

public interface RecipeManagerBridge {

    default void bridge$addRecipe(RecipeHolder<?> recipe) {

    }

    default void bridge$clearRecipes() {

    }
}
