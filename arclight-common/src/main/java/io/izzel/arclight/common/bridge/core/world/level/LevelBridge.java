package io.izzel.arclight.common.bridge.core.world.level;

import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.gamerules.GameRules;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.block.CapturedBlockState;
import org.bukkit.entity.SpawnCategory;
import org.bukkit.generator.ChunkGenerator;
import org.spigotmc.SpigotWorldConfig;

import java.util.Map;

public interface LevelBridge extends LevelWriterBridge, LevelAccessorBridge {

    default boolean bridge$isCaptureBlockStates() {
        return false;
    }

    default void bridge$setCaptureBlockStates(boolean captureBlockStates) {
    }

    default boolean bridge$isCaptureTreeGeneration() {
        return false;
    }

    default void bridge$setCaptureTreeGeneration(boolean captureTreeGeneration) {

    }

    default Map<BlockPos, CapturedBlockState> bridge$getCapturedBlockStates() {
        return null;
    }

    default void bridge$setCapturedBlockStates(Map<BlockPos, CapturedBlockState> capturedBlockStates) {

    }

    default CraftWorld getWorld() {
        return null;
    }

    default boolean bridge$isPopulating() {
        return false;
    }

    default void bridge$setPopulating(boolean populating) {

    }

    default ChunkGenerator bridge$getGenerator() {
        return null;
    }

    default BlockEntity getBlockEntity(BlockPos pos, boolean validate) {
        return null;
    }

    default SpigotWorldConfig bridge$spigotConfig() {
        return null;
    }

    default Object2LongOpenHashMap<SpawnCategory> bridge$ticksPerSpawnCategory() {
        return null;
    }

    default ResourceKey<LevelStem> bridge$getTypeKey() {
        return null;
    }

    default void bridge$setLastPhysicsProblem(BlockPos pos) {

    }

    default boolean bridge$preventPoiUpdated() {
        return false;
    }

    default void bridge$preventPoiUpdated(boolean b) {

    }

    default void bridge$forge$notifyAndUpdatePhysics(BlockPos pos, LevelChunk chunk, BlockState oldBlock, BlockState newBlock, int i, int j) {

    }

    default boolean bridge$forge$onBlockPlace(BlockPos pos, LivingEntity livingEntity, Direction direction) {
        return false;
    }

    default boolean bridge$forge$mobGriefing(Entity entity) {
        if (this instanceof ServerLevel serverLevel) {
            return serverLevel.getGameRules().get(GameRules.MOB_GRIEFING);
        }
        return GameRules.MOB_GRIEFING.defaultValue();
    }

    default void bridge$forge$onPotionBrewed(NonNullList<ItemStack> stacks) {}

    default boolean bridge$forge$restoringBlockSnapshots() {
        return false;
    }

    default Map<BlockPos, CapturedBlockState> bridge$getCapturedBlockState() {
        return null;
    }

    default Map<BlockPos, BlockEntity> bridge$getCapturedBlockEntity() {
        return null;
    }

    default void bridge$platform$startCaptureBlockBreak() {}

    default boolean bridge$isCapturingBlockBreak() {
        return false;
    }

    default void bridge$platform$endCaptureBlockBreak() {}

    default CraftServer getCraftServer() {
        return null;
    }

    default ResourceKey<LevelStem> getTypeKey(){
        return null;
    }

    default void notifyAndUpdatePhysics(BlockPos blockpos, LevelChunk levelchunk, BlockState oldBlock, BlockState newBlock, BlockState actualBlock, int i, int j) {

    }
}
