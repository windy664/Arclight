package io.izzel.arclight.common.bridge.core.server.level;

public interface TicketBridge {

    default Object bridge$getKey() {
        return null;
    }

    default void bridge$setKey(Object key) {
    }
}
