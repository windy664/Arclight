package io.izzel.arclight.common.mod.util;

import com.mojang.brigadier.tree.CommandNode;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.permissions.Permission;
import net.minecraft.server.permissions.PermissionSet;
import net.minecraft.server.permissions.Permissions;

public class ArclightDelegatePermissionSet implements PermissionSet {

    private final PermissionSet handle;
    private final CommandSourceStack sourceStack;

    public ArclightDelegatePermissionSet(PermissionSet handle, CommandSourceStack sourceStack) {
        this.handle = handle;
        this.sourceStack = sourceStack;
    }

    @Override
    public boolean hasPermission(Permission permission) {
        boolean hasPermission = handle.hasPermission(permission);

        CommandNode currentCommand = sourceStack.bridge$getCurrentCommand();
        if (currentCommand != null) {
            return hasPermission(hasPermission, org.bukkit.craftbukkit.command.VanillaCommandWrapper.getPermission(currentCommand));
        }

        if (permission.equals(Permissions.COMMANDS_ENTITY_SELECTORS)) {
            return hasPermission(hasPermission, "minecraft.command.selector");
        }

        return hasPermission;
    }

    public boolean hasPermission(boolean hasPermission, String bukkitPermission) {
        // World is null when loading functions
        return ((sourceStack.getLevel() == null || !sourceStack.getLevel().getCraftServer().ignoreVanillaPermissions) && hasPermission) || sourceStack.getBukkitSender().hasPermission(bukkitPermission);
    }
}
// CraftBukkit end