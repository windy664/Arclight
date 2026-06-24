package io.izzel.arclight.common.bridge.core.server.level;

import net.minecraft.server.level.ThreadedLevelLightEngine;

import java.io.IOException;

public interface ServerChunkCacheBridge {

    default boolean bridge$tickDistanceManager() {
        return false;
    }

    default ThreadedLevelLightEngine bridge$getLightManager() {
        return null;
    }

    default boolean isChunkLoaded(int chunkX, int chunkZ) {
        return false;
    }

    default void close(boolean save) throws IOException {

    }

    default void purgeUnload() {

    }

    default void bridge$setViewDistance(int viewDistance) {
    }

    default void bridge$setSimulationDistance(int simDistance) {
    }
}
