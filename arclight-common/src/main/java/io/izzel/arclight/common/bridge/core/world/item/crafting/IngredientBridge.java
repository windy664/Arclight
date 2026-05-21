package io.izzel.arclight.common.bridge.core.world.item.crafting;

public interface IngredientBridge {

    default void bridge$setExact(boolean exact) {

    }

    default boolean bridge$isExact() {
        return false;
    }
}
