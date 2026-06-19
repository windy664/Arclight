package io.izzel.arclight.common.bridge.core.network.protocol.common.custom;

import io.netty.buffer.ByteBuf;

public interface DiscardedPayloadBridge {

    default ByteBuf data() {
        return null;
    }

    default void bridge$pushData(ByteBuf buf) {

    }
}
