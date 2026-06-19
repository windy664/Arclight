package io.izzel.arclight.common.mixin.core.world.level.block;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Cancellable;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageSources;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SweetBerryBushBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootTable;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.event.player.PlayerHarvestBlockEvent;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.LinkedList;
import java.util.List;
import java.util.function.BiConsumer;

@Mixin(SweetBerryBushBlock.class)
public class SweetBerryBushBlockMixin {

    @Redirect(method = "randomTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z"))
    private boolean arclight$handleBlockGrowEvent(ServerLevel instance, BlockPos blockPos, BlockState state, int i, @Cancellable CallbackInfo ci) {
        if (!CraftEventFactory.handleBlockGrowEvent(instance, blockPos, state, i)) ci.cancel(); // CraftBukkit
        return true;
    }

    @WrapOperation(method = "entityInside", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/damagesource/DamageSources;sweetBerryBush()Lnet/minecraft/world/damagesource/DamageSource;"))
    private DamageSource arclight$putDmgSrc(DamageSources instance,
                                            Operation<DamageSource> original,
                                            @Local(argsOnly = true) Level level, @Local(argsOnly = true) BlockPos pos) {
        return original.call(instance).directBlock(level, pos);
    }

    @WrapOperation(method = "useWithoutItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/Block;dropFromBlockInteractLootTable(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/resources/ResourceKey;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/block/entity/BlockEntity;Lnet/minecraft/world/item/ItemInstance;Lnet/minecraft/world/entity/Entity;Ljava/util/function/BiConsumer;)Z"))
    private boolean arclight$callPlayerHarvestBlockEvent(ServerLevel level, ResourceKey<LootTable> key, BlockState interactedBlockState,
                                                         @Nullable BlockEntity interactedBlockEntity, @Nullable ItemInstance tool,
                                                         @Nullable Entity interactingEntity, BiConsumer<ServerLevel, ItemStack> consumer,
                                                         Operation<Boolean> original,
                                                         @Local(argsOnly = true) BlockPos pos,
                                                         @Local(argsOnly = true) Player player,
                                                         @Cancellable CallbackInfoReturnable<InteractionResult> cir) {
        // CraftBukkit start - useWithoutItem is always MAIN_HAND
        List<ItemStack> dropped = new LinkedList<>();
        Block.dropFromBlockInteractLootTable(level, key, interactedBlockState, interactedBlockEntity, tool, interactingEntity, (serverLevel, itemStack) -> dropped.add(itemStack));
        PlayerHarvestBlockEvent event = CraftEventFactory.callPlayerHarvestBlockEvent(level, pos, player, InteractionHand.MAIN_HAND, dropped);
        if (event.isCancelled()) {
            cir.setReturnValue(InteractionResult.SUCCESS); // We need to return a success either way, because making it PASS or FAIL will result in a bug where cancelling while harvesting w/ block in hand places block
        }
        for (org.bukkit.inventory.ItemStack itemStack : event.getItemsHarvested()) {
            Block.popResource(level, pos, CraftItemStack.asNMSCopy(itemStack));
        }
        // CraftBukkit end
        return true;
    }
}
