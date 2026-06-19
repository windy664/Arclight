package io.izzel.arclight.common.mixin.core.world.level.block;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.TripWireBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import org.bukkit.event.entity.EntityInteractEvent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(TripWireBlock.class)
public class TripWireBlockMixin {

    @Shadow
    @Final
    public static BooleanProperty ATTACHED;

    @Definition(id = "wasPressed", local = @Local(type = boolean.class, ordinal = 0))
    @Definition(id = "shouldBePressed", local = @Local(type = boolean.class, ordinal = 1))
    @Expression("shouldBePressed != wasPressed")
    @Inject(method = "checkPressed(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Ljava/util/List;)V", at = @At("MIXINEXTRAS:EXPRESSION"), cancellable = true)
    private void arclight$callPlayerInteractEvent(Level level, BlockPos pos, List<? extends Entity> entities, CallbackInfo ci,
                                                  @Local BlockState state,
                                                  @Local(ordinal = 0) boolean wasPressed,
                                                  @Local(ordinal = 1) boolean shouldBePressed) {
        // CraftBukkit start - Call interact even when triggering connected tripwire
        if (wasPressed != shouldBePressed && shouldBePressed && (Boolean)state.getValue(ATTACHED)) {
            org.bukkit.World bworld = level.getWorld();
            org.bukkit.plugin.PluginManager manager = level.getCraftServer().getPluginManager();
            org.bukkit.block.Block block = bworld.getBlockAt(pos.getX(), pos.getY(), pos.getZ());
            boolean allowed = false;

            // If all of the events are cancelled block the tripwire trigger, else allow
            for (Object object : entities) {
                if (object != null) {
                    org.bukkit.event.Cancellable cancellable;

                    if (object instanceof Player) {
                        cancellable = org.bukkit.craftbukkit.event.CraftEventFactory.callPlayerInteractEvent((Player) object, org.bukkit.event.block.Action.PHYSICAL, pos, null, null, null);
                    } else if (object instanceof Entity) {
                        cancellable = new EntityInteractEvent(((Entity) object).getBukkitEntity(), block);
                        manager.callEvent((EntityInteractEvent) cancellable);
                    } else {
                        continue;
                    }

                    if (!cancellable.isCancelled()) {
                        allowed = true;
                        break;
                    }
                }
            }

            if (!allowed) {
                ci.cancel();
                return;
            }
        }
        // CraftBukkit end
    }
}
