package io.izzel.arclight.common.mixin.bukkit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.command.ColouredConsoleSender;
import org.jline.terminal.Terminal;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = ColouredConsoleSender.class, remap = false)
public class ColouredConsoleSenderMixin extends CraftConsoleCommandSenderMixin {

    private static final Logger LOGGER = LogManager.getLogger("Console");

    @Redirect(method = "<init>", at = @At(value = "INVOKE", target = "Lorg/bukkit/craftbukkit/CraftServer;getTerminal()Lorg/jline/terminal/Terminal;"))
    private Terminal arclight$terminal(CraftServer instance) {
        return null;
    }

    /**
     * @author IzzelAliz
     * @reason use TerminalConsoleAppender
     */
    @Overwrite
    public void sendMessage(String message) {
        if (!this.conversationTracker.isConversingModaly()) {
            LOGGER.info(message);
        }
    }
}
