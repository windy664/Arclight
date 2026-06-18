package io.izzel.arclight.common.bridge.core.world.level;

import net.minecraft.world.entity.Entity;
import org.bukkit.event.entity.CreatureSpawnEvent;

public interface LevelWriterBridge {

    default boolean bridge$addEntity(Entity entity, CreatureSpawnEvent.SpawnReason reason) {
        return false;
    }

    default void bridge$pushAddEntityReason(CreatureSpawnEvent.SpawnReason reason) {

    }

    default CreatureSpawnEvent.SpawnReason bridge$getAddEntityReason() {
        return null;
    }

    // CraftBukkit start
    default boolean addFreshEntity(Entity entity, org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason reason) {
        return false;
    }
    // CraftBukkit end
}
