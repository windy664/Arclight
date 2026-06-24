package io.izzel.arclight.common.bridge.core.world.level.chunk;

public interface LevelChunkBridge {

    default boolean bridge$isMustNotSave() {
        return false;
    }

    default void bridge$setMustNotSave(boolean mustNotSave) {

    }

    default boolean bridge$isNeedsDecoration() {
        return false;
    }

    default void setUnsaved(boolean b) {

    }

    default void loadCallback() {

    }

    default void unloadCallback() {

    }
}
