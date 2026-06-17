package io.izzel.arclight.common.bridge.core.server.network;

import com.mojang.authlib.GameProfile;
import io.izzel.arclight.common.mod.util.ArclightCustomQueryAnswerPayload;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.login.ServerboundCustomQueryAnswerPacket;

public interface ServerLoginPacketListenerImplBridge {
    default Thread bridge$newHandleThread(String name, Runnable runnable) {
        return new Thread(runnable, name);
    }

    default int bridge$getVelocityLoginId() {
        return 0;
    }

    default void bridge$preLogin(GameProfile authenticatedProfile) throws Exception {

    }

    default void bridge$disconnect(String reason) {

    }

    default FriendlyByteBuf arclight$platform$customQAData(ServerboundCustomQueryAnswerPacket packet) {
        return ArclightCustomQueryAnswerPayload.tryUnwrap(packet.payload());
    }

    default void arclight$platform$onCustomQA(ServerboundCustomQueryAnswerPacket payload) {}
}
