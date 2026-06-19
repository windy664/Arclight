package io.izzel.arclight.common.mixin.core.world.level.block;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.turtle.Turtle;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.TurtleEggBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.bukkit.craftbukkit.block.CraftBlock;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityInteractEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TurtleEggBlock.class)
public class TurtleEggBlockMixin {

    @Inject(method = "destroyEgg", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/TurtleEggBlock;decreaseEggs(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)V"), cancellable = true)
    private void arclight$callPlayerInteractEvent(Level level, BlockState state, BlockPos pos, Entity entity, int randomness, CallbackInfo ci, @Local ServerLevel serverLevel) {
        // CraftBukkit start - Step on eggs
        org.bukkit.event.Cancellable cancellable;
        if (entity instanceof Player) {
            cancellable = CraftEventFactory.callPlayerInteractEvent((Player) entity, org.bukkit.event.block.Action.PHYSICAL, pos, null, null, null);
        } else {
            cancellable = new EntityInteractEvent(entity.getBukkitEntity(), CraftBlock.at(serverLevel, pos));
            serverLevel.getCraftServer().getPluginManager().callEvent((EntityInteractEvent) cancellable);
        }

        if (cancellable.isCancelled()) {
            ci.cancel();
            return;
        }
        // CraftBukkit end
    }

    @Inject(method = "randomTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;playSound(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/core/BlockPos;Lnet/minecraft/sounds/SoundEvent;Lnet/minecraft/sounds/SoundSource;FF)V", ordinal = 0), cancellable = true)
    private void arclight$handleBlockGrowEvent(BlockState state, ServerLevel level, BlockPos pos, RandomSource random, CallbackInfo ci, @Local int hatch) {
        // CraftBukkit start - Call BlockGrowEvent
        if (!CraftEventFactory.handleBlockGrowEvent(level, pos, state.setValue(TurtleEggBlock.HATCH, hatch + 1), 2)) {
            ci.cancel();
            return;
        }
        // CraftBukkit end
    }

    @Redirect(method = "randomTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z"))
    private boolean arclight$cancelSetBlock(ServerLevel instance, BlockPos blockPos, BlockState state, int i) {
        return false;
    }

    @Inject(method = "randomTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;playSound(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/core/BlockPos;Lnet/minecraft/sounds/SoundEvent;Lnet/minecraft/sounds/SoundSource;FF)V", ordinal = 1), cancellable = true)
    private void arclight$callBlockFadeEvent(BlockState state, ServerLevel level, BlockPos pos, RandomSource random, CallbackInfo ci) {
        // CraftBukkit start - Call BlockFadeEvent
        if (CraftEventFactory.callBlockFadeEvent(level, pos, Blocks.AIR.defaultBlockState()).isCancelled()) {
            ci.cancel();
            return;
        }
        // CraftBukkit end
    }

    @Inject(method = "randomTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;addFreshEntity(Lnet/minecraft/world/entity/Entity;)Z"))
    private void arclight$pusSpawnReason(BlockState state, ServerLevel level, BlockPos pos, RandomSource random, CallbackInfo ci, @Local Turtle turtle) {
        turtle.arclight$pushAddEntityReason(CreatureSpawnEvent.SpawnReason.EGG);
    }
}
