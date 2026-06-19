package io.izzel.arclight.common.mixin.vanilla.server.dedicated;

import io.izzel.arclight.common.mod.util.ArclightTerminalReader;
import net.minecraft.server.dedicated.DedicatedServer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "net/minecraft/server/dedicated/DedicatedServer$1")
public class DedicatedServer_ServerInputThreadMixin_Vanilla {

    @Shadow
    @Final
    DedicatedServer this$0;

    @Inject(method = "run", cancellable = true, at = @At("HEAD"))
    private void arclight$terminalConsole(CallbackInfo ci) {
        if (ArclightTerminalReader.handleCommands(this$0)) {
            ci.cancel();
        }
    }
}