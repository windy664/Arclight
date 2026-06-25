package io.izzel.arclight.common.mixin.core.server.level;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.izzel.arclight.common.bridge.core.server.level.ServerChunkCacheBridge;
import net.minecraft.server.level.ChunkHolder;
import net.minecraft.server.level.ChunkLevel;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.DistanceManager;
import net.minecraft.server.level.FullChunkStatus;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ThreadedLevelLightEngine;
import net.minecraft.util.profiling.Profiler;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.TicketStorage;
import net.minecraft.world.level.gamerules.GameRule;
import net.minecraft.world.level.gamerules.GameRules;
import net.minecraft.world.level.storage.SavedDataStorage;
import org.bukkit.entity.SpawnCategory;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.io.IOException;

@Mixin(ServerChunkCache.class)
public abstract class ServerChunkCacheMixin implements ServerChunkCacheBridge {

    @Shadow
    @Final
    public ChunkMap chunkMap;

    @Shadow
    public abstract void save(boolean flushStorage);

    @Shadow
    @Final
    private SavedDataStorage savedDataStorage;

    @Shadow
    @Final
    private ThreadedLevelLightEngine lightEngine;

    @Shadow
    @Final
    public TicketStorage ticketStorage;

    @Shadow
    abstract boolean runDistanceManagerUpdates();

    @Shadow
    protected abstract void clearCache();

    @Shadow
    @Final
    private DistanceManager distanceManager;

    @Shadow
    @Nullable
    protected abstract ChunkHolder getVisibleChunkIfPresent(long key);

    @Shadow
    @Final
    private ServerLevel level;

    @Invoker("runDistanceManagerUpdates") public abstract boolean bridge$tickDistanceManager();
    @Accessor("lightEngine") public abstract ThreadedLevelLightEngine bridge$getLightManager();

    // CraftBukkit start - properly implement isChunkLoaded
    @Override
    public boolean isChunkLoaded(int chunkX, int chunkZ) {
        ChunkHolder chunk = this.chunkMap.getUpdatingChunkIfPresent(ChunkPos.pack(chunkX, chunkZ));
        if (chunk == null) {
            return false;
        }
        return chunk.getFullChunkNow() != null;
    }
    // CraftBukkit end

    @Override
    public void bridge$setViewDistance(int viewDistance) {
        this.chunkMap.bridge$setViewDistance(viewDistance);
    }

    @Override
    public void bridge$setSimulationDistance(int simDistance) {
        distanceManager.updateSimulationDistance(simDistance);
    }

    @ModifyVariable(method = "getChunkFutureMainThread", index = 4, at = @At("HEAD"), argsOnly = true)
    private boolean arclight$skipLoadIfUnloading(boolean flag, int chunkX, int chunkZ) {
        if (flag) {
            ChunkHolder chunkholder = this.getVisibleChunkIfPresent(ChunkPos.pack(chunkX, chunkZ));
            if (chunkholder != null) {
                FullChunkStatus oldChunkState = ChunkLevel.fullStatus(chunkholder.oldTicketLevel);
                FullChunkStatus currentChunkState = ChunkLevel.fullStatus(chunkholder.getTicketLevel());
                return (oldChunkState.isOrAfter(FullChunkStatus.FULL) && !currentChunkState.isOrAfter(FullChunkStatus.FULL));
            } else {
                return true;
            }
        } else {
            return false;
        }
    }


    @Redirect(method = "tickChunks(Lnet/minecraft/util/profiling/ProfilerFiller;J)V", at = @At(value = "INVOKE", ordinal = 0, target = "Lnet/minecraft/world/level/gamerules/GameRules;get(Lnet/minecraft/world/level/gamerules/GameRule;)Ljava/lang/Object;"))
    private Object arclight$noPlayer(GameRules gameRules, GameRule<Boolean> key) {
        return gameRules.get(key) && !this.level.players().isEmpty();
    }

    @Redirect(method = "tickChunks(Lnet/minecraft/util/profiling/ProfilerFiller;J)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;getGameTime()J"))
    private long arclight$ticksPer(ServerLevel level) {
        long gameTime = level.getGameTime();
        long ticksPer = level.bridge$ticksPerSpawnCategory().getLong(SpawnCategory.ANIMAL);
        return (ticksPer != 0L && gameTime % ticksPer == 0) ? 0 : 1;
    }

    @Override
    public void close(boolean save) throws IOException {
        if (save) {
            this.save(true);
        }
        this.savedDataStorage.close();
        this.lightEngine.close();
        this.chunkMap.close();
    }

    // CraftBukkit start - modelled on below
    @Override
    public void purgeUnload() {
        ProfilerFiller profilerfiller = Profiler.get();

        profilerfiller.push("purge");
        this.ticketStorage.purgeStaleTickets(this.chunkMap);
        this.runDistanceManagerUpdates();
        profilerfiller.popPush("unload");
        this.chunkMap.tick(() -> true);
        profilerfiller.pop();
        this.clearCache();
    }
    // CraftBukkit end

    @Redirect(method = "chunkAbsent", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ChunkHolder;getTicketLevel()I"), require = 0)
    public int arclight$useOldTicketLevel(ChunkHolder chunkHolder) {
        // XXX: Disable for C2ME (#1597)
        return  chunkHolder.bridge$getOldTicketLevel();
    }

    @WrapOperation(method = "tickChunks(Lnet/minecraft/util/profiling/ProfilerFiller;J)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/gamerules/GameRules;get(Lnet/minecraft/world/level/gamerules/GameRule;)Ljava/lang/Object;"))
    private Object arclight$noPlayer(GameRules instance, GameRule<Boolean> gameRule, Operation<Boolean> original) {
        return original.call(instance, gameRule) && !this.level.players().isEmpty();
    }
}
