package io.izzel.arclight.common.bridge.core.world.clock;

import net.minecraft.core.Holder;
import net.minecraft.network.protocol.game.ClientboundSetTimePacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.clock.ClockTimeMarker;
import net.minecraft.world.clock.WorldClock;
import org.bukkit.event.world.TimeSkipEvent;

public interface ServerClockManagerBridge {

    default void setTotalTicks(Holder<WorldClock> clock, long totalTicks, TimeSkipEvent.SkipReason reason) {

    }

    default boolean moveToTimeMarker(Holder<WorldClock> clock, ResourceKey<ClockTimeMarker> timeMarkerId, TimeSkipEvent.SkipReason reason) {
        return false;
    }

    default ClientboundSetTimePacket createFullSyncPacket(ServerPlayer player) {
        return null;
    }
}
