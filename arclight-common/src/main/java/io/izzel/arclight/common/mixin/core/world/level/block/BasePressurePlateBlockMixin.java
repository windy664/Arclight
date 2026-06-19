package io.izzel.arclight.common.mixin.core.world.level.block;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.sugar.Local;
import io.izzel.arclight.common.mod.mixins.annotation.TransformAccess;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BasePressurePlateBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BasePressurePlateBlock.class)
public class BasePressurePlateBlockMixin {

    @Definition(id = "oldSignal", local = @Local(type = int.class, ordinal = 0, argsOnly = true))
    @Definition(id = "signal", local = @Local(type = int.class, ordinal = 1))
    @Expression("oldSignal != signal")
    @Inject(method = "checkPressed", at = @At("MIXINEXTRAS:EXPRESSION"))
    private void arclight$callBlockRedstoneEvent(Entity sourceEntity, Level level, BlockPos pos, BlockState state, int oldSignal, CallbackInfo ci,
                                                 @Local(ordinal = 1) int signal,
                                                 @Local(ordinal = 0) boolean wasPressed,
                                                 @Local(ordinal = 1) boolean isPressed) {
        // CraftBukkit start - Interact Pressure Plate
        org.bukkit.World bworld = level.getWorld();
        org.bukkit.plugin.PluginManager manager = level.getCraftServer().getPluginManager();

        if (wasPressed != isPressed) {
            BlockRedstoneEvent eventRedstone = new BlockRedstoneEvent(bworld.getBlockAt(pos.getX(), pos.getY(), pos.getZ()), oldSignal, signal);
            manager.callEvent(eventRedstone);

            isPressed = eventRedstone.getNewCurrent() > 0;
            signal = eventRedstone.getNewCurrent();
        }
        // CraftBukkit end
    }

    @TransformAccess(Opcodes.ACC_PROTECTED | Opcodes.ACC_STATIC)
    private static <T extends Entity> java.util.List<T> getEntities(Level level, AABB entityDetectionBox, Class<T> entityClass) {
        return level.getEntitiesOfClass(entityClass, entityDetectionBox, EntitySelector.NO_SPECTATORS.and((e) -> !e.isIgnoringBlockTriggers()));
    }
}
