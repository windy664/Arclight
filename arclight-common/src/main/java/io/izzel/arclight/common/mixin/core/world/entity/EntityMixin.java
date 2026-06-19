package io.izzel.arclight.common.mixin.core.world.entity;

import io.izzel.arclight.common.bridge.core.world.entity.EntityBridge;
import io.izzel.arclight.common.bridge.core.world.entity.InternalEntityBridge;
import io.izzel.arclight.common.bridge.optimization.EntityBridge_ActivationRange;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Entity.class)
public abstract class EntityMixin implements EntityBridge, EntityBridge_ActivationRange, InternalEntityBridge {

    @Shadow
    private Level level;
    @Shadow
    @Final
    public static int TOTAL_AIR_SUPPLY;
    @Shadow
    private float yRot;

    @Shadow
    public abstract double getX();

    @Shadow
    public abstract double getZ();

    // CraftBukkit start
    private static final int CURRENT_LEVEL = 2;
    private static boolean isLevelAtLeast(ValueInput tag, int level) {
        int updateLevel = tag.getIntOr("Bukkit.updateLevel", -1);
        return updateLevel != -1 && tag.getIntOr("Bukkit.updateLevel", -1) >= level;
    }

    private CraftEntity bukkitEntity;

    @Override
    public CraftEntity getBukkitEntity() {
        if (bukkitEntity == null) {
            bukkitEntity = CraftEntity.getEntity(level.getCraftServer(), ((Entity) (Object) this));
        }
        return bukkitEntity;
    }

    // CraftBukkit - SPIGOT-6907: re-implement LivingEntity#setMaximumAir()
    @Override
    public int getDefaultMaxAirSupply() {
        return TOTAL_AIR_SUPPLY;
    }
    // CraftBukkit end

    // CraftBukkit start
    public boolean forceDrops;
    public boolean persist = true;
    public boolean visibleByDefault = true;
    public boolean valid;
    public boolean inWorld = false;
    public boolean generation;
    public int maxAirTicks = getDefaultMaxAirSupply(); // CraftBukkit - SPIGOT-6907: re-implement LivingEntity#setMaximumAir()
    public org.bukkit.projectiles.ProjectileSource projectileSource; // For projectiles only
    public boolean lastDamageCancelled; // SPIGOT-5339, SPIGOT-6252, SPIGOT-6777: Keep track if the event was canceled
    public boolean persistentInvisibility = false;
    public BlockPos lastLavaContact;
    // Marks an entity, that it was removed by a plugin via Entity#remove
    // Main use case currently is for SPIGOT-7487, preventing dropping of leash when leash is removed
    public boolean pluginRemoved = false;

    @Override
    public float getBukkitYaw() {
        return this.yRot;
    }

    @Override
    public boolean bridge$isForceDrops() {
        return this.forceDrops;
    }

    @Override
    public void bridge$setForceDrops(boolean b) {
        this.forceDrops = b;
    }

    @Override
    public boolean bridge$isPersist() {
        return this.persist;
    }

    @Override
    public void bridge$setPersist(boolean persist) {
        this.persist = persist;
    }

    @Override
    public boolean isChunkLoaded() {
        return level.hasChunk((int) Math.floor(this.getX()) >> 4, (int) Math.floor(this.getZ()) >> 4);
    }
    // CraftBukkit end
}
