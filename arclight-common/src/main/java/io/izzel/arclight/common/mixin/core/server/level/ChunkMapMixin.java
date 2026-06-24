package io.izzel.arclight.common.mixin.core.server.level;

import com.mojang.datafixers.DataFixer;
import io.izzel.arclight.common.bridge.core.server.level.ChunkMapBridge;
import io.izzel.arclight.common.mod.util.ArclightCallbackExecutor;
import it.unimi.dsi.fastutil.longs.Long2ObjectLinkedOpenHashMap;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ChunkHolder;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.GeneratingChunkMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.util.thread.BlockableEventLoop;
import net.minecraft.world.level.TicketStorage;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.ChunkGeneratorStructureState;
import net.minecraft.world.level.chunk.LightChunkGetter;
import net.minecraft.world.level.chunk.status.WorldGenContext;
import net.minecraft.world.level.chunk.storage.RegionStorageInfo;
import net.minecraft.world.level.chunk.storage.SimpleRegionStorage;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.entity.ChunkStatusUpdateListener;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import net.minecraft.world.level.storage.LevelStorageSource;
import org.bukkit.craftbukkit.generator.CustomChunkGenerator;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.nio.file.Path;
import java.util.concurrent.Executor;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

@Mixin(ChunkMap.class)
public abstract class ChunkMapMixin extends SimpleRegionStorage implements ChunkHolder.PlayerProvider, GeneratingChunkMap, ChunkMapBridge {

    @Shadow
    public abstract @Nullable ChunkHolder getUpdatingChunkIfPresent(long key);

    @Shadow
    public volatile Long2ObjectLinkedOpenHashMap<ChunkHolder> visibleChunkMap;

    @Mutable
    @Shadow
    @Final
    private RandomState randomState;

    @Mutable
    @Shadow
    @Final
    private ChunkGeneratorStructureState chunkGeneratorState;

    @Shadow
    @Final
    public ServerLevel level;

    @Mutable
    @Shadow
    @Final
    private WorldGenContext worldGenContext;

    @Shadow
    protected abstract void tick();

    @Invoker("tick") @Override public abstract void bridge$tick(BooleanSupplier hasMoreTime);
    @Invoker("setServerViewDistance") @Override public abstract void bridge$setViewDistance(int i);

    public ChunkMapMixin(RegionStorageInfo info, Path folder, DataFixer fixerUpper, boolean syncWrites, DataFixTypes dataFixType) {
        super(info, folder, fixerUpper, syncWrites, dataFixType);
    }

    // CraftBukkit start - recursion-safe executor for Chunk loadCallback() and unloadCallback()
    public final ArclightCallbackExecutor callbackExecutor = new ArclightCallbackExecutor();

    @Override
    public ArclightCallbackExecutor bridge$getCallbackExecutor() {
        return callbackExecutor;
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    private void arclight$updateRandom(ServerLevel level, LevelStorageSource.LevelStorageAccess levelStorage, DataFixer dataFixer, StructureTemplateManager structureManager, Executor executor, BlockableEventLoop mainThreadExecutor, LightChunkGetter chunkGetter, ChunkGenerator generator, ChunkStatusUpdateListener chunkStatusListener, Supplier overworldDataStorage, TicketStorage ticketStorage, int serverViewDistance, boolean syncWrites, CallbackInfo ci) {
        this.bridge$setChunkGenerator(generator);
    }

    @Redirect(method = "upgradeChunkTag", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;dimension()Lnet/minecraft/resources/ResourceKey;"))
    private ResourceKey<LevelStem> arclight$useTypeKey(ServerLevel serverWorld) {
        return serverWorld.getTypeKey();
    }

    @Override
    public ChunkHolder bridge$chunkHolderAt(long chunkPos) {
        return getUpdatingChunkIfPresent(chunkPos);
    }

    @Override
    public Iterable<ChunkHolder> bridge$getLoadedChunksIterable() {
        return this.visibleChunkMap.values();
    }

    @Override
    public void bridge$tickEntityTracker() {
        this.tick();
    }

    @Override
    public void bridge$setChunkGenerator(ChunkGenerator generator) {
        var rg = generator;
        if (rg instanceof CustomChunkGenerator custom) {
            rg = custom.getDelegate();
        }
        if (rg instanceof NoiseBasedChunkGenerator noise) {
            this.randomState = RandomState.create(noise.generatorSettings().value(), this.level.registryAccess().lookupOrThrow(Registries.NOISE), this.level.getSeed());
        } else {
            this.randomState = RandomState.create(NoiseGeneratorSettings.dummy(), this.level.registryAccess().lookupOrThrow(Registries.NOISE), this.level.getSeed());
        }
        this.chunkGeneratorState = generator.createState(level.registryAccess().lookupOrThrow(Registries.STRUCTURE_SET), this.randomState, level.getSeed());
        var old = this.worldGenContext;
        this.worldGenContext = new WorldGenContext(old.level(), generator, old.structureManager(), old.lightEngine(), old.mainThreadExecutor(), old.unsavedListener());
    }

}
