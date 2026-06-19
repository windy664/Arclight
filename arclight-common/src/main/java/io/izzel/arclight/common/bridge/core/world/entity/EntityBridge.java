package io.izzel.arclight.common.bridge.core.world.entity;

import io.izzel.arclight.common.bridge.core.command.CommandSourceBridge;
import io.izzel.arclight.common.mod.server.entity.ArclightSpawnReason;
import io.izzel.tools.product.Product;
import io.izzel.tools.product.Product4;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.boss.enderdragon.EnderDragonPart;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import org.bukkit.Location;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.craftbukkit.event.CraftPortalEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityRemoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.projectiles.ProjectileSource;

import java.util.List;

public interface EntityBridge extends CommandSourceBridge {

    default float getBukkitYaw() {
        return 0;
    }

    default boolean isChunkLoaded() {
        return false;
    }

    default int getDefaultMaxAirSupply() {
        return 0;
    }

    default void bridge$setOnFire(float seconds, boolean callEvent) {

    }

    default CraftEntity getBukkitEntity() {
        return null;
    }

    default void bridge$setBukkitEntity(CraftEntity craftEntity) {

    }

    default boolean bridge$isPersist() {
        return false;
    }

    default void bridge$setPersist(boolean persist) {

    }

    default boolean bridge$isValid() {
        return false;
    }

    default void bridge$setValid(boolean valid) {

    }

    default boolean bridge$isInWorld() {
        return false;
    }

    default void bridge$setInWorld(boolean inWorld) {

    }

    default ProjectileSource bridge$getProjectileSource() {
        return null;
    }

    default void bridge$setProjectileSource(ProjectileSource projectileSource) {

    }

    default float bridge$getBukkitYaw() {
        return 0;
    }

    default boolean bridge$isChunkLoaded() {
        return false;
    }

    default boolean bridge$isLastDamageCancelled() {
        return false;
    }

    default void bridge$setLastDamageCancelled(boolean cancelled) {

    }

    default void bridge$postTick() {

    }

    default List<Entity> bridge$getPassengers() {
        return null;
    }

    default void bridge$setRideCooldown(int rideCooldown) {

    }

    default int bridge$getRideCooldown() {
        return 0;
    }

    default void bridge$setLastLavaContact(BlockPos pos) {

    }

    default void bridge$revive() {

    }

    default void bridge$pushEntityRemoveCause(EntityRemoveEvent.Cause cause) {

    }

    default CraftPortalEvent bridge$callPortalEvent(Entity entity, Location exit, PlayerTeleportEvent.TeleportCause cause, int searchRadius, int creationRadius) {
        return null;
    }

    default boolean bridge$pluginRemoved() {
        return false;
    }

    default boolean bridge$isForceDrops() {
        return false;
    }

    default void bridge$setForceDrops(boolean b) {

    }

    default boolean bridge$forge$isPartEntity() {
        return this instanceof EnderDragonPart;
    }

    default Entity bridge$forge$getParent() {
        return this instanceof EnderDragonPart part ? part.parentMob : null;
    }

    default Entity[] bridge$forge$getParts() {
        return this instanceof EnderDragon dragon ? dragon.subEntities : null;
    }

    default Product4<Boolean /* Cancelled */, Double /* X */, Double /* Y */, Double /* Z */>
    bridge$onEntityTeleportCommand(double x, double y, double z) {
        return Product.of(false, x, y, z);
    }

    default boolean bridge$forge$canUpdate() {
        return true;
    }

    default void arclight$pushAddEntityReason(CreatureSpawnEvent.SpawnReason reason) {

    }

    default CreatureSpawnEvent.SpawnReason arclight$getAddEntityReason() {
        return null;
    }

    default void arclight$pushExtraSpawnReason(ArclightSpawnReason reason) {

    }

    default ArclightSpawnReason arclight$getExtraSpawnReason() {
        return null;
    }

    default ItemEntity arclight$spawnAtLocationNoAdd(ItemStack stack, float yOffset) {
        return null;
    }

    default ItemEntity arclight$spawnAtLocationNoAdd(ItemStack stack) {
        return arclight$spawnAtLocationNoAdd(stack, 0f);
    }

    /**
     * Called when an Entity is added to a ServerLevel via {@link net.minecraft.server.level.ServerLevel#addEntity(Entity)}.
     * If entity is discarded before it can enter the level, the remove event will be wrongly sent (before it's actually added).
     * And in the case when used by world generation, the server may crash for triggering {@link org.bukkit.event.entity.EntityRemoveEvent}
     * asynchronously.
     * We maintain whether it's "in the level" here, recording whether the event has been sent, with the assumption that an entity
     * is only removed from the main thread, once it's added to the world. This will solve the problem above and more potential problems.
     */
    @SuppressWarnings("JavadocReference")
    default void arclight$onAddedToLevel() {

    }
}
