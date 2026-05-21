package io.izzel.arclight.common.bridge.core.world.entity;

import org.bukkit.craftbukkit.entity.CraftEntity;

public interface InternalEntityBridge {

    CraftEntity internal$getBukkitEntity();
}
