package io.izzel.arclight.common.bridge.core.world.level.block.entity;

import org.bukkit.potion.PotionEffect;

public interface BeaconBlockEntityBridge {

    default PotionEffect bridge$getPrimaryEffect() {
        return null;
    }

    default PotionEffect bridge$getSecondaryEffect() {
        return null;
    }
}
