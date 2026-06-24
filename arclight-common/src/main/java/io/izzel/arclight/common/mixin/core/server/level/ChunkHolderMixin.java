package io.izzel.arclight.common.mixin.core.server.level;

import com.llamalad7.mixinextras.sugar.Local;
import io.izzel.arclight.common.bridge.core.server.level.ChunkHolderBridge;
import it.unimi.dsi.fastutil.shorts.ShortSet;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ChunkHolder;
import net.minecraft.server.level.ChunkLevel;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.ChunkResult;
import net.minecraft.server.level.FullChunkStatus;
import net.minecraft.server.level.GenerationChunkHolder;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Mixin(ChunkHolder.class)
public abstract class ChunkHolderMixin extends GenerationChunkHolder implements ChunkHolderBridge {

    @Shadow
    public int oldTicketLevel;

    @Shadow
    private int ticketLevel;

    @Shadow
    public abstract CompletableFuture<ChunkResult<LevelChunk>> getFullChunkFuture();

    @Shadow
    @Final
    private @Nullable ShortSet[] changedBlocksPerSection;

    public ChunkHolderMixin(ChunkPos pos) {
        super(pos);
    }

    @Inject(method = "blockChanged", at = @At(value = "FIELD", target = "Lnet/minecraft/server/level/ChunkHolder;changedBlocksPerSection:[Lit/unimi/dsi/fastutil/shorts/ShortSet;", ordinal = 0), cancellable = true)
    private void arclight$checkSecIndex(BlockPos pos, CallbackInfoReturnable<Boolean> cir, @Local int sectionIndex) {
        if (sectionIndex < 0 || sectionIndex >= this.changedBlocksPerSection.length) cir.setReturnValue(false); // CraftBukkit - SPIGOT-6086, SPIGOT-6296
    }

    @Inject(method = "updateFutures", at = @At("RETURN"))
    private void arclight$callChunkLoadEvent(ChunkMap scheduler, Executor mainThreadExecutor, CallbackInfo ci, @Local(ordinal = 0) FullChunkStatus oldFullStatus, @Local(ordinal = 1) FullChunkStatus newFullStatus) {
        // CraftBukkit start
        // ChunkLoadEvent: Called after the chunk is loaded: isChunkLoaded returns true and chunk is ready to be modified by plugins.
        if (!oldFullStatus.isOrAfter(FullChunkStatus.FULL) && newFullStatus.isOrAfter(FullChunkStatus.FULL)) {
            this.getFullChunkFuture().thenAccept((either) -> {
                LevelChunk levelchunk = (LevelChunk) either.orElse(null);
                if (levelchunk != null) {
                    scheduler.bridge$getCallbackExecutor().execute(() -> {
                        levelchunk.loadCallback();
                    });
                }
            }).exceptionally((throwable) -> {
                // ensure exceptions are printed, by default this is not the case
                MinecraftServer.LOGGER.error("Failed to schedule load callback for chunk " + this.pos, throwable);
                return null;
            });

            // Run callback right away if the future was already done
            scheduler.bridge$getCallbackExecutor().run();
        }
        // CraftBukkit end
    }

    // CraftBukkit start
    @Override
    public LevelChunk getFullChunkNow() {
        // Note: We use the oldTicketLevel for isLoaded checks.
        if (!ChunkLevel.fullStatus(this.oldTicketLevel).isOrAfter(FullChunkStatus.FULL)) return null;
        return this.getFullChunkNowUnchecked();
    }

    @Override
    public LevelChunk getFullChunkNowUnchecked() {
        return (LevelChunk) this.getChunkIfPresentUnchecked(ChunkStatus.FULL);
    }
    // CraftBukkit end

    @Override
    public int bridge$getOldTicketLevel() {
        return this.oldTicketLevel;
    }

    // CraftBukkit start
    // ChunkUnloadEvent: Called before the chunk is unloaded: isChunkLoaded is still true and chunk can still be modified by plugins.
    // SPIGOT-7780: Moved out of updateFutures to call all chunk unload events before calling updateHighestAllowedStatus for all chunks
    @Override
    public void callEventIfUnloading(ChunkMap chunkmap) {
        FullChunkStatus oldFullChunkStatus = ChunkLevel.fullStatus(this.oldTicketLevel);
        FullChunkStatus newFullChunkStatus = ChunkLevel.fullStatus(this.ticketLevel);
        boolean oldIsFull = oldFullChunkStatus.isOrAfter(FullChunkStatus.FULL);
        boolean newIsFull = newFullChunkStatus.isOrAfter(FullChunkStatus.FULL);
        if (oldIsFull && !newIsFull) {
            this.getFullChunkFuture().thenAccept((either) -> {
                LevelChunk levelchunk = (LevelChunk) either.orElse(null);
                if (levelchunk != null) {
                    chunkmap.bridge$getCallbackExecutor().execute(() -> {
                        // Minecraft will apply the chunks tick lists to the world once the chunk got loaded, and then store the tick
                        // lists again inside the chunk once the chunk becomes inaccessible and set the chunk's needsSaving flag.
                        // These actions may however happen deferred, so we manually set the needsSaving flag already here.
                        levelchunk.markUnsaved();
                        levelchunk.unloadCallback();
                    });
                }
            }).exceptionally((throwable) -> {
                // ensure exceptions are printed, by default this is not the case
                MinecraftServer.LOGGER.error("Failed to schedule unload callback for chunk " + this.pos, throwable);
                return null;
            });

            // Run callback right away if the future was already done
            chunkmap.bridge$getCallbackExecutor().run();
        }
    }
    // CraftBukkit end
}
