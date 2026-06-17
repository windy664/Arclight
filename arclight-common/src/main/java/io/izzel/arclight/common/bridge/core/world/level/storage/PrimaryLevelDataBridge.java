package io.izzel.arclight.common.bridge.core.world.level.storage;

import com.mojang.serialization.Lifecycle;
import net.minecraft.core.Registry;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.LevelSettings;
import net.minecraft.world.level.dimension.LevelStem;

public interface PrimaryLevelDataBridge {

    default void setWorld(ServerLevel world) {

    }

    default ServerLevel bridge$getWorld() {
        return null;
    }

    default LevelSettings bridge$getWorldSettings() {
        return null;
    }

    default Lifecycle bridge$getLifecycle() {
        return null;
    }

    default void checkName(String name) {

    }

    default void arclight$offerCustomDimensions(Registry<LevelStem> registry) {

    }
}
