package io.izzel.arclight.common.mixin.core.server;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.mojang.datafixers.DataFixer;
import io.izzel.arclight.common.bridge.bukkit.CraftServerBridge;
import io.izzel.arclight.common.bridge.core.server.MinecraftServerBridge;
import io.izzel.arclight.common.mod.mixins.annotation.TransformAccess;
import io.izzel.arclight.common.mod.server.ArclightServer;
import io.izzel.arclight.common.mod.util.ArclightCaptures;
import io.izzel.arclight.common.mod.util.BukkitOptionParser;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import net.minecraft.commands.Commands;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundSetTimePacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.Services;
import net.minecraft.server.TickTask;
import net.minecraft.server.WorldLoader;
import net.minecraft.server.WorldStem;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.progress.LevelLoadListener;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.server.players.PlayerList;
import net.minecraft.util.TimeUtil;
import net.minecraft.util.Util;
import net.minecraft.util.thread.ReentrantBlockableEventLoop;
import net.minecraft.world.clock.ServerClockManager;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.LevelStorageSource;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.craftbukkit.CraftRegistry;
import org.bukkit.craftbukkit.CraftServer;
import org.objectweb.asm.Opcodes;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.lang.management.ManagementFactory;
import java.net.Proxy;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import java.util.function.BooleanSupplier;

@Mixin(MinecraftServer.class)
public abstract class MinecraftServerMixin extends ReentrantBlockableEventLoop<TickTask> implements MinecraftServerBridge {

    // @formatter:off
    @Mutable @Shadow @Final private static long OVERLOADED_THRESHOLD_NANOS;
    @Shadow private boolean mayHaveDelayedTasks;
    @Shadow private long delayedTasksMaxNextTickTimeNanos;
    @Shadow private long nextTickTimeNanos;
    @Shadow private int ticksUntilAutosave;
    @Shadow public abstract Iterable<ServerLevel> getAllLevels();
    @Shadow private PlayerList playerList;
    @Shadow public Map<ResourceKey<Level>, ServerLevel> levels;
    // @formatter:on

    // CraftBukkit start
    public WorldLoader.DataLoadContext worldLoader;
    public org.bukkit.craftbukkit.CraftServer server;
    public OptionSet options;
    public org.bukkit.command.ConsoleCommandSender console;
    @TransformAccess(Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC)
    private static int currentTick = (int) (System.currentTimeMillis() / 50);
    public java.util.Queue<Runnable> processQueue = new java.util.concurrent.ConcurrentLinkedQueue<Runnable>();
    public int autosavePeriod;
    public Commands vanillaCommandDispatcher;
    private boolean forceTicks;
    // CraftBukkit end

    public MinecraftServerMixin(String name, boolean propagatesCrashes) {
        super(name, propagatesCrashes);
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    public void arclight$loadOptions(Thread serverThread, LevelStorageSource.LevelStorageAccess storageSource, PackRepository packRepository, WorldStem worldStem, Optional gameRules, Proxy proxy, DataFixer fixerUpper, Services services, LevelLoadListener levelLoadListener, boolean propagatesCrashes, CallbackInfo ci) {
        OVERLOADED_THRESHOLD_NANOS = 30L * TimeUtil.NANOSECONDS_PER_SECOND / 20L; // CraftBukkit
        String[] arguments = ManagementFactory.getRuntimeMXBean().getInputArguments().toArray(new String[0]);
        OptionParser parser = new BukkitOptionParser();
        try {
            options = parser.parse(arguments);
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.vanillaCommandDispatcher = worldStem.dataPackResources().getCommands();
        this.worldLoader = ArclightCaptures.getDataLoadContext();
        ArclightServer.setMinecraftServer((MinecraftServer) (Object) this);
    }

    // CraftBukkit start
    private boolean hasStopped = false;
    private final Object stopLock = new Object();

    @Override
    public final boolean hasStopped() {
        synchronized (stopLock) {
            return hasStopped;
        }
    }
    // CraftBukkit end

    @Inject(method = "stopServer", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/PacketProcessor;close()V"), cancellable = true)
    private void arclight$preventDoubleStopping(CallbackInfo ci) {
        // CraftBukkit start - prevent double stopping on multiple threads
        synchronized(stopLock) {
            if (hasStopped) return;
            ci.cancel();
            hasStopped = true;
        }
        // CraftBukkit end
    }

    @Inject(method = "stopServer", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerConnectionListener;stop()V"))
    private void arclight$disablePlugins(CallbackInfo ci) {
        // CraftBukkit start
        if (this.server != null) {
            this.server.disablePlugins();
        }
        // CraftBukkit end
    }

    @Inject(method = "stopServer", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/players/PlayerList;removeAll()V"))
    private void arclight$laterStop(CallbackInfo ci) {
        try { Thread.sleep(100); } catch (InterruptedException ex) {} // CraftBukkit - SPIGOT-625 - give server at least a chance to send packets
    }

    @WrapWithCondition(method = "runServer", at = @At(value = "INVOKE", target = "Lorg/slf4j/Logger;warn(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V"))
    private boolean arclight$warnOnLoad(Logger instance, String s, Object o1, Object o2) {
        return server.getWarnOnOverload();
    }

    @Inject(method = "runServer", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/MinecraftServer;startMeasuringTaskExecutionTime()V"))
    private void arclight$updateTickParam(CallbackInfo ci) {
        currentTick = (int) (System.currentTimeMillis() / 50);
    }

    @Inject(method = "tickServer", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/MinecraftServer;tickConnection()V"))
    private void arclight$mainThreadHeartbeatIfTick(BooleanSupplier haveTime, CallbackInfo ci) {
        this.server.getScheduler().mainThreadHeartbeat(); // CraftBukkit
    }

    @WrapWithCondition(method = "tickServer", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/MinecraftServer;autoSave()V"))
    private boolean arclight$checkAutoSave(MinecraftServer instance) {
        return this.autosavePeriod > 0;
    }

    @Inject(method = "autoSave", at = @At(value = "INVOKE", target = "Lorg/slf4j/Logger;debug(Ljava/lang/String;)V"))
    private void arclight$resetAutoSave(CallbackInfo ci) {
        this.ticksUntilAutosave = this.autosavePeriod; // CraftBukkit
    }

    @Inject(method = "tickChildren", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/profiling/ProfilerFiller;push(Ljava/lang/String;)V", ordinal = 0))
    private void arclight$mainThreadHeartbeatIfTickChildren(BooleanSupplier haveTime, CallbackInfo ci) {
        this.server.getScheduler().mainThreadHeartbeat(); // CraftBukkit
    }

    @Redirect(method = "tickChildren", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/clock/ServerClockManager;tick()V"))
    private void arclight$tickClockAllLevels(ServerClockManager instance) {
        // CraftBukkit start
        for (ServerLevel serverlevel : this.getAllLevels()) {
            serverlevel.clockManager().tick();
        }
        // CraftBukkit end
    }

    @Inject(method = "tickChildren", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/MinecraftServer;updateEffectiveRespawnData()V", shift = At.Shift.AFTER))
    private void arclight$runAllTasks(CallbackInfo ci) {
        // CraftBukkit start
        // Run tasks that are waiting on processing
        while (!processQueue.isEmpty()) {
            processQueue.remove().run();
        }
        // CraftBukkit end
    }

    @Redirect(method = "forceGameTimeSynchronization", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/players/PlayerList;broadcastAll(Lnet/minecraft/network/protocol/Packet;)V"))
    private void arclight$broadcastAllLevels(PlayerList instance, Packet<?> packet) {
        // CraftBukkit start
        for (ServerLevel serverlevel : this.getAllLevels()) {
            this.playerList.broadcastAll(new ClientboundSetTimePacket(serverlevel.getGameTime(), Map.of() /*, serverLevel*/));
        }
        // CraftBukkit end
    }

    @Inject(method = "lambda$reloadResources$4", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/packs/repository/PackRepository;setSelected(Ljava/util/Collection;)V"))
    private void arclight$syncCommand(Collection packsToEnable, MinecraftServer.ReloadableResources newResources, CallbackInfo ci) {
        this.server.syncCommands();
    }

    // CraftBukkit start
    @Override
    public boolean isDebugging() {
        return false;
    }

    @Deprecated
    @TransformAccess(Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC)
    private static MinecraftServer getServer() {
        return (Bukkit.getServer() instanceof CraftServer) ? ((CraftServer) Bukkit.getServer()).getServer() : null;
    }

    @Deprecated
    @TransformAccess(Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC)
    private static RegistryAccess getDefaultRegistryAccess() {
        return CraftRegistry.getMinecraftRegistry();
    }
    // CraftBukkit end

    // CraftBukkit start
    public final java.util.concurrent.ExecutorService chatExecutor = java.util.concurrent.Executors.newCachedThreadPool(
            new com.google.common.util.concurrent.ThreadFactoryBuilder().setDaemon(true).setNameFormat("Async Chat Thread - #%d").build());
    // CraftBukkit end

    @Override
    public java.util.concurrent.ExecutorService bridge$getChatExecutor() {
        return this.chatExecutor;
    }

    // bukkit callbacks
    @Override
    public void addLevel(ServerLevel level) {
        this.levels.put(level.dimension(), level);
        this.arclight$onServerLoad(level);
        this.bridge$forge$markLevelsDirty();
    }

    @Override
    public void removeLevel(ServerLevel level) {
        this.levels.remove(level.dimension());
        this.arclight$onServerUnload(level);
        this.bridge$forge$markLevelsDirty();
        ((CraftServerBridge) Bukkit.getServer()).bridge$removeWorld(level);
    }

    /**
     * @author wdog5
     * @reason Bukkit
     */
    @Overwrite
    private boolean haveTime() {
        return this.forceTicks || this.runningTask() || Util.getNanos() < (this.mayHaveDelayedTasks ? this.delayedTasksMaxNextTickTimeNanos : this.nextTickTimeNanos);
    }

    private void executeModerately() {
        this.runAllTasks();
        java.util.concurrent.locks.LockSupport.parkNanos("executing tasks", 1000L);
        // CraftBukkit end
    }

    @Override
    public WorldLoader.DataLoadContext bridge$getWorldLoader() {
        return this.worldLoader;
    }

    @Override
    public void bridge$setWorldLoader(WorldLoader.DataLoadContext worldLoader) {
        this.worldLoader = worldLoader;
    }

    @Override
    public CraftServer bridge$getServer() {
        return this.server;
    }

    @Override
    public void bridge$setServer(CraftServer server) {
        this.server = server;
    }

    @Override
    public OptionSet bridge$getOptions() {
        return this.options;
    }

    @Override
    public void bridge$setOptions(OptionSet options) {
        this.options = options;
    }

    @Override
    public ConsoleCommandSender bridge$getConsole() {
        return this.console;
    }

    @Override
    public void bridge$setConsole(ConsoleCommandSender console) {
        this.console = console;
    }

    @Override
    public java.util.Queue<Runnable> bridge$getProcessQueue() {
        return this.processQueue;
    }

    @Override
    public void bridge$setProcessQueue(Queue<Runnable> processQueue) {
        this.processQueue = processQueue;
    }

    @Override
    public int bridge$getAutosavePeriod() {
        return this.autosavePeriod;
    }

    @Override
    public void bridge$setAutosavePeriod(int autosavePeriod) {
        this.autosavePeriod = autosavePeriod;
    }

    @Override
    public void bridge$setForceTicks(boolean forceTicks) {
        this.forceTicks = forceTicks;
    }

    @Override
    public boolean bridge$isForceTicks() {
        return this.forceTicks;
    }

    @Override
    public Commands bridge$getVanillaCommandDispatcher() {
        return this.vanillaCommandDispatcher;
    }

    @Override
    public void bridge$setVanillaCommandDispatcher(Commands vanillaCommandDispatcher) {
        this.vanillaCommandDispatcher = vanillaCommandDispatcher;
    }
}
