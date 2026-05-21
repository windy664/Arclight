package io.izzel.arclight.common.bridge.core.world.entity.animal.fox;

import io.izzel.arclight.common.bridge.core.world.entity.animal.AnimalBridge;

import java.util.UUID;

public interface FoxBridge extends AnimalBridge {

    default void bridge$addTrustedUUID(UUID uuidIn) {

    }
}
