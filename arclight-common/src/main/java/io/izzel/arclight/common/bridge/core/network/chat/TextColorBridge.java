package io.izzel.arclight.common.bridge.core.network.chat;

import net.minecraft.ChatFormatting;

public interface TextColorBridge {

    default ChatFormatting bridge$getFormat(){
        return null;
    }

    default void bridge$setFormat(ChatFormatting format) {

    }
}
