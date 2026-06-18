package io.izzel.arclight.fabric.mod.permission;

import net.fabricmc.fabric.api.permission.v1.PermissionContextOwner;
import net.fabricmc.fabric.api.util.TriState;
import net.minecraft.resources.Identifier;
import org.bukkit.permissions.PermissibleBase;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.ServerOperator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ArclightFabricPermissible extends PermissibleBase {

    private final PermissionContextOwner owner;

    public ArclightFabricPermissible(@Nullable ServerOperator opable, @NotNull PermissionContextOwner owner) {
        super(opable);
        this.owner = owner;
    }

    private TriState checkPerm(String name) {
        return owner.checkPermission(Identifier.fromNamespaceAndPath("bukkit", name));
    }

    @Override
    public boolean isPermissionSet(@NotNull String name) {
        return checkPerm(name) != TriState.DEFAULT;
    }

    @Override
    public boolean isPermissionSet(@NotNull Permission perm) {
        return checkPerm(perm.getName()) != TriState.DEFAULT;
    }

    @Override
    public boolean hasPermission(@NotNull String name) {
        return checkPerm(name).orElseGet(() -> super.hasPermission(name));
    }

    @Override
    public boolean hasPermission(@NotNull Permission perm) {
        return checkPerm(perm.getName()).orElseGet(() -> super.hasPermission(perm));
    }
}
