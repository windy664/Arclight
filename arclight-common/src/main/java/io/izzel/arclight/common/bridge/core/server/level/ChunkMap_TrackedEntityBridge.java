package io.izzel.arclight.common.bridge.core.server.level;

import net.minecraft.core.SectionPos;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.world.entity.Entity;

public interface ChunkMap_TrackedEntityBridge {

    default ServerEntity bridge$getServerEntity() {
        return null;
    }

    default Entity bridge$getEntity() {
        return null;
    }

    default SectionPos bridge$getLastSectionPos() {
        return null;
    }

    default void bridge$setLastSectionPos(SectionPos pos) {

    }
}
