package io.izzel.arclight.common.bridge.core.world.entity.animal.turtle;

import io.izzel.arclight.common.bridge.core.world.entity.animal.AnimalBridge;

public interface TurtleBridge extends AnimalBridge {

    default int bridge$getDigging() {
        return 0;
    }

    default void bridge$setDigging(boolean digging) {

    }

    default void bridge$setDigging(int i) {

    }

    default void bridge$setHasEgg(boolean b) {

    }
}
