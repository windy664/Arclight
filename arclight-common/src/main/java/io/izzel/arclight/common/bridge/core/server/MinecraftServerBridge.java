package io.izzel.arclight.common.bridge.core.server;

import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.TimeSource;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.RemoteConsoleCommandSender;
import org.bukkit.craftbukkit.CraftServer;

public interface MinecraftServerBridge {

    default void bridge$setConsole(ConsoleCommandSender console) {

    }

    default void bridge$setServer(CraftServer server) {

    }

    default CraftServer bridge$getServer() {
        return null;
    }

    default RemoteConsoleCommandSender bridge$getRemoteConsole() {
        return null;
    }

    default void bridge$queuedProcess(Runnable runnable) {

    }

    default void bridge$drainQueuedTasks() {

    }

    default boolean bridge$hasStopped() {
        return false;
    }

    default Commands bridge$getVanillaCommands() {
        return null;
    }

    default void arclight$onServerLoad(ServerLevel level) {

    }

    default void arclight$onServerUnload(ServerLevel level) {

    }

    default void bridge$forge$markLevelsDirty() {}

    default void bridge$forge$reinstatePersistentChunks(ServerLevel level) {}

    default void bridge$forge$lockRegistries() {}

    default void bridge$forge$unlockRegistries() {}

    default void arclight$extendNextTickTimeTo(TimeSource.NanoTimeSource timeSource) {

    }
}
