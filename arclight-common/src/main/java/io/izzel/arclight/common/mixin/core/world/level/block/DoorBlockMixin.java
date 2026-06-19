package io.izzel.arclight.common.mixin.core.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.redstone.Orientation;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(DoorBlock.class)
public abstract class DoorBlockMixin {

    @Shadow
    @Final
    public static BooleanProperty OPEN;

    @Shadow
    protected abstract void playSound(@Nullable Entity entity, Level level, BlockPos pos, boolean open);

    @Shadow
    @Final
    public static BooleanProperty POWERED;

    /**
     * @author wdog5734
     * @reason Bukkit
     */
    @Overwrite
    protected void neighborChanged(final BlockState state, final Level level, final BlockPos pos, final Block block, final @Nullable Orientation orientation, final boolean movedByPiston) {
        // CraftBukkit start
        BlockPos otherHalf = pos.relative(state.getValue(DoorBlock.HALF) == DoubleBlockHalf.LOWER ? Direction.UP : Direction.DOWN);

        org.bukkit.World bworld = level.getWorld();
        org.bukkit.block.Block bukkitBlock = bworld.getBlockAt(pos.getX(), pos.getY(), pos.getZ());
        org.bukkit.block.Block blockTop = bworld.getBlockAt(otherHalf.getX(), otherHalf.getY(), otherHalf.getZ());

        int power = bukkitBlock.getBlockPower();
        int powerTop = blockTop.getBlockPower();
        if (powerTop > power) power = powerTop;
        int oldPower = state.getValue(DoorBlock.POWERED) ? 15 : 0;

        if (oldPower == 0 ^ power == 0) {
            BlockRedstoneEvent eventRedstone = new BlockRedstoneEvent(bukkitBlock, oldPower, power);
            level.getCraftServer().getPluginManager().callEvent(eventRedstone);

            boolean signal = eventRedstone.getNewCurrent() > 0;
            // CraftBukkit end
            if (signal != state.getValue(OPEN)) {
                this.playSound(null, level, pos, signal);
                level.gameEvent(null, signal ? GameEvent.BLOCK_OPEN : GameEvent.BLOCK_CLOSE, pos);
            }

            level.setBlock(pos, state.setValue(POWERED, signal).setValue(OPEN, signal), 2);
        }

    }
}
