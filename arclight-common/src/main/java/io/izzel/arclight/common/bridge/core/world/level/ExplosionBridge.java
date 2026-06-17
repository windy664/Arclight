package io.izzel.arclight.common.bridge.core.world.level;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;

import java.util.List;

public interface ExplosionBridge {

    default Entity bridge$getExploder() {
        return null;
    }

    default float bridge$getSize() {
        return 0;
    }

    default void bridge$setSize(float size) {

    }

    default Explosion.BlockInteraction bridge$getMode() {
        return null;
    }

    default boolean bridge$wasCancelled() {
        return false;
    }

    default float bridge$getYield() {
        return 0;
    }

    default void bridge$forge$onExplosionDetonate(Level level, Explosion explosion, List<Entity> list, double diameter) {}
}
