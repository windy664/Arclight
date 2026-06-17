package io.izzel.arclight.common.bridge.core.server.network;

import net.minecraft.server.level.ServerPlayer;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.entity.CraftPlayer;

public interface ServerCommonPacketListenerImplBridge {

    default boolean bridge$processedDisconnect() {
        return false;
    }

    default boolean bridge$isDisconnected() {
        return false;
    }

    default void bridge$disconnect(String s) {

    }

    default CraftServer bridge$getCraftServer() {
        return null;
    }

    default CraftPlayer bridge$getCraftPlayer() {
        return null;
    }

    default ServerPlayer bridge$getPlayer() {
        return null;
    }

    default void bridge$setPlayer(ServerPlayer player) {

    }

}
