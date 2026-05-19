package io.izzel.arclight.common.bridge.core.server.level;

import io.izzel.arclight.common.mod.util.ArclightCallbackExecutor;
import net.minecraft.server.level.ChunkHolder;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.ChunkGenerator;

import java.util.function.BooleanSupplier;

public interface ChunkMapBridge {

    default void bridge$tick(BooleanSupplier hasMoreTime) {

    }

    default Iterable<ChunkHolder> bridge$getLoadedChunksIterable() {
        return null;
    }

    default void bridge$tickEntityTracker() {

    }

    default ArclightCallbackExecutor bridge$getCallbackExecutor() {
        return null;
    }

    default ChunkHolder bridge$chunkHolderAt(long chunkPos) {
        return null;
    }

    default void bridge$setViewDistance(int i) {

    }

    default void bridge$setChunkGenerator(ChunkGenerator generator) {

    }
}
