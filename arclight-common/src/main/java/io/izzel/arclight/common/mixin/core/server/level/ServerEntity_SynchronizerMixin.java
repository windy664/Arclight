package io.izzel.arclight.common.mixin.core.server.level;

import io.izzel.arclight.common.bridge.core.server.level.ServerEntity_SynchronizerBridge;
import net.minecraft.server.level.ServerEntity;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ServerEntity.Synchronizer.class)
public class ServerEntity_SynchronizerMixin implements ServerEntity_SynchronizerBridge {
}
