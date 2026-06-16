package io.izzel.arclight.common.bridge.core.network;

import com.mojang.authlib.properties.Property;

import java.net.SocketAddress;
import java.util.UUID;

public interface ConnectionBridge {

    default UUID bridge$getSpoofedUUID() {
        return null;
    }

    default void bridge$setSpoofedUUID(UUID spoofedUUID) {

    }

    default Property[] bridge$getSpoofedProfile() {
        return null;
    }

    default void bridge$setSpoofedProfile(Property[] spoofedProfile) {

    }

    default String bridge$getHostname() {
        return null;
    }

    default void bridge$setHostname(String hostname) {

    }
}
