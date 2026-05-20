package io.izzel.arclight.common.bridge.core.util;

public interface TickThrottlerBridge {

    default boolean isIncrementAndUnderThreshold() {
        return false;
    }

    default boolean isIncrementAndUnderThreshold(int incrementStep, int threshold) {
        return false;
    }
}
