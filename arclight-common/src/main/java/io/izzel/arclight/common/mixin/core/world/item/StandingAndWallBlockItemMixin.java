package io.izzel.arclight.common.mixin.core.world.item;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.StandingAndWallBlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import org.bukkit.craftbukkit.block.CraftBlock;
import org.bukkit.craftbukkit.block.data.CraftBlockData;
import org.bukkit.event.block.BlockCanBuildEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(StandingAndWallBlockItem.class)
public class StandingAndWallBlockItemMixin {

    @ModifyReturnValue(method = "getPlacementState", at = @At("RETURN"))
    private BlockState arclight$callBlockCanBuildEvent(BlockState original,
                                                       @Local(argsOnly = true) BlockPlaceContext context,
                                                       @Local LevelReader levelreader,
                                                       @Local(ordinal = 1) BlockState stateForPlacement,
                                                       @Local BlockPos pos) {
        // CraftBukkit start
        if (stateForPlacement != null) {
            boolean defaultReturn = levelreader.isUnobstructed(stateForPlacement, pos, CollisionContext.empty());
            org.bukkit.entity.Player player = (context.getPlayer() instanceof ServerPlayer) ? (org.bukkit.entity.Player) context.getPlayer().getBukkitEntity() : null;

            BlockCanBuildEvent event = new BlockCanBuildEvent(CraftBlock.at(context.getLevel(), pos), player, CraftBlockData.fromData(stateForPlacement), defaultReturn);
            context.getLevel().getCraftServer().getPluginManager().callEvent(event);

            return (event.isBuildable()) ? stateForPlacement : null;
        } else {
            return null;
        }
        // CraftBukkit end
    }
}
