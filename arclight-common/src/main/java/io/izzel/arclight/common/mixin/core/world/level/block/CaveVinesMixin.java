package io.izzel.arclight.common.mixin.core.world.level.block;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Cancellable;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CaveVines;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootTable;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.event.player.PlayerHarvestBlockEvent;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.LinkedList;
import java.util.List;
import java.util.function.BiConsumer;

@Mixin(CaveVines.class)
public interface CaveVinesMixin {

    @WrapOperation(method = "use", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/Block;dropFromBlockInteractLootTable(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/resources/ResourceKey;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/block/entity/BlockEntity;Lnet/minecraft/world/item/ItemInstance;Lnet/minecraft/world/entity/Entity;Ljava/util/function/BiConsumer;)Z"))
    private static boolean arclight$callCbEvents(ServerLevel serverlevel, ResourceKey<LootTable> key, BlockState interactedBlockState,
                                                 @Nullable BlockEntity interactedBlockEntity,
                                                 @Nullable ItemInstance tool,
                                                 @Nullable Entity interactingEntity,
                                                 BiConsumer<ServerLevel, ItemStack> consumer,
                                                 Operation<Boolean> original,
                                                 @Local(argsOnly = true) Level level,
                                                 @Local(argsOnly = true) Entity sourceEntity,
                                                 @Local(argsOnly = true) BlockState state,
                                                 @Local(argsOnly = true) BlockPos pos,
                                                 @Cancellable CallbackInfoReturnable<InteractionResult> cir) {
        // CraftBukkit start
        if (!org.bukkit.craftbukkit.event.CraftEventFactory.callEntityChangeBlockEvent(sourceEntity, pos, (BlockState) state.setValue(CaveVines.BERRIES, false))) {
            cir.setReturnValue(InteractionResult.SUCCESS);
        }

        if (sourceEntity instanceof Player) {
            List<ItemStack> dropped = new LinkedList<>();
            Block.dropFromBlockInteractLootTable(serverlevel, BuiltInLootTables.HARVEST_CAVE_VINE, state, level.getBlockEntity(pos), (ItemInstance) null, sourceEntity, (serverlevel1, itemstack) -> {
                dropped.add(itemstack);
            });
            PlayerHarvestBlockEvent event = CraftEventFactory.callPlayerHarvestBlockEvent(level, pos, (Player) sourceEntity, net.minecraft.world.InteractionHand.MAIN_HAND, dropped);
            if (event.isCancelled()) {
                cir.setReturnValue(InteractionResult.SUCCESS); // We need to return a success either way, because making it PASS or FAIL will result in a bug where cancelling while harvesting w/ block in hand places block
            }
            for (org.bukkit.inventory.ItemStack itemStack : event.getItemsHarvested()) {
                Block.popResource(level, pos, CraftItemStack.asNMSCopy(itemStack));
            }
        } else {
            return original.call(serverlevel, key, interactedBlockState, interactedBlockEntity, tool, interactingEntity, consumer);
        }
        return true;
    }
}
