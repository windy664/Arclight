package io.izzel.arclight.common.mixin.core.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.SculkSensorBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.bukkit.craftbukkit.block.CraftBlock;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SculkSensorBlock.class)
public abstract class SculkSensorBlockMixin extends BaseEntityBlock {

    protected SculkSensorBlockMixin(Properties properties) {
        super(properties);
    }

    @Inject(method = "stepOn", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;getBlockEntity(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/entity/BlockEntity;"), cancellable = true)
    private void arclight$callInteractEvents(Level level, BlockPos pos, BlockState onState, Entity entity, CallbackInfo ci) {
        // CraftBukkit start
        org.bukkit.event.Cancellable cancellable;
        if (entity instanceof Player) {
            cancellable = org.bukkit.craftbukkit.event.CraftEventFactory.callPlayerInteractEvent((Player) entity, org.bukkit.event.block.Action.PHYSICAL, pos, null, null, null);
        } else {
            cancellable = new org.bukkit.event.entity.EntityInteractEvent(entity.getBukkitEntity(), level.getWorld().getBlockAt(pos.getX(), pos.getY(), pos.getZ()));
            level.getCraftServer().getPluginManager().callEvent((org.bukkit.event.entity.EntityInteractEvent) cancellable);
        }
        if (cancellable.isCancelled()) {
            ci.cancel();
            return;
        }
        // CraftBukkit end
    }

    @Inject(method = "deactivate", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z"), cancellable = true)
    private static void arclight$callBlockRedstoneEvent0(Level level, BlockPos pos, BlockState state, CallbackInfo ci) {
        // CraftBukkit start
        BlockRedstoneEvent eventRedstone = new BlockRedstoneEvent(CraftBlock.at(level, pos), state.getValue(SculkSensorBlock.POWER), 0);
        level.getCraftServer().getPluginManager().callEvent(eventRedstone);

        if (eventRedstone.getNewCurrent() > 0) {
            level.setBlock(pos, state.setValue(SculkSensorBlock.POWER, eventRedstone.getNewCurrent()), 3);
            ci.cancel();
            return;
        }
        // CraftBukkit end
    }

    @Inject(method = "activate", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z"), cancellable = true)
    private static void arclight$callBlockRedstoneEvent1(Entity sourceEntity, Level level, BlockPos pos, BlockState state, int calculatedPower, int vibrationFrequency, CallbackInfo ci) {
        // CraftBukkit start
        BlockRedstoneEvent eventRedstone = new BlockRedstoneEvent(CraftBlock.at(level, pos), state.getValue(SculkSensorBlock.POWER), calculatedPower);
        level.getCraftServer().getPluginManager().callEvent(eventRedstone);

        if (eventRedstone.getNewCurrent() <= 0) {
            ci.cancel();
            return;
        }
        calculatedPower = eventRedstone.getNewCurrent();
        // CraftBukkit end
    }

    @Override
    public int bridge$getExpDrop(BlockState blockState, ServerLevel world, BlockPos blockPos, ItemStack itemStack, boolean dropExperience) {
        if (dropExperience) {
            return this.bridge$tryDropExperience(world, blockPos, itemStack, ConstantInt.of(5));
        }
        return 0;
        // CraftBukkit end
    }
}
