package io.izzel.arclight.common.bridge.core.server.level;

public interface TicketTypeBridge {

    default long timeout() {
        return 0;
    }

    default void bridge$setLifespan(long lifespan) {

    }
}
