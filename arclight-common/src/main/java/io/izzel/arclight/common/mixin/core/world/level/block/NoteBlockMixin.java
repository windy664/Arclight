package io.izzel.arclight.common.mixin.core.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.NoteBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.redstone.Orientation;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.bukkit.event.block.NotePlayEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(NoteBlock.class)
public class NoteBlockMixin {

    @Inject(method = "neighborChanged", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/NoteBlock;playNote(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;)V", shift = At.Shift.AFTER))
    private void arclight$updateState(BlockState state, Level level, BlockPos pos, Block block, Orientation orientation, boolean movedByPiston, CallbackInfo ci) {
        state = level.getBlockState(pos); // CraftBukkit - SPIGOT-5617: update in case changed in event
    }

    @Inject(method = "playNote", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;blockEvent(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/Block;II)V"), cancellable = true)
    private void arclight$callNotePlayEvent(Entity source, BlockState state, Level level, BlockPos pos, CallbackInfo ci) {
        // CraftBukkit start
        NotePlayEvent event = CraftEventFactory.callNotePlayEvent(level, pos, state.getValue(NoteBlock.INSTRUMENT), state.getValue(NoteBlock.NOTE));
        if (event.isCancelled()) {
            ci.cancel();
            return;
        }
        // CraftBukkit end
    }
}
