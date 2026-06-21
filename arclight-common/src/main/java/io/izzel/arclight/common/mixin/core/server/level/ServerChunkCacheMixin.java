package io.izzel.arclight.common.mixin.core.server.level;

import io.izzel.arclight.common.bridge.core.server.level.ServerChunkCacheBridge;
import net.minecraft.server.level.ChunkHolder;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ThreadedLevelLightEngine;
import net.minecraft.util.profiling.Profiler;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.TicketStorage;
import net.minecraft.world.level.storage.SavedDataStorage;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

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
}
