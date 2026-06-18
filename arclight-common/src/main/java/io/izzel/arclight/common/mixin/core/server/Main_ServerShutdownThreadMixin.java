package io.izzel.arclight.common.mixin.core.server;

import org.spigotmc.AsyncCatcher;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "net/minecraft/server/Main$1")
public class Main_ServerShutdownThreadMixin {

    @Inject(method = "run", require = 0, at = @At("HEAD"))
    private void arclight$shutdown(CallbackInfo ci) throws Throwable {
        AsyncCatcher.enabled = false;
    }
}