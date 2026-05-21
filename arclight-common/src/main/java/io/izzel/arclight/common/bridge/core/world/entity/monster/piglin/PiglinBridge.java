package io.izzel.arclight.common.bridge.core.world.entity.monster.piglin;

import net.minecraft.world.item.Item;

import java.util.Set;

public interface PiglinBridge {

    default Set<Item> bridge$getAllowedBarterItems() {
        return null;
    }

    default Set<Item> bridge$getInterestItems() {
        return null;
    }
}
