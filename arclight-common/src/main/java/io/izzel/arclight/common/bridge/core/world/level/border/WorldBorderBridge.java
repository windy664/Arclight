package io.izzel.arclight.common.bridge.core.world.level.border;

import net.minecraft.world.level.Level;

public interface WorldBorderBridge {

    default Level bridge$getWorld() {
        return null;
    }

    default void bridge$setWorld(Level world) {

    }
}
