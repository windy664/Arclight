package io.izzel.arclight.common.bridge.core.world.damagesource;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public interface DamageSourcesBridge {

    default DamageSource melting() {
        return null;
    }

    default DamageSource poison() {
        return null;
    }

    default DamageSource explosion(@Nullable Entity entity, @Nullable Entity entity1, ResourceKey<DamageType> resourceKey) {
        return null;
    }

    default DamageSource badRespawnPointExplosion(Vec3 vec3, org.bukkit.block.BlockState blockState) {
        return null;
    }
}
