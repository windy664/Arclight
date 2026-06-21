package io.izzel.arclight.common.mixin.core.server;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.datafixers.DataFixer;
import io.izzel.arclight.api.ArclightVersion;
import io.izzel.arclight.common.bridge.bukkit.CraftServerBridge;
import io.izzel.arclight.common.bridge.core.server.MinecraftServerBridge;
import io.izzel.arclight.common.mod.mixins.annotation.TransformAccess;
import io.izzel.arclight.common.mod.server.ArclightServer;
import io.izzel.arclight.common.mod.server.world.border.ArclightBorderChangeListener;
import io.izzel.arclight.common.mod.server.world.border.ArclightDelegatedBorderListener;
import io.izzel.arclight.common.mod.util.ArclightCaptures;
import io.izzel.arclight.common.mod.util.BukkitOptionParser;
import io.izzel.arclight.common.util.IteratorUtil;
import io.izzel.arclight.i18n.ArclightConfig;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import net.minecraft.CrashReport;
import net.minecraft.ReportedException;
import net.minecraft.commands.Commands;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundSetDefaultSpawnPositionPacket;
import net.minecraft.network.protocol.game.ClientboundSetTimePacket;
import net.minecraft.network.protocol.status.ServerStatus;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.Services;
import net.minecraft.server.TickTask;
import net.minecraft.server.WorldLoader;
import net.minecraft.server.WorldStem;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.progress.LevelLoadListener;
import net.minecraft.server.notifications.NotificationManager;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.server.players.PlayerList;
import net.minecraft.util.TimeSource;
import net.minecraft.util.TimeUtil;
import net.minecraft.util.Util;
import net.minecraft.util.thread.ReentrantBlockableEventLoop;
import net.minecraft.world.clock.ServerClockManager;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.WorldOptions;
import net.minecraft.world.level.storage.LevelData;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.level.storage.ServerLevelData;
import net.minecraft.world.level.storage.WorldData;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.craftbukkit.CraftRegistry;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.scoreboard.CraftScoreboardManager;
import org.bukkit.craftbukkit.util.CraftLocation;
import org.bukkit.event.server.ServerLoadEvent;
import org.bukkit.event.world.SpawnChangeEvent;
import org.bukkit.event.world.WorldInitEvent;
import org.bukkit.plugin.PluginLoadOrder;
import org.jspecify.annotations.Nullable;
import org.objectweb.asm.Opcodes;
import org.slf4j.Logger;
import org.spigotmc.WatchdogThread;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.lang.management.ManagementFactory;
import java.net.Proxy;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;
import java.util.function.BooleanSupplier;

@Mixin(MinecraftServer.class)
public abstract class MinecraftServerMixin extends ReentrantBlockableEventLoop<TickTask> implements MinecraftServerBridge {

    // @formatter:off
    @Mutable @Shadow @Final private static long OVERLOADED_THRESHOLD_NANOS;
    @Shadow private long nextTickTimeNanos;
    @Shadow private int ticksUntilAutosave;
    @Shadow public abstract Iterable<ServerLevel> getAllLevels();
    @Shadow private PlayerList playerList;
    @Shadow public Map<ResourceKey<Level>, ServerLevel> levels;
    @Shadow public abstract @Nullable ServerLevel getLevel(ResourceKey<Level> dimension);
    @Shadow public abstract ServerLevel overworld();
    @Shadow public abstract PlayerList getPlayerList();
    @Shadow protected abstract void updateEffectiveRespawnData();
    @Shadow private static void setInitialSpawn(ServerLevel level, ServerLevelData levelData, boolean spawnBonusChest, boolean isDebug, LevelLoadListener levelLoadListener) {}
    @Shadow @Final public LevelLoadListener levelLoadListener;
    @Shadow protected abstract void setupDebugLevel(WorldData worldData);
    @Shadow public WorldData worldData;
    // @formatter:on

    @Shadow
    public abstract int getAbsoluteMaxWorldSize();

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
    public final double[] recentTps = new double[3];
    @Unique
    private long arclight$tpsTickSection;
    @Unique
    private long arclight$tpsTickCount;
    private static final int TPS = 20;
    private static final int TICK_TIME = 1000000000 / TPS;
    private static final int SAMPLE_INTERVAL = 100;

    public MinecraftServerMixin(String name, boolean propagatesCrashes) {
        super(name, propagatesCrashes);
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    public void arclight$loadOptions(Thread serverThread, LevelStorageSource.LevelStorageAccess storageSource, PackRepository packRepository, WorldStem worldStem, Optional gameRules, Proxy proxy, DataFixer fixerUpper, Services services, LevelLoadListener levelLoadListener, boolean propagatesCrashes, NotificationManager notificationManager, CallbackInfo ci) {
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

    @WrapOperation(method = "runServer", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/MinecraftServer;buildServerStatus()Lnet/minecraft/network/protocol/status/ServerStatus;"))
    private ServerStatus arclight$initTickParam(MinecraftServer instance, Operation<ServerStatus> original) {
        var serverStatus = original.call(instance);
        Arrays.fill(recentTps, 20);
        this.arclight$tpsTickSection = Util.getMillis();
        this.arclight$tpsTickCount = 1;
        return serverStatus;
    }

    @WrapOperation(method = "runServer", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/MinecraftServer;waitUntilNextTick()V"))
    private void arclight$updateTickParam(MinecraftServer instance, Operation<Void> original, @Local long tickSection, @Local long tickCount) {
        if (tickCount++ % SAMPLE_INTERVAL == 0) {
            long curTime = Util.getMillis();
            double currentTps = 1E3 / (curTime - tickSection) * SAMPLE_INTERVAL;
            recentTps[0] = calcTps(recentTps[0], 0.92, currentTps); // 1/exp(5sec/1min)
            recentTps[1] = calcTps(recentTps[1], 0.9835, currentTps); // 1/exp(5sec/5min)
            recentTps[2] = calcTps(recentTps[2], 0.9945, currentTps); // 1/exp(5sec/15min)
            tickSection = curTime;
        }
        currentTick = (int) (System.currentTimeMillis() / 50);
        original.call(instance);
    }

    private static double calcTps(double avg, double exp, double tps) {
        return (avg * exp) + (tps * (1 - exp));
    }

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

    @Inject(method = "runServer", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/MinecraftServer;onServerExit()V"))
    private void arclight$watchdogExit(CallbackInfo ci) {
        WatchdogThread.doStop();
    }

    @WrapOperation(method = "createLevels", at = @At(value = "INVOKE", target = "Ljava/util/Set;iterator()Ljava/util/Iterator;"))
    private Iterator<Map.Entry<ResourceKey<LevelStem>, LevelStem>> arclight$skipBukkitLevels(Set<Map.Entry<ResourceKey<LevelStem>, LevelStem>> instance, Operation<Iterator<Map.Entry<ResourceKey<LevelStem>, LevelStem>>> original) {
        final Iterator<Map.Entry<ResourceKey<LevelStem>, LevelStem>> iterator = original.call(instance);
        if (ArclightConfig.spec().getExperimental().canOverrideWorldgen()) {
            return IteratorUtil.filter(iterator, it -> {
                final var location = it.getKey().identifier();
                if (location.getNamespace().equals("bukkit")) {
                    ArclightServer.LOGGER.info("Deferred {} custom dimension creation", location);
                    return false;
                } else {
                    return true;
                }
            });
        } else {
            return iterator;
        }
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

    @WrapOperation(method = "createLevels", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/players/PlayerList;addWorldborderListener(Lnet/minecraft/server/level/ServerLevel;)V"))
    private void arclight$configurableDelegatedListener(PlayerList instance, ServerLevel level, Operation<Void> original) throws Throwable {
        // Arclight: move world border listener initialization to world registration
        if (ArclightDelegatedBorderListener.isEnabled()) {
            original.call(instance, level);
        }
    }

    @WrapOperation(method = "createLevels", at = @At(value = "INVOKE", remap = false, target = "Ljava/util/Map;put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;"))
    private Object arclight$worldInit(Map<Object, Object> instance, Object k, Object v, Operation<Object> original){
        if (v instanceof ServerLevel level) {
            if (((CraftServer) Bukkit.getServer()).scoreboardManager == null) {
                ((CraftServer) Bukkit.getServer()).scoreboardManager = new CraftScoreboardManager((MinecraftServer) (Object) this, level.getScoreboard());
            }
            if (level.bridge$getGenerator() != null) {
                level.getWorld().getPopulators().addAll(
                         level.bridge$getGenerator().getDefaultPopulators(
                                level.getWorld()));
            }
            Bukkit.getPluginManager().callEvent(new WorldInitEvent(level.getWorld()));

            // Arclight: move world border listener initialization to world registration
            // Arclight: ArclightBorderChangeListener is singleton so won't be added more than once
            // Arclight: since it seems that we can't apply multiple Decorators to a target on Forge...
            level.getWorldBorder().addListener(ArclightBorderChangeListener.typed());
        }
        return original.call(instance, k, v);
    }

    private void initWorldBorder(ServerLevel serverlevel1) {
        serverlevel1.getWorldBorder().setAbsoluteMaxSize(this.getAbsoluteMaxWorldSize());
        this.getPlayerList().addWorldborderListener(serverlevel1);
    }

    @Redirect(method = "forceGameTimeSynchronization", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/players/PlayerList;broadcastAll(Lnet/minecraft/network/protocol/Packet;)V"))
    private void arclight$broadcastAllLevels(PlayerList instance, Packet<?> packet) {
        // CraftBukkit start
        for (ServerLevel serverlevel : this.getAllLevels()) {
            this.playerList.broadcastAll(new ClientboundSetTimePacket(serverlevel.getGameTime(), Map.of() /*, serverLevel*/));
        }
        // CraftBukkit end
    }

    @Inject(method = "getServerModName", remap = false, cancellable = true, at = @At("RETURN"))
    private void arclight$brand(CallbackInfoReturnable<String> cir) {
        cir.setReturnValue(cir.getReturnValue() + " arclight/" + ArclightVersion.current().getReleaseName());
    }

    @Inject(method = "lambda$reloadResources$4", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/packs/repository/PackRepository;setSelected(Ljava/util/Collection;)V"))
    private void arclight$syncCommand(Collection packsToEnable, MinecraftServer.ReloadableResources newResources, CallbackInfo ci) {
        this.server.syncCommands();
    }

    private boolean arclight$skipWatchdogSetTime = false;

    @Override
    public void arclight$extendNextTickTimeTo(TimeSource.NanoTimeSource timeSource) {
        if (!arclight$skipWatchdogSetTime) {
            this.nextTickTimeNanos = timeSource.getAsLong();
        }
    }

    @Override
    public void arclight$tickSpigotWatchdogInternal() {
        try {
            arclight$skipWatchdogSetTime = true;
            WatchdogThread.tick();
        } finally {
            arclight$skipWatchdogSetTime = false;
        }
    }

    @Inject(method = "tickServer", at = @At("HEAD"))
    private void arclight$tickWatchdog(CallbackInfo ci) {
        arclight$tickSpigotWatchdogInternal();
    }

    // CraftBukkit start
    @Override
    public boolean isDebugging() {
        return false;
    }

    // bukkit methods
    public void initWorld(ServerLevel serverWorld, ServerLevelData worldInfo, WorldData saveData, WorldOptions worldOptions) {
        boolean flag = saveData.isDebugWorld();
        if (serverWorld.bridge$getGenerator() != null) {
            serverWorld.getWorld().getPopulators().addAll(
                    serverWorld.bridge$getGenerator().getDefaultPopulators(
                            serverWorld.getWorld()));
        }
        WorldBorder worldborder = serverWorld.getWorldBorder();
        worldborder.applyInitialSettings(0L);

        // Arclight: move world border listener initialization to world registration
        playerList.addWorldborderListener(serverWorld);

        // Call WorldInitEvent for Bukkit created world
        // Before any chunk is loaded/generated.
        // This makes delayed configurate possible.
        // Calling multiple times is OK since Spigot also do so.
        // See [PlotSquared] BukkitSetupUtils#setupWorld(PlotAreaBuilder).
        // See CraftServer.
        // CraftBukkit - SPIGOT-5569: Call WorldInitEvent before any chunks are generated
        this.server.getPluginManager().callEvent(new WorldInitEvent(serverWorld.getWorld()));

        if (!worldInfo.isInitialized()) {
            try {
                setInitialSpawn(serverWorld, worldInfo, worldOptions.generateBonusChest(), flag, this.levelLoadListener);
                worldInfo.setInitialized(true);
                if (flag) {
                    this.setupDebugLevel(this.worldData);
                }
            } catch (Throwable throwable) {
                CrashReport crashreport = CrashReport.forThrowable(throwable, "Exception initializing level");
                try {
                    serverWorld.fillReportDetails(crashreport);
                } catch (Throwable throwable2) {
                    // empty catch block
                }
                throw new ReportedException(crashreport);
            }
            worldInfo.setInitialized(true);
        }
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


    @ModifyReturnValue(method = "haveTime", at = @At("RETURN"))
    private boolean arclight$checkForceTicks(boolean original) {
        return this.forceTicks || original;
    }

    private void executeModerately() {
        this.runAllTasks();
        this.bridge$drainQueuedTasks();
        java.util.concurrent.locks.LockSupport.parkNanos("executing tasks", 1000L);
    }

    @Override
    public void bridge$drainQueuedTasks() {
        while (!processQueue.isEmpty()) {
            processQueue.remove().run();
        }
    }

    @Inject(method = "createLevels", at = @At("RETURN"))
    public void arclight$enablePlugins(CallbackInfo ci) {
        this.bridge$forge$unlockRegistries();
        this.server.enablePlugins(PluginLoadOrder.POSTWORLD);
        this.bridge$forge$lockRegistries();
        this.server.getPluginManager().callEvent(new ServerLoadEvent(ServerLoadEvent.LoadType.STARTUP));
    }

    @Redirect(method = "findRespawnDimension", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/storage/ServerLevelData;getRespawnData()Lnet/minecraft/world/level/storage/LevelData$RespawnData;"))
    private LevelData.RespawnData arclight$resetRespawnData(ServerLevelData instance) {
        return this.overworld().getRespawnData();
    }

    @Redirect(method = "setRespawnData", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/storage/WorldData;overworldData()Lnet/minecraft/world/level/storage/ServerLevelData;"))
    private ServerLevelData arclight$resetServerLevelData(WorldData instance) {
        return this.overworld().serverLevelData;
    }

    @Inject(method = "setRespawnData", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/players/PlayerList;broadcastAll(Lnet/minecraft/network/protocol/Packet;)V"))
    private void arclight$callSpawnChangeEvent(LevelData.RespawnData respawnData, CallbackInfo ci, @Local(ordinal = 1) LevelData.RespawnData oldRespawnData) {
        // CraftBukkit start - Notify anyone who's listening.
        SpawnChangeEvent event = new SpawnChangeEvent(this.overworld().getWorld(), CraftLocation.toBukkit(oldRespawnData.pos(), this.overworld().getWorld(), oldRespawnData.yaw(), oldRespawnData.pitch()));
        this.server.getPluginManager().callEvent(event);
        // CraftBukkit end
    }

    @Override
    public ServerLevel findRespawnDimension(ServerLevel world) {
        if (world == null) {
            return null;
        }
        LevelData.RespawnData respawnData = world.getRespawnData();
        ResourceKey<Level> respawnDimension = respawnData.dimension();
        ServerLevel respawnLevel = this.getLevel(respawnDimension);
        return respawnLevel != null ? respawnLevel : this.overworld();
    }

    @Override
    public void setRespawnData(final LevelData.RespawnData respawnData, ServerLevel world) {
        ServerLevelData levelData = world.serverLevelData;
        LevelData.RespawnData oldRespawnData = levelData.getRespawnData();
        if (!oldRespawnData.equals(respawnData)) {
            // CraftBukkit start - Notify anyone who's listening.
            SpawnChangeEvent event = new SpawnChangeEvent(world.getWorld(), CraftLocation.toBukkit(oldRespawnData.pos(), world.getWorld(), oldRespawnData.yaw(), oldRespawnData.pitch()));
            this.server.getPluginManager().callEvent(event);
            // CraftBukkit end
            levelData.setSpawn(respawnData);
            this.getPlayerList().broadcastAll(new ClientboundSetDefaultSpawnPositionPacket(respawnData));
            this.updateEffectiveRespawnData();
        }
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
