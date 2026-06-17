package io.izzel.arclight.common.bridge.core.world.entity.player;

import com.mojang.datafixers.util.Either;
import io.izzel.arclight.common.bridge.core.world.entity.LivingEntityBridge;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Unit;
import net.minecraft.world.entity.player.Player;
import org.bukkit.craftbukkit.entity.CraftHumanEntity;
import org.bukkit.event.entity.EntityExhaustionEvent;

public interface PlayerBridge extends LivingEntityBridge {

    default boolean bridge$isFauxSleeping() {
        return false;
    }

    @Override
    default CraftHumanEntity getBukkitEntity() {
        return null;
    }

    default Either<Player.BedSleepingProblem, Unit> bridge$trySleep(BlockPos at, boolean force) {
        return null;
    }

    default void bridge$pushExhaustReason(EntityExhaustionEvent.ExhaustionReason reason) {

    }

    default double bridge$platform$getBlockReach() {
        return 0;
    }

    default boolean bridge$platform$mayfly() {
        return ((Player) this).getAbilities().mayfly;
    }
}
