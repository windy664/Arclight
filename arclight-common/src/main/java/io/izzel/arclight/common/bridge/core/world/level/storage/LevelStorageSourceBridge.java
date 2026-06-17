package io.izzel.arclight.common.bridge.core.world.level.storage;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.level.validation.ContentValidationException;

import java.io.IOException;

public interface LevelStorageSourceBridge {

    default LevelStorageSource.LevelStorageAccess createAccess(String saveName, ResourceKey<LevelStem> world) throws IOException {
        return null;
    }

    default LevelStorageSource.LevelStorageAccess validateAndCreateAccess(String saveName, ResourceKey<LevelStem> world) throws IOException, ContentValidationException {
        return null;
    }

    interface LevelStorageAccessBridge {

        default void bridge$setDimType(ResourceKey<LevelStem> typeKey) {

        }

        default ResourceKey<LevelStem> bridge$getTypeKey() {
            return null;
        }
    }
}
