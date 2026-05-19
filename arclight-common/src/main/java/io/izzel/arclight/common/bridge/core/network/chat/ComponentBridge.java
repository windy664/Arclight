package io.izzel.arclight.common.bridge.core.network.chat;

import net.minecraft.network.chat.Component;

import java.util.stream.Stream;

public interface ComponentBridge extends Iterable<Component>{

    default Stream<Component> stream() {
        return Stream.empty();
    }
}
