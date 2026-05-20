package io.izzel.arclight.common.mixin.core.world.clock;

import io.izzel.arclight.common.bridge.core.world.clock.ServerClockManagerBridge;
import net.minecraft.world.clock.ServerClockManager;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ServerClockManager.class)
public class ServerClockManagerMixin implements ServerClockManagerBridge {
}

