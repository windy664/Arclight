package io.izzel.arclight.common.mixin.core.server.level;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import io.izzel.arclight.common.bridge.bukkit.CraftServerBridge;
import io.izzel.arclight.common.bridge.core.server.level.ServerLevelBridge;
import io.izzel.arclight.common.bridge.core.world.level.levelgen.flat.FlatLevelGeneratorSettingsBridge;
import io.izzel.arclight.common.mixin.core.world.level.LevelMixin;
import io.izzel.arclight.common.mod.mixins.annotation.CreateConstructor;
import io.izzel.arclight.common.mod.mixins.annotation.ShadowConstructor;
import io.izzel.arclight.common.mod.server.ArclightServer;
import io.izzel.arclight.common.mod.util.DelegateWorldInfo;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.bossevents.CustomBossEvents;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerEntityGetter;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.RandomSequences;
import net.minecraft.world.clock.ServerClockManager;
import net.minecraft.world.level.CustomSpawner;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.gamerules.GameRuleMap;
import net.minecraft.world.level.gamerules.GameRules;
import net.minecraft.world.level.levelgen.FlatLevelSource;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.WorldGenSettings;
import net.minecraft.world.level.saveddata.WeatherData;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.level.storage.PrimaryLevelData;
import net.minecraft.world.level.storage.SavedDataStorage;
import net.minecraft.world.level.storage.ServerLevelData;
import net.minecraft.world.level.timers.TimerQueue;
import org.bukkit.World;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.generator.CustomChunkGenerator;
import org.bukkit.craftbukkit.generator.CustomWorldChunkManager;
import org.bukkit.craftbukkit.util.WorldUUID;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executor;

@Mixin(ServerLevel.class)
public abstract class ServerLevelMixin extends LevelMixin implements WorldGenLevel, ServerEntityGetter, ServerLevelBridge {

    @Shadow
    @Final
    public ServerLevelData serverLevelData;
    @Shadow
    @Final
    private ServerChunkCache chunkSource;

    @Shadow
    public abstract WeatherData getWeatherData();

    @SuppressWarnings({"FieldCanBeLocal", "unused"})
    public PrimaryLevelData K; // Stupid CraftBukkit patch.
    public LevelStorageSource.LevelStorageAccess storageSource;
    public UUID uuid;
    public ResourceKey<LevelStem> typeKey;

    // CraftBukkit start
    // Moved from MinecraftServer start
    private SavedDataStorage savedDataStorage;
    private GameRules gameRules;
    private WorldGenSettings worldGenSettings;
    private CustomBossEvents customBossEvents;
    private RandomSequences randomSequences;
    private WeatherData weatherData;
    private TimerQueue<MinecraftServer> scheduledEvents;
    private ServerClockManager clockManager;

    @Override
    public WorldGenSettings getWorldGenSettings() {
        return this.worldGenSettings;
    }

    @Override
    public CustomBossEvents getCustomBossEvents() {
        return this.customBossEvents;
    }

    @Override
    public RandomSource getRandomSequence(Identifier key) {
        return this.randomSequences.get(key, this.worldGenSettings.options().seed());
    }

    @Override
    public RandomSequences getRandomSequences() {
        return this.randomSequences;
    }

    @Override
    public void setWeatherParameters(int clearTime, int rainTime, boolean raining, boolean thundering) {
        WeatherData weatherdata = this.getWeatherData();

        weatherdata.setClearWeatherTime(clearTime);
        weatherdata.setRainTime(rainTime);
        weatherdata.setThunderTime(rainTime);
        weatherdata.setRaining(raining);
        weatherdata.setThundering(thundering);
    }

    @Override
    public TimerQueue<MinecraftServer> getScheduledEvents() {
        return this.scheduledEvents;
    }
    // Moved from MinecraftServer end

    @Override
    public LevelChunk getChunkIfLoaded(int x, int z) {
        return this.chunkSource.getChunk(x, z, false);
    }

    @Override
    public ResourceKey<LevelStem> getTypeKey() {
        return this.typeKey;
    }

    @ShadowConstructor
    public void arclight$constructor(final MinecraftServer server, final Executor executor, final LevelStorageSource.LevelStorageAccess levelStorage, final ServerLevelData levelData, final ResourceKey<Level> dimension, final LevelStem levelStem, final boolean isDebug, final long biomeZoomSeed, final List<CustomSpawner> customSpawners, final boolean tickTime) {
        throw new RuntimeException();
    }

    @CreateConstructor
    public void arclight$constructor(final MinecraftServer server, final Executor executor, final LevelStorageSource.LevelStorageAccess levelStorage, final ServerLevelData levelData, final ResourceKey<Level> dimension, final LevelStem levelStem, final boolean isDebug, final long biomeZoomSeed, final List<CustomSpawner> customSpawners, final boolean tickTime, org.bukkit.World.Environment env, org.bukkit.generator.ChunkGenerator gen, org.bukkit.generator.BiomeProvider biomeProvider) {
        var craftBridge = (CraftServerBridge) (Object) server.bridge$getServer();
        assert craftBridge != null;
        craftBridge.bridge$offerEnvironmentCache(levelData.getLevelName(), env);
        craftBridge.bridge$offerGeneratorCache(levelData.getLevelName(), gen);
        craftBridge.bridge$offerBiomeProviderCache(levelData.getLevelName(), biomeProvider);
        arclight$constructor(server, executor, levelStorage, levelData, dimension, levelStem, isDebug, biomeZoomSeed, customSpawners, tickTime);
    }

    @Inject(method = "<init>", at = @At("CTOR_HEAD"))
    private void arclight$init(MinecraftServer server, Executor executor, LevelStorageSource.LevelStorageAccess levelStorage, ServerLevelData levelData, ResourceKey dimension, LevelStem levelStem, boolean isDebug, long biomeZoomSeed, List customSpawners, boolean tickTime, CallbackInfo ci) {
        storageSource = levelStorage;
        uuid = WorldUUID.getUUID(levelStorage.levelDirectory.path().toFile());
        // CraftBukkit end
    }

    // Support custom chunk generator; in consistency with CraftBukkit
    // The real part is inside ServerChunkCache, when initializing ChunkMap (in ctor).
    // A generator state is created, which is later used for chunk generation.
    // Previously we didn't modify it before ChunkMap is created,
    // which in turn cause custom world generation from Bukkit failing to work.
    @WrapOperation(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/dimension/LevelStem;generator()Lnet/minecraft/world/level/chunk/ChunkGenerator;"))
    private ChunkGenerator arclight$initChunkGenerator(LevelStem instance, Operation<ChunkGenerator> original, @Local MinecraftServer server, @Local ServerLevelData worldInfo) throws Throwable {
        // Pulling up world info init since level info is used when selecting ChunkGenerator.
        if (arclight$isActual() && worldInfo instanceof PrimaryLevelData primary) {
            this.K = primary;
        } else {
            // damn spigot again
            this.K = DelegateWorldInfo.wrap(worldInfo);
        }

        if (arclight$isActual()) {
            var craftBridge = (CraftServerBridge) (Object) server.bridge$getServer();

            this.biomeProvider = craftBridge.bridge$consumeBiomeProviderCache(worldInfo.getLevelName());
            this.generator = craftBridge.bridge$consumeGeneratorCache(worldInfo.getLevelName());
            this.environment = craftBridge.bridge$consumeEnvironmentCache(worldInfo.getLevelName());

            if (this.environment == null) {
                // Select world environment for vanilla/mod world creation
                if (instance.type().is(LevelStem.OVERWORLD.identifier())) {
                    this.environment = World.Environment.NORMAL;
                } else if (instance.type().is(LevelStem.NETHER.identifier())) {
                    this.environment = World.Environment.NETHER;
                } else if (instance.type().is(LevelStem.END.identifier())) {
                    this.environment = World.Environment.THE_END;
                } else {
                    // Don't use CUSTOM; it's not even supported in Multiverse
                    // this.environment = World.Environment.CUSTOM;
                    this.environment = World.Environment.NORMAL;
                }
            }

            // Now we create the CraftWorld
            this.world = new CraftWorld((ServerLevel) (Object) this, generator, biomeProvider, environment);
        }

        ChunkGenerator raw = original.call(instance);
        if (arclight$isActual()) {
            // Data needed by getWorld() are all initialized for possible creating CraftWorld.
            // CraftBukkit start: select custom chunk generator
            if (biomeProvider != null) {
                BiomeSource biomeSource = new CustomWorldChunkManager(getWorld(), biomeProvider, getServer().registryAccess().lookupOrThrow(Registries.BIOME));
                if (raw instanceof NoiseBasedChunkGenerator noise) {
                    raw = new NoiseBasedChunkGenerator(biomeSource, noise.settings);
                } else if (raw instanceof FlatLevelSource flat) {
                    raw = new FlatLevelSource(((FlatLevelGeneratorSettingsBridge) flat.settings()).bridge$withBiomeSource(biomeSource));
                } else {
                    ArclightServer.LOGGER.warn("Level {} has unknown customized generator -- requested biome provider won't be satisfied.", this.serverLevelData.getLevelName());
                }
            }
            if (generator != null) {
                raw = new CustomChunkGenerator((ServerLevel) (Object) this, raw, generator);
            }
            // CraftBukkit end
        }
        return raw;
    }
}

