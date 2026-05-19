package io.izzel.arclight.common.bridge.core.server.level;

import net.minecraft.server.network.ServerPlayerConnection;
import net.minecraft.world.entity.Entity;

import java.util.Set;

public interface ServerEntityBridge {

    default void bridge$setTrackedPlayers(Set<ServerPlayerConnection> trackedPlayers) {

    }

    default Entity bridge$getTrackingEntity() {
        return null;
    }

    default boolean bridge$syncPosition() {
        return false;
    }

    default boolean bridge$instantSyncPosition() {
        return false;
    }

    default boolean bridge$instantSyncMotion() {
        return false;
    }
}
