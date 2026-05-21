package io.izzel.arclight.common.bridge.core.world.item.crafting;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.Recipe;

public interface RecipeBridge {

    default Recipe bridge$toBukkitRecipe(NamespacedKey id) {
        return null;
    }
}
