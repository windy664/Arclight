package io.izzel.arclight.common.mixin.core.world.level;

import io.izzel.arclight.common.bridge.core.world.level.LevelWriterBridge;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.LevelWriter;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(LevelWriter.class)
public interface LevelWriterMixin extends LevelWriterBridge {

    // CraftBukkit start
    @Override
    default boolean addFreshEntity(Entity entity, org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason reason) {
        return false;
    }
    // CraftBukkit end
}
