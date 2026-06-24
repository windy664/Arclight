package io.izzel.arclight.common.bridge.core.world.level;

import net.minecraft.world.entity.Entity;

public interface ServerLevelAccessorBridge {

    default void addFreshEntityWithPassengers(Entity entity, org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason reason) {
    }
}
