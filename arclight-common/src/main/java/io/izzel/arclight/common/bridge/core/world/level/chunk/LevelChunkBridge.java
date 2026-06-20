package io.izzel.arclight.common.bridge.core.world.level.chunk;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import org.bukkit.Chunk;
import org.bukkit.craftbukkit.persistence.CraftPersistentDataContainer;

public interface LevelChunkBridge {

    default Chunk bridge$getBukkitChunk() {
        return null;
    }

    default BlockState bridge$setType(BlockPos pos, BlockState state, int flags, boolean doPlace) {
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

    default void setUnsaved(boolean b) {

    }

    default void loadCallback() {

    }

    default void unloadCallback() {

    }

    default BlockState setBlockState(BlockPos pos, BlockState state, int flags, boolean doPlace) {
        return null;
    }
}
