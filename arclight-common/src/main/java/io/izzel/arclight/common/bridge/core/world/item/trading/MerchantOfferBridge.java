package io.izzel.arclight.common.bridge.core.world.item.trading;

import org.bukkit.craftbukkit.inventory.CraftMerchantRecipe;

public interface MerchantOfferBridge {

    default CraftMerchantRecipe bridge$asBukkit() {
        return null;
    }
}
