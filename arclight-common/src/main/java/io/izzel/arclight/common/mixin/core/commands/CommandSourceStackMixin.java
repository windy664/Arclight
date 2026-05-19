package io.izzel.arclight.common.mixin.core.commands;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.tree.CommandNode;
import io.izzel.arclight.common.bridge.core.commands.CommandSourceStackBridge;
import io.izzel.arclight.common.bridge.core.command.CommandSourceBridge;
import io.izzel.arclight.common.bridge.core.server.level.ServerPlayerBridge;
import io.izzel.arclight.common.mod.compat.CommandNodeHooks;
import io.izzel.arclight.common.mod.server.command.ArclightDummyCommandSender;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.players.PlayerList;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.command.VanillaCommandWrapper;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import java.util.Objects;

@Mixin(CommandSourceStack.class)
public abstract class CommandSourceStackMixin implements CommandSourceStackBridge {

    // @formatter:off
    @Shadow @Final @Mutable public CommandSource source;
    @Shadow public abstract ServerLevel getLevel();
    // @formatter:on

    @Override
    public void bridge$setSource(CommandSource source) {
        this.source = source;
    }

    public CommandNode currentCommand;

    @Redirect(method = "broadcastToAdmins", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/players/PlayerList;isOp(Lcom/mojang/authlib/GameProfile;)Z"))
    private boolean arclight$feedbackPermission(PlayerList instance, GameProfile profile) {
        return ((ServerPlayerBridge) instance.getPlayer(profile.getId())).bridge$getBukkitEntity().hasPermission("minecraft.admin.command_feedback");
    }

    @Override
    public CommandNode<?> bridge$getCurrentCommand() {
        if (currentCommand == null) {
            return CommandNodeHooks.getCurrent();
        } else {
            return currentCommand;
        }
    }

    @Override
    public void bridge$setCurrentCommand(CommandNode<?> node) {
        this.currentCommand = node;
    }

    @Override
    public CommandSender getBukkitSender() {
        var thus = (CommandSourceStack) (Object) this;
        var sender = ((CommandSourceBridge) this.source).bridge$getBukkitSender(thus);
        // It means that this is a custom CommandSource
        return Objects.requireNonNullElseGet(sender, () -> new ArclightDummyCommandSender(thus));
    }
}
