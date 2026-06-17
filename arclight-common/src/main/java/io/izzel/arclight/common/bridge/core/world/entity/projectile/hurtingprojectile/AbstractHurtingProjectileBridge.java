package io.izzel.arclight.common.bridge.core.world.entity.projectile.hurtingprojectile;

import io.izzel.arclight.common.bridge.core.world.entity.EntityBridge;

public interface AbstractHurtingProjectileBridge extends EntityBridge {

    default void bridge$setBukkitYield(float yield) {

    }
}
