package io.izzel.arclight.common.bridge.core.commands;

import com.mojang.brigadier.tree.CommandNode;
import net.minecraft.commands.CommandSource;
import net.minecraft.server.permissions.PermissionSet;
import org.bukkit.command.CommandSender;

public interface CommandSourceStackBridge {

    default PermissionSet bridge$getBukkitPermissions() {
        return null;
    }

    default void bridge$setBukkitPermissions(PermissionSet bukkitPermissions) {

    }

    default CommandNode<?> bridge$getCurrentCommand() {
        return null;
    }

    default void bridge$setCurrentCommand(CommandNode<?> node) {

    }

    default void bridge$setSource(CommandSource source) {

    }

    default CommandSender getBukkitSender() {
        return null;
    }
}
