package io.izzel.arclight.common.bridge.core.server;

import joptsimple.OptionSet;
import net.minecraft.commands.Commands;
import net.minecraft.server.WorldLoader;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.TimeSource;
import net.minecraft.world.level.levelgen.WorldOptions;
import net.minecraft.world.level.storage.LevelData;
import net.minecraft.world.level.storage.ServerLevelData;
import net.minecraft.world.level.storage.WorldData;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.RemoteConsoleCommandSender;
import org.bukkit.craftbukkit.CraftServer;

public interface MinecraftServerBridge {

    default void bridge$setConsole(ConsoleCommandSender console) {

    }

    default ConsoleCommandSender bridge$getConsole() {
        return null;
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

    default WorldLoader.DataLoadContext bridge$getWorldLoader() {
        return null;
    }

    default void bridge$setWorldLoader(WorldLoader.DataLoadContext worldLoader) {

    }

    default OptionSet bridge$getOptions() {
        return null;
    }

    default void bridge$setOptions(OptionSet options) {

    }

    default java.util.Queue<Runnable> bridge$getProcessQueue() {
        return null;
    }

    default void bridge$setProcessQueue(java.util.Queue<Runnable> processQueue) {

    }

    default int bridge$getAutosavePeriod() {
        return 0;
    }

    default void bridge$setAutosavePeriod(int autosavePeriod) {

    }

    default void bridge$setForceTicks(boolean forceTicks) {

    }

    default boolean bridge$isForceTicks() {
        return false;
    }

    default Commands bridge$getVanillaCommandDispatcher() {
        return null;
    }

    default void bridge$setVanillaCommandDispatcher(Commands vanillaCommandDispatcher) {
    }

    default boolean hasStopped() {
        return false;
    }

    default void addLevel(ServerLevel level) {

    }

    default void removeLevel(ServerLevel level) {

    }

    default boolean isDebugging() {
        return false;
    }

    default java.util.concurrent.ExecutorService bridge$getChatExecutor() {
        return null;
    }

    default ServerLevel findRespawnDimension(ServerLevel world) {
        return null;
    }

    default void setRespawnData(LevelData.RespawnData respawnData, ServerLevel world) {
    }

    default void arclight$tickSpigotWatchdogInternal() {
    }

    default void initWorld(ServerLevel serverWorld, ServerLevelData worldInfo, WorldData saveData, WorldOptions worldOptions) {
    }

    default void prepareLevels(ServerLevel serverWorld) {
    }
}
