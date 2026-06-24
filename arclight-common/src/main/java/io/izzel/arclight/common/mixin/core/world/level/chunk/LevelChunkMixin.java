package io.izzel.arclight.common.mixin.core.world.level.chunk;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import io.izzel.arclight.common.bridge.core.world.level.LevelAccessorBridge;
import io.izzel.arclight.common.bridge.core.world.level.chunk.LevelChunkBridge;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.debug.DebugValueSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.chunk.PalettedContainerFactory;
import net.minecraft.world.level.chunk.ProtoChunk;
import net.minecraft.world.level.chunk.UpgradeData;
import net.minecraft.world.level.levelgen.blending.BlendingData;
import net.minecraft.world.ticks.LevelChunkTicks;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(LevelChunk.class)
public abstract class LevelChunkMixin extends ChunkAccess implements DebugValueSource, LevelChunkBridge {

    @Shadow
    @Final
    public Level level;
    public ServerLevel r; // TODO f_62776_ check on update
    // CraftBukkit start
    public boolean mustNotSave;
    public boolean needsDecoration;
    // CraftBukkit end

    public LevelChunkMixin(ChunkPos chunkPos, UpgradeData upgradeData, LevelHeightAccessor levelHeightAccessor, PalettedContainerFactory containerFactory, long inhabitedTime, LevelChunkSection @Nullable [] sections, @Nullable BlendingData blendingData) {
        super(chunkPos, upgradeData, levelHeightAccessor, containerFactory, inhabitedTime, sections, blendingData);
    }

    @Inject(method = "<init>(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/level/ChunkPos;Lnet/minecraft/world/level/chunk/UpgradeData;Lnet/minecraft/world/ticks/LevelChunkTicks;Lnet/minecraft/world/ticks/LevelChunkTicks;J[Lnet/minecraft/world/level/chunk/LevelChunkSection;Lnet/minecraft/world/level/chunk/LevelChunk$PostLoadProcessor;Lnet/minecraft/world/level/levelgen/blending/BlendingData;)V", at = @At("RETURN"))
    private void arclight$init(Level level, ChunkPos pos, UpgradeData upgradeData, LevelChunkTicks blockTicks, LevelChunkTicks fluidTicks, long inhabitedTime, LevelChunkSection[] sections, LevelChunk.PostLoadProcessor postLoad, BlendingData blendingData, CallbackInfo ci) {
        if (LevelAccessorBridge.from(level) instanceof LevelAccessorBridge bridge) {
            this.r = bridge.getMinecraftWorld();
        }
    }

    @Inject(method = "<init>(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/level/chunk/ProtoChunk;Lnet/minecraft/world/level/chunk/LevelChunk$PostLoadProcessor;)V", at = @At("RETURN"))
    private void arclight$init(ServerLevel level, ProtoChunk protoChunk, LevelChunk.PostLoadProcessor postLoad, CallbackInfo ci) {
        this.needsDecoration = true;
        this.bridge$setPersistentDataContainer(protoChunk.bridge$getPersistentDataContainer()); // SPIGOT-6814: copy PDC to account for 1.17 to 1.18 chunk upgrading.
    }

    @ModifyExpressionValue(method = "setBlockState", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;isClientSide()Z"))
    private boolean arclight$doPlace(boolean original, @Local Block newBlock) {
        return original && (!this.level.bridge$isCaptureBlockStates() || newBlock instanceof net.minecraft.world.level.block.BaseEntityBlock);
    }

    @WrapOperation(method = "getBlockEntity(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/chunk/LevelChunk$EntityCreationType;)Lnet/minecraft/world/level/block/entity/BlockEntity;", at = @At(value = "INVOKE", target = "Ljava/util/Map;get(Ljava/lang/Object;)Ljava/lang/Object;"))
    private Object arclight$resetBlockEntity(Map instance, Object o, Operation<BlockEntity> original, @Local(argsOnly = true) BlockPos pos) {
        return level.bridge$getCapturedBlockEntity().get(pos) == null ? original.call(instance, o) : level.bridge$getCapturedBlockEntity();
    }

    @Inject(method = "setBlockEntity", at = @At(value = "INVOKE", target = "Lorg/slf4j/Logger;warn(Ljava/lang/String;[Ljava/lang/Object;)V", ordinal = 0))
    private void arclight$throwNewException(BlockEntity blockEntity, CallbackInfo ci) {
        new Exception().printStackTrace(); // CraftBukkit
    }

    @Inject(method = "removeBlockEntity", at = @At(value = "INVOKE_ASSIGN", remap = false, target = "Ljava/util/Map;remove(Ljava/lang/Object;)Ljava/lang/Object;"))
    private void arclight$remove(BlockPos pos, CallbackInfo ci) {
        if (!pendingBlockEntities.isEmpty()) {
            pendingBlockEntities.remove(pos);
        }
    }

    // CraftBukkit start
    public void loadCallback() {
        org.bukkit.Server server = this.level.getCraftServer();
        if (server != null) {
            /*
             * If it's a new world, the first few chunks are generated inside
             * the World constructor. We can't reliably alter that, so we have
             * no way of creating a CraftWorld/CraftServer at that point.
             */
            org.bukkit.Chunk bukkitChunk = new org.bukkit.craftbukkit.CraftChunk(((LevelChunk) (Object) this));
            server.getPluginManager().callEvent(new org.bukkit.event.world.ChunkLoadEvent(bukkitChunk, this.needsDecoration));

            if (this.needsDecoration) {
                this.needsDecoration = false;
                java.util.Random random = new java.util.Random();
                random.setSeed(((ServerLevel)this.level).getSeed());
                long xRand = random.nextLong() / 2L * 2L + 1L;
                long zRand = random.nextLong() / 2L * 2L + 1L;
                random.setSeed((long) this.chunkPos.x() * xRand + (long) this.chunkPos.z() * zRand ^ ((ServerLevel) level).getSeed());

                org.bukkit.World world = this.level.getWorld();
                if (world != null) {
                    this.level.bridge$setPopulating(true);
                    try {
                        for (org.bukkit.generator.BlockPopulator populator : world.getPopulators()) {
                            populator.populate(world, random, bukkitChunk);
                        }
                    } finally {
                        this.level.bridge$setPopulating(false);
                    }
                }
                server.getPluginManager().callEvent(new org.bukkit.event.world.ChunkPopulateEvent(bukkitChunk));
            }
        }
    }

    @Override
    public void unloadCallback() {
        org.bukkit.Server server = this.level.getCraftServer();
        org.bukkit.Chunk bukkitChunk = new org.bukkit.craftbukkit.CraftChunk(((LevelChunk) (Object) this));
        org.bukkit.event.world.ChunkUnloadEvent unloadEvent = new org.bukkit.event.world.ChunkUnloadEvent(bukkitChunk, this.isUnsaved());
        server.getPluginManager().callEvent(unloadEvent);
        // note: saving can be prevented, but not forced if no saving is actually required
        this.mustNotSave = !unloadEvent.isSaveChunk();
    }

    @Override
    public boolean isUnsaved() {
        return super.isUnsaved() && !this.mustNotSave;
    }
    // CraftBukkit end

    @Override
    public boolean bridge$isMustNotSave() {
        return this.mustNotSave;
    }

    @Override
    public void bridge$setMustNotSave(boolean mustNotSave) {
        this.mustNotSave = mustNotSave;
    }

    @Override
    public boolean bridge$isNeedsDecoration() {
        return this.needsDecoration;
    }
}
