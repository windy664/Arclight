package io.izzel.arclight.common.bridge.core.world.chunk;

import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;
import org.bukkit.persistence.PersistentDataContainer;

public interface ChunkAccessBridge {

    default void setBiome(int i, int j, int k, Holder<Biome> biome) {
    }

    default PersistentDataContainer bridge$getPersistentDataContainer() {
        return null;
    }

    default void bridge$setPersistentDataContainer(PersistentDataContainer container) {
    }
}
