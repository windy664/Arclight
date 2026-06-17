package io.izzel.arclight.common.bridge.core.server.level;

import java.io.IOException;
import net.minecraft.server.level.ThreadedLevelLightEngine;

public interface ServerChunkProviderBridge {

    default void bridge$close(boolean save) throws IOException {

    }

    default void bridge$purgeUnload() {

    }

    default boolean bridge$tickDistanceManager() {
        return false;
    }

    default boolean bridge$isChunkLoaded(int x, int z) {
        return false;
    }

    default ThreadedLevelLightEngine bridge$getLightManager() {
        return null;
    }

    default void bridge$setViewDistance(int viewDistance) {

    }

    default void bridge$setSimulationDistance(int simDistance) {

    }
}