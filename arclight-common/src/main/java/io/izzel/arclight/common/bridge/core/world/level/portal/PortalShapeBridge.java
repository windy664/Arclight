package io.izzel.arclight.common.bridge.core.world.level.portal;

public interface PortalShapeBridge {

    default boolean bridge$createPortal() {
        return false;
    }
}
