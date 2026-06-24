package io.izzel.arclight.common.mixin.core.world.level.chunk;

import com.llamalad7.mixinextras.sugar.Local;
import io.izzel.arclight.common.bridge.core.world.level.chunk.ChunkGeneratorBridge;
import it.unimi.dsi.fastutil.ints.IntArraySet;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.SharedConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.SectionPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.FeatureSorter;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.RandomSupport;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.XoroshiroRandomSource;
import net.minecraft.world.level.levelgen.feature.FeatureCountTracker;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Mixin(ChunkGenerator.class)
public class ChunkGeneratorMixin implements ChunkGeneratorBridge {

    @Mutable
    @Shadow
    @Final
    protected BiomeSource biomeSource;

    @Shadow
    @Final
    private Supplier<List<FeatureSorter.StepFeatureData>> featuresPerStep;

    @Shadow
    @Final
    public Function<Holder<Biome>, BiomeGenerationSettings> generationSettingsGetter;

    @Shadow
    private static BoundingBox getWritableArea(ChunkAccess chunk) {
        throw new UnsupportedOperationException("Implemented via mixin");
    }

    @Override
    public void addVanillaDecorations(final WorldGenLevel level, final ChunkAccess chunk, final StructureManager structureManager) {
        ChunkPos centerPos = chunk.getPos();
        if (!SharedConstants.debugVoidTerrain(centerPos)) {
            SectionPos sectionPos = SectionPos.of(centerPos, level.getMinSectionY());
            BlockPos origin = sectionPos.origin();
            Registry<Structure> structuresRegistry = level.registryAccess().lookupOrThrow(Registries.STRUCTURE);
            Map<Integer, List<Structure>> structuresByStep = (Map) structuresRegistry.stream().collect(Collectors.groupingBy((structurex) -> structurex.step().ordinal()));
            List<FeatureSorter.StepFeatureData> featureList = (List) this.featuresPerStep.get();
            WorldgenRandom random = new WorldgenRandom(new XoroshiroRandomSource(RandomSupport.generateUniqueSeed()));
            long decorationSeed = random.setDecorationSeed(level.getSeed(), origin.getX(), origin.getZ());
            Set<Holder<Biome>> possibleBiomes = new ObjectArraySet();
            ChunkPos.rangeClosed(sectionPos.chunk(), 1).forEach((chunkPos) -> {
                ChunkAccess chunkInRange = level.getChunk(chunkPos.x(), chunkPos.z());

                for (LevelChunkSection section : chunkInRange.getSections()) {
                    Objects.requireNonNull(possibleBiomes);
                    section.getBiomes().getAll(possibleBiomes::add);
                }
            });
            possibleBiomes.retainAll(this.biomeSource.possibleBiomes());
            int featureStepCount = featureList.size();

            try {
                Registry<PlacedFeature> featureRegistry = level.registryAccess().lookupOrThrow(Registries.PLACED_FEATURE);
                int generationSteps = Math.max(GenerationStep.Decoration.values().length, featureStepCount);

                for (int stepIndex = 0; stepIndex < generationSteps; ++stepIndex) {
                    int index = 0;
                    if (structureManager.shouldGenerateStructures()) {
                        for (Structure structure : structuresByStep.getOrDefault(stepIndex, Collections.emptyList())) {
                            random.setFeatureSeed(decorationSeed, index, stepIndex);
                            Supplier<String> currentlyGenerating = () -> {
                                Optional var10000 = structuresRegistry.getResourceKey(structure).map(Object::toString);
                                Objects.requireNonNull(structure);
                                return (String) var10000.orElseGet(structure::toString);
                            };

                            try {
                                level.setCurrentlyGenerating(currentlyGenerating);
                                structureManager.startsForStructure(sectionPos, structure).forEach((start) -> start.placeInChunk(level, structureManager, ((ChunkGenerator) (Object) this), random, getWritableArea(chunk), centerPos));
                            } catch (Exception e) {
                                CrashReport report = CrashReport.forThrowable(e, "Feature placement");
                                CrashReportCategory var10000 = report.addCategory("Feature");
                                Objects.requireNonNull(currentlyGenerating);
                                var10000.setDetail("Description", currentlyGenerating::get);
                                throw new ReportedException(report);
                            }

                            ++index;
                        }
                    }

                    if (stepIndex < featureStepCount) {
                        IntSet possibleFeaturesThisStep = new IntArraySet();

                        for (Holder<Biome> biome : possibleBiomes) {
                            List<HolderSet<PlacedFeature>> featuresInBiome = this.generationSettingsGetter.apply(biome).features();
                            if (stepIndex < featuresInBiome.size()) {
                                HolderSet<PlacedFeature> featuresInBiomeThisStep = featuresInBiome.get(stepIndex);
                                FeatureSorter.StepFeatureData stepFeatureData = featureList.get(stepIndex);
                                featuresInBiomeThisStep.stream().map(Holder::value).forEach((featurex) -> possibleFeaturesThisStep.add(stepFeatureData.indexMapping().applyAsInt(featurex)));
                            }
                        }

                        int numberOfFeaturesInStep = possibleFeaturesThisStep.size();
                        int[] indexArray = possibleFeaturesThisStep.toIntArray();
                        Arrays.sort(indexArray);
                        FeatureSorter.StepFeatureData stepFeatureData = (FeatureSorter.StepFeatureData) featureList.get(stepIndex);

                        for (int featureIndex = 0; featureIndex < numberOfFeaturesInStep; ++featureIndex) {
                            int globalIndexOfFeature = indexArray[featureIndex];
                            PlacedFeature feature = (PlacedFeature) stepFeatureData.features().get(globalIndexOfFeature);
                            Supplier<String> currentlyGenerating = () -> {
                                Optional var10000 = featureRegistry.getResourceKey(feature).map(Object::toString);
                                Objects.requireNonNull(feature);
                                return (String) var10000.orElseGet(feature::toString);
                            };
                            random.setFeatureSeed(decorationSeed, globalIndexOfFeature, stepIndex);

                            try {
                                level.setCurrentlyGenerating(currentlyGenerating);
                                feature.placeWithBiomeCheck(level, ((ChunkGenerator) (Object) this), random, origin);
                            } catch (Exception e) {
                                CrashReport report = CrashReport.forThrowable(e, "Feature placement");
                                CrashReportCategory var43 = report.addCategory("Feature");
                                Objects.requireNonNull(currentlyGenerating);
                                var43.setDetail("Description", currentlyGenerating::get);
                                throw new ReportedException(report);
                            }
                        }
                    }
                }

                level.setCurrentlyGenerating((Supplier) null);
                if (SharedConstants.DEBUG_FEATURE_COUNT) {
                    FeatureCountTracker.chunkDecorated(level.getLevel());
                }

            } catch (Exception e) {
                CrashReport report = CrashReport.forThrowable(e, "Biome decoration");
                report.addCategory("Generation").setDetail("CenterX", centerPos.x()).setDetail("CenterZ", centerPos.z()).setDetail("Decoration Seed", decorationSeed);
                throw new ReportedException(report);
            }
        }
    }

    // CraftBukkit start
    @Override
    public void applyBiomeDecoration(WorldGenLevel level, ChunkAccess chunk, StructureManager structureManager) {
        applyBiomeDecoration(level, chunk, structureManager, true);
    }

    @Override
    public void applyBiomeDecoration(WorldGenLevel worldgenlevel, ChunkAccess chunkaccess, StructureManager structuremanager, boolean vanilla) {
        if (vanilla) {
            addVanillaDecorations(worldgenlevel, chunkaccess, structuremanager);
        }

        org.bukkit.World world = worldgenlevel.getMinecraftWorld().getWorld();
        // only call when a populator is present (prevents unnecessary entity conversion)
        if (!world.getPopulators().isEmpty()) {
            org.bukkit.craftbukkit.generator.CraftLimitedRegion limitedRegion = new org.bukkit.craftbukkit.generator.CraftLimitedRegion(worldgenlevel, chunkaccess.getPos());
            int x = chunkaccess.getPos().x();
            int z = chunkaccess.getPos().z();
            for (org.bukkit.generator.BlockPopulator populator : world.getPopulators()) {
                WorldgenRandom worldgenrandom = new WorldgenRandom(new net.minecraft.world.level.levelgen.LegacyRandomSource(worldgenlevel.getSeed()));
                worldgenrandom.setDecorationSeed(worldgenlevel.getSeed(), x, z);
                populator.populate(world, new org.bukkit.craftbukkit.util.RandomSourceWrapper.RandomWrapper(worldgenrandom), x, z, limitedRegion);
            }
            limitedRegion.saveEntities();
            limitedRegion.breakLink();
        }
    }
    // CraftBukkit end

    @Override
    public void bridge$setBiomeSource(BiomeSource biomeSource) {
        this.biomeSource = biomeSource;
    }

    @Inject(method = "tryGenerateStructure",at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/StructureManager;setStartForStructure(Lnet/minecraft/core/SectionPos;Lnet/minecraft/world/level/levelgen/structure/Structure;Lnet/minecraft/world/level/levelgen/structure/StructureStart;Lnet/minecraft/world/level/chunk/StructureAccess;)V"), cancellable = true)
    private void arclight$callAsyncStructureSpawnEvent(StructureSet.StructureSelectionEntry selected, StructureManager structureManager, RegistryAccess registryAccess, RandomState randomState, StructureTemplateManager structureTemplateManager, long seed, ChunkAccess centerChunk, ChunkPos sourceChunkPos, SectionPos sectionPos, ResourceKey<Level> level, CallbackInfoReturnable<Boolean> cir, @Local StructureStart start, @Local Structure structure) {
        // CraftBukkit start
        BoundingBox box = start.getBoundingBox();
        org.bukkit.event.world.AsyncStructureSpawnEvent event = new org.bukkit.event.world.AsyncStructureSpawnEvent(structureManager.level.getMinecraftWorld().getWorld(), org.bukkit.craftbukkit.generator.structure.CraftStructure.minecraftToBukkit(structure), new org.bukkit.util.BoundingBox(box.minX(), box.minY(), box.minZ(), box.maxX(), box.maxY(), box.maxZ()), sourceChunkPos.x(), sourceChunkPos.z());
        org.bukkit.Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            cir.setReturnValue(true);
        }
        // CraftBukkit end
    }
}
