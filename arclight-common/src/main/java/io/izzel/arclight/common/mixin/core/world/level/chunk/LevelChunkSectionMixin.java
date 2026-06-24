package io.izzel.arclight.common.mixin.core.world.level.chunk;

import io.izzel.arclight.common.bridge.core.world.level.chunk.LevelChunkSectionBridge;
import io.izzel.arclight.common.mod.mixins.annotation.CreateConstructor;
import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.chunk.PalettedContainer;
import net.minecraft.world.level.chunk.PalettedContainerRO;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(LevelChunkSection.class)
public abstract class LevelChunkSectionMixin implements LevelChunkSectionBridge {

    @Mutable
    @Shadow
    @Final
    private PalettedContainer<BlockState> states;

    @Shadow
    private PalettedContainerRO<Holder<Biome>> biomes;

    @Shadow
    public abstract void recalcBlockCounts();

    @CreateConstructor
    public void arclight$constructor(final PalettedContainer<BlockState> states, final PalettedContainer<Holder<Biome>> biomes) {
        this.states = states;
        this.biomes = biomes;
        this.recalcBlockCounts();
    }

    // CraftBukkit start
    @Override
    public void setBiome(int i, int j, int k, Holder<Biome> biome) {
        ((PalettedContainer<Holder<Biome>>) this.biomes).set(i, j, k, biome);
    }
    // CraftBukkit end
}
