package io.izzel.arclight.common.bridge.core.server.level;

import net.minecraft.server.level.ChunkMap;
import net.minecraft.world.level.chunk.LevelChunk;

public interface ChunkHolderBridge {

    default int bridge$getOldTicketLevel() {
        return 0;
    }

    default LevelChunk getFullChunkNow() {
        return null;
    }

    default LevelChunk getFullChunkNowUnchecked() {
        return null;
    }

    default void callEventIfUnloading(ChunkMap chunkmap) {

    }
}
