package io.izzel.arclight.common.bridge.core.world.level.levelgen.flat;

import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.levelgen.flat.FlatLevelGeneratorSettings;

public interface FlatLevelGeneratorSettingsBridge {
    default void bridge$setBiomeSource(BiomeSource biomeSource) {

    }

    default FlatLevelGeneratorSettings bridge$withBiomeSource(BiomeSource biomeSource) {
        return null;
    }

    default BiomeSource bridge$getBiomeSource() {
        return null;
    }
}
