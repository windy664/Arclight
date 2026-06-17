package io.izzel.arclight.common.bridge.core.world.level.portal;

import org.bukkit.event.player.PlayerTeleportEvent;

public interface TeleportTransitionBridge {

    default void bridge$setTeleportCause(PlayerTeleportEvent.TeleportCause cause) {

    }

    default PlayerTeleportEvent.TeleportCause bridge$getTeleportCause() {
        return null;
    }
}
