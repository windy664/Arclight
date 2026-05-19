package io.izzel.arclight.common.bridge.core.server.level;

import java.io.IOException;

public interface ServerChunkCacheBridge {

    default boolean isChunkLoaded(int chunkX, int chunkZ) {
        return false;
    }

    default void close(boolean save) throws IOException {

    }

    default void purgeUnload() {

    }
}
