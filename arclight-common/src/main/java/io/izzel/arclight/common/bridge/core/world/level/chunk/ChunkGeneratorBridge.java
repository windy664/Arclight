package io.izzel.arclight.common.bridge.core.world.level.chunk;

import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.chunk.ChunkAccess;

public interface ChunkGeneratorBridge {

    default void applyBiomeDecoration(WorldGenLevel level, ChunkAccess chunk, StructureManager structureManager) {
    }

    default void addVanillaDecorations(final WorldGenLevel level, final ChunkAccess chunk, final StructureManager structureManager) {
    }

    default void applyBiomeDecoration(WorldGenLevel worldgenlevel, ChunkAccess chunkaccess, StructureManager structuremanager, boolean vanilla) {
    }

    default void bridge$setBiomeSource(BiomeSource biomeSource) {

    }
}
