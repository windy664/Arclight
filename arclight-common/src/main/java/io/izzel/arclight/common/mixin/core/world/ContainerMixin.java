package io.izzel.arclight.common.mixin.core.world;

import io.izzel.arclight.common.bridge.core.world.ContainerBridge;
import net.minecraft.world.Container;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Container.class)
public interface ContainerMixin extends ContainerBridge {
}
