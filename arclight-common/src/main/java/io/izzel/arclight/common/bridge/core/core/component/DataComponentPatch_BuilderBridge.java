package io.izzel.arclight.common.bridge.core.core.component;

import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponentType;

public interface DataComponentPatch_BuilderBridge {

    default void copy(DataComponentPatch orig) {

    }

    default void clear(DataComponentType<?> type) {

    }

    default boolean isSet(DataComponentType<?> type) {
        return false;
    }

    default boolean isEmpty() {
        return false;
    }
}
