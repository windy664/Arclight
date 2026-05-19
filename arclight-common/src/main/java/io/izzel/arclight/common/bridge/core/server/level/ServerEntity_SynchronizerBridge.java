package io.izzel.arclight.common.bridge.core.server.level;

import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.server.level.ServerPlayer;

import java.util.function.Predicate;

public interface ServerEntity_SynchronizerBridge {

    default void sendToTrackingPlayersFilteredAndSelf(Packet<? super ClientGamePacketListener> packet, Predicate<ServerPlayer> predicate){

    }
}
