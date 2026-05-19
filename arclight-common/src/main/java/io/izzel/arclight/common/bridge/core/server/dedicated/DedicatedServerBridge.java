package io.izzel.arclight.common.bridge.core.server.dedicated;

import net.minecraft.core.RegistryAccess;
import net.minecraft.server.WorldLoader;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.rcon.RconConsoleSource;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.level.storage.PrimaryLevelData;

public interface DedicatedServerBridge {

    default String runCommand(RconConsoleSource rconConsoleSource, String s) {
        return s;
    }

    default void bridge$platform$exitNow() {
    }

    default WorldLoader.DataLoadContext arclight$dataLoadContext() {
        return null;
    }

    default void arclight$forceUpgradeIfNeeded(LevelStorageSource.LevelStorageAccess worldSession, RegistryAccess.Frozen dimensions) {

    }

    default void arclight$prepareAndAddLevel(ServerLevel level, PrimaryLevelData levelData) {

    }
}
