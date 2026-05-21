package io.izzel.arclight.common.bridge.core.world.chunk;

import org.bukkit.persistence.PersistentDataContainer;

public interface ChunkAccessBridge {

    default PersistentDataContainer bridge$getPersistentDataContainer() {
        return null;
    }
}
