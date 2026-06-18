package io.izzel.arclight.fabric.mixin.bukkit;

import io.izzel.arclight.common.bridge.bukkit.PermissibleBaseBridge;
import io.izzel.arclight.fabric.mod.permission.ArclightFabricPermissible;
import io.izzel.arclight.i18n.ArclightConfig;
import net.minecraft.world.entity.Entity;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.permissions.*;
import org.bukkit.plugin.Plugin;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.Set;

@Mixin(CraftEntity.class)
public abstract class CraftEntityMixin_Fabric {

    @Shadow
    private static PermissibleBase getPermissibleBase() {
        return null;
    }

    @Shadow
    public abstract Entity getHandle();

    @Unique
    private Permissible arclight$permissible;

    @Unique
    private Permissible arclight$getOrCreatePermissible() {
        final var base = getPermissibleBase();
        assert base != null;
        if (!ArclightConfig.spec().getCompat().isForwardPermissionReverse()) {
            return base;
        }
        if (arclight$permissible == null) {
            final var opable = ((PermissibleBaseBridge) base).arclight$unwrap();
            return arclight$permissible = new ArclightFabricPermissible(opable, getHandle());
        }
        return arclight$permissible;
    }

    /**
     * @author InitAuther97
     * @reason use stronger implementation
     */
    @Overwrite
    public boolean isPermissionSet(String name) {
        return arclight$getOrCreatePermissible().isPermissionSet(name);
    }

    /**
     * @author InitAuther97
     * @reason use stronger implementation
     */
    @Overwrite
    public boolean isPermissionSet(Permission perm) {
        return arclight$getOrCreatePermissible().isPermissionSet(perm);
    }

    /**
     * @author InitAuther97
     * @reason use stronger implementation
     */
    @Overwrite
    public boolean hasPermission(String name) {
        return arclight$getOrCreatePermissible().hasPermission(name);
    }

    /**
     * @author InitAuther97
     * @reason use stronger implementation
     */
    @Overwrite
    public boolean hasPermission(Permission perm) {
        return arclight$getOrCreatePermissible().hasPermission(perm);
    }

    /**
     * @author InitAuther97
     * @reason use stronger implementation
     */
    @Overwrite
    public PermissionAttachment addAttachment(Plugin plugin, String name, boolean value) {
        return arclight$getOrCreatePermissible().addAttachment(plugin, name, value);
    }

    /**
     * @author InitAuther97
     * @reason use stronger implementation
     */
    @Overwrite
    public PermissionAttachment addAttachment(Plugin plugin) {
        return arclight$getOrCreatePermissible().addAttachment(plugin);
    }

    /**
     * @author InitAuther97
     * @reason use stronger implementation
     */
    @Overwrite
    public PermissionAttachment addAttachment(Plugin plugin, String name, boolean value, int ticks) {
        return arclight$getOrCreatePermissible().addAttachment(plugin, name, value, ticks);
    }

    /**
     * @author InitAuther97
     * @reason use stronger implementation
     */
    @Overwrite
    public PermissionAttachment addAttachment(Plugin plugin, int ticks) {
        return arclight$getOrCreatePermissible().addAttachment(plugin, ticks);
    }

    /**
     * @author InitAuther97
     * @reason use stronger implementation
     */
    @Overwrite
    public void removeAttachment(PermissionAttachment attachment) {
        arclight$getOrCreatePermissible().removeAttachment(attachment);
    }

    /**
     * @author InitAuther97
     * @reason use stronger implementation
     */
    @Overwrite
    public void recalculatePermissions() {
        arclight$getOrCreatePermissible().recalculatePermissions();
    }

    /**
     * @author InitAuther97
     * @reason use stronger implementation
     */
    @Overwrite
    public Set<PermissionAttachmentInfo> getEffectivePermissions() {
        return arclight$getOrCreatePermissible().getEffectivePermissions();
    }

    /**
     * @author InitAuther97
     * @reason use stronger implementation
     */
    @Overwrite
    public boolean isOp() {
        return arclight$getOrCreatePermissible().isOp();
    }

    /**
     * @author InitAuther97
     * @reason use stronger implementation
     */
    @Overwrite
    public void setOp(boolean value) {
        arclight$getOrCreatePermissible().setOp(value);
    }
}
