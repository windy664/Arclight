package io.izzel.arclight.common.bridge.core.world.attribute;

import org.bukkit.event.player.PlayerBedEnterEvent;

public interface BedRuleBridge {

    default PlayerBedEnterEvent.BedEnterResult bukkit() {
        return null;
    }

    default void bridge$pushBedEnterResult(PlayerBedEnterEvent.BedEnterResult bukkit) {

    }
}
