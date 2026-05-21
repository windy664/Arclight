package io.izzel.arclight.common.bridge.core.world.entity;

public interface AgeableMobBridge extends LivingEntityBridge {

    default boolean bridge$isAgeLocked() {
        return false;
    }
}
