package io.izzel.arclight.common.bridge.core.world.level.portal;

import net.minecraft.world.entity.Entity;

public interface PortalForcerBridge {

    default void bridge$pushSearchRadius(int searchRadius) {

    }

    default void bridge$pushPortalCreate(Entity entity, int createRadius) {

    }
}
