package io.izzel.arclight.common.mixin.core.world.level.chunk;

import io.izzel.arclight.common.bridge.core.world.chunk.ChunkAccessBridge;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.core.Holder;
import net.minecraft.core.QuartPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.chunk.LightChunk;
import net.minecraft.world.level.chunk.StructureAccess;
import org.bukkit.persistence.PersistentDataContainer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChunkAccess.class)
public abstract class ChunkAccessMixin implements LightChunk, StructureAccess, BiomeManager.NoiseBiomeSource, ChunkAccessBridge {

    @Shadow
    public abstract int getMinY();

    @Shadow
    public abstract int getHeight();

    @Shadow
    @Final
    protected LevelChunkSection[] sections;
    // CraftBukkit start - SPIGOT-6814: move to IChunkAccess to account for 1.17 to 1.18 chunk upgrading.
    private static final org.bukkit.craftbukkit.persistence.CraftPersistentDataTypeRegistry DATA_TYPE_REGISTRY = new org.bukkit.craftbukkit.persistence.CraftPersistentDataTypeRegistry();
    public org.bukkit.craftbukkit.persistence.DirtyCraftPersistentDataContainer persistentDataContainer = new org.bukkit.craftbukkit.persistence.DirtyCraftPersistentDataContainer(DATA_TYPE_REGISTRY);
    // CraftBukkit end

    @Inject(method = "tryMarkSaved", cancellable = true, at = @At("RETURN"))
    private void arclight$tryMarkSaved(CallbackInfoReturnable<Boolean> cir) {
        if (this.persistentDataContainer.dirty()) {
            this.persistentDataContainer.dirty(false);
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "isUnsaved", cancellable = true, at = @At("RETURN"))
    private void arclight$isDirty(CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(cir.getReturnValueZ() || this.persistentDataContainer.dirty());
    }

    // CraftBukkit start
    @Override
    public void setBiome(int i, int j, int k, Holder<Biome> biome) {
        try {
            int l = QuartPos.fromBlock(this.getMinY());
            int i1 = l + QuartPos.fromBlock(this.getHeight()) - 1;
            int j1 = Mth.clamp(j, l, i1);
            int k1 = this.getSectionIndex(QuartPos.toBlock(j1));

            this.sections[k1].setBiome(i & 3, j1 & 3, k & 3, biome);
        } catch (Throwable throwable) {
            CrashReport crashreport = CrashReport.forThrowable(throwable, "Setting biome");
            CrashReportCategory crashreportcategory = crashreport.addCategory("Biome being set");

            crashreportcategory.setDetail("Location", () -> {
                return CrashReportCategory.formatLocation(this, i, j, k);
            });
            throw new ReportedException(crashreport);
        }
    }
    // CraftBukkit end

    @Override
    public PersistentDataContainer bridge$getPersistentDataContainer() {
        return this.persistentDataContainer;
    }

    @Override
    public void bridge$setPersistentDataContainer(PersistentDataContainer container) {
        this.persistentDataContainer = (org.bukkit.craftbukkit.persistence.DirtyCraftPersistentDataContainer) container;
    }
}
