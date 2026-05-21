package io.izzel.arclight.common.bridge.core.world.level.chunk;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import org.bukkit.Chunk;
import org.bukkit.craftbukkit.persistence.CraftPersistentDataContainer;

public interface LevelChunkBridge {

    default Chunk bridge$getBukkitChunk() {
        return null;
    }

    default BlockState bridge$setType(BlockPos pos, BlockState state, boolean isMoving, boolean doPlace) {
        return null;
    }

    default boolean bridge$isMustNotSave() {
        return false;
    }

    default void bridge$setMustNotSave(boolean mustNotSave) {

    }

    default boolean bridge$isNeedsDecoration() {
        return false;
    }

    default void bridge$loadCallback() {

    }

    default void bridge$unloadCallback() {

    }

    default CraftPersistentDataContainer bridge$getPersistentContainer() {
        return null;
    }
}
