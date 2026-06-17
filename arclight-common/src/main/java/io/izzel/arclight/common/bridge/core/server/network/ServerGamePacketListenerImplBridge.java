package io.izzel.arclight.common.bridge.core.server.network;

import io.izzel.arclight.common.bridge.core.server.network.ServerCommonPacketListenerImplBridge;
import io.izzel.tools.product.Product;
import io.izzel.tools.product.Product3;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.bukkit.Location;
import org.bukkit.event.player.PlayerTeleportEvent;

public interface ServerGamePacketListenerImplBridge extends ServerCommonPacketListenerImplBridge {

    default void bridge$pushTeleportCause(PlayerTeleportEvent.TeleportCause cause) {

    }

    default void bridge$teleport(Location dest) {

    }

    default void bridge$pushNoTeleportEvent() {

    }

    default boolean bridge$teleportCancelled() {
        return false;
    }

    default Product3<Boolean /* Cancelled */, ItemStack /* SwappedToMainHand */, ItemStack /* SwappedToOffHand */>
    bridge$platform$canSwapHandItems(LivingEntity entity) {
        return Product.of(false, entity.getOffhandItem(), entity.getMainHandItem());
    }

    default InteractionResult bridge$platform$onInteractEntityAt(ServerPlayer player, Entity entity, Vec3 vec,
                                                                 InteractionHand interactionHand) {
        return null;
    }

    default void arclight$platform$setLastPosX(double d) {

    }

    default void arclight$platform$setLastPosY(double d) {

    }

    default void arclight$platform$setLastPosZ(double d) {

    }

    default void arclight$platform$setLastPitch(float f) {

    }

    default void arclight$platform$setLastYaw(float f) {

    }
}
