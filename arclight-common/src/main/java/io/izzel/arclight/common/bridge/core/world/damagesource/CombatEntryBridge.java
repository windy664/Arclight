package io.izzel.arclight.common.bridge.core.world.damagesource;

import net.minecraft.network.chat.Component;

public interface CombatEntryBridge {

    default void bridge$setDeathMessage(Component component) {

    }

    default Component bridge$getDeathMessage() {
        return null;
    }
}
