package io.izzel.arclight.common.mixin.core.world.level.block;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BasePressurePlateBlock;
import net.minecraft.world.level.block.PressurePlateBlock;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.phys.AABB;
import org.bukkit.event.entity.EntityInteractEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(PressurePlateBlock.class)
public abstract class PressurePlateBlockMixin extends BasePressurePlateBlock {

    protected PressurePlateBlockMixin(Properties properties, BlockSetType type) {
        super(properties, type);
    }

    @ModifyReturnValue(method = "getSignalStrength", at = @At("RETURN"))
    private int arclight$callPlayerInteractEvent(int original, @Local(argsOnly = true) Level level, @Local(argsOnly = true) BlockPos pos, @Local Class<? extends Entity> oclass) {
        // CraftBukkit start - Call interact event when turning on a pressure plate
        for (Entity entity : getEntities(level, PressurePlateBlock.TOUCH_AABB.move(pos), oclass)) {
            if (this.getSignalForState(level.getBlockState(pos)) == 0) {
                org.bukkit.World bworld = level.getWorld();
                org.bukkit.plugin.PluginManager manager = level.getCraftServer().getPluginManager();
                org.bukkit.event.Cancellable cancellable;

                if (entity instanceof Player) {
                    cancellable = org.bukkit.craftbukkit.event.CraftEventFactory.callPlayerInteractEvent((Player) entity, org.bukkit.event.block.Action.PHYSICAL, pos, null, null, null);
                } else {
                    cancellable = new EntityInteractEvent(entity.getBukkitEntity(), bworld.getBlockAt(pos.getX(), pos.getY(), pos.getZ()));
                    manager.callEvent((EntityInteractEvent) cancellable);
                }

                // We only want to block turning the plate on if all events are cancelled
                if (cancellable.isCancelled()) {
                    continue;
                }
            }

            return 15;
        }

        return 0;
        // CraftBukkit end
    }

    private static <T extends Entity> java.util.List<T> getEntities(Level world, AABB axisalignedbb, Class<T> oclass) {
        return world.getEntitiesOfClass(oclass, axisalignedbb, EntitySelector.NO_SPECTATORS.and((entity) -> {
            return !entity.isIgnoringBlockTriggers();
        }));
    }

}
