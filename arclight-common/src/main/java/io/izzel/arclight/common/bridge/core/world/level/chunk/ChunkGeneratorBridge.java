package io.izzel.arclight.common.bridge.core.world.level.chunk;

import net.minecraft.world.level.biome.BiomeSource;

public interface ChunkGeneratorBridge {

    default void bridge$setBiomeSource(BiomeSource biomeSource) {

    }
}
