package io.izzel.arclight.common.mixin.core.util;

import io.izzel.arclight.common.bridge.core.util.TickThrottlerBridge;
import net.minecraft.util.TickThrottler;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(TickThrottler.class)
public class TickThrottlerMixin implements TickThrottlerBridge {
}
