package io.izzel.arclight.common.bridge.core.server.level;

import com.mojang.datafixers.util.Either;
import io.izzel.arclight.common.bridge.core.world.entity.player.PlayerBridge;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import org.bukkit.Location;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerSpawnChangeEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.Optional;

public interface ServerPlayerBridge extends PlayerBridge {

    default <L, R> Either<L, R> bridge$fireBedEvent(Either<L, R> e, BlockPos pos) {
        return null;
    }

    @Override
    default CraftPlayer getBukkitEntity() {
        return null;
    }

    default void bridge$pushChangeDimensionCause(PlayerTeleportEvent.TeleportCause cause) {

    }

    default void bridge$pushChangeSpawnCause(PlayerSpawnChangeEvent.Cause cause) {

    }

    default Optional<PlayerTeleportEvent.TeleportCause> bridge$getTeleportCause() {
        return null;
    }

    default void bridge$pushRespawnReason(PlayerRespawnEvent.RespawnReason respawnReason) {

    }

    default void bridge$setTransferCookieConnection(CraftPlayer.TransferCookieConnection transferCookieConnection) {

    }

    default CraftPlayer.TransferCookieConnection bridge$getTransferCookieConnection() {
        return null;
    }

    default void bridge$resendItemInHands() {

    }

    default BlockPos bridge$getSpawnPoint(ServerLevel world) {
        return null;
    }

    default boolean bridge$isMovementBlocked() {
        return false;
    }

    default void bridge$setCompassTarget(Location location) {

    }

    default boolean bridge$isJoining() {
        return false;
    }

    default void bridge$reset() {

    }

    default boolean bridge$initialized() {
        return false;
    }

    default boolean bridge$isTrackerDirty() {
        return false;
    }

    default void bridge$setTrackerDirty(boolean flag) {

    }

    default boolean arclight$isKeepLevel() {
        return false;
    }

    default void arclight$readDeathEvent(PlayerDeathEvent event) {

    }

    interface RespawnPosAngleBridge {

        default boolean bridge$isBedSpawn() {
            return false;
        }

        default boolean bridge$isAnchorSpawn() {
            return false;
        }

        default void bridge$setBedSpawn(boolean b) {

        }

        default void bridge$setAnchorSpawn(boolean b) {

        }
    }
}
