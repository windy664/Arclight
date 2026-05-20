package io.izzel.arclight.common.bridge.core.world.level;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.ChunkAccess;

public interface NaturalSpawner_SpawnStateBridge {
    default void bridge$updateDensity(Mob mobEntity, ChunkAccess chunk) {

    }

    default boolean bridge$canSpawn(MobCategory classification, ChunkPos pos, int limit) {
        return false;
    }

    default boolean bridge$canSpawn(EntityType<?> entityType, BlockPos pos, ChunkAccess chunk) {
        return false;

    }
}
