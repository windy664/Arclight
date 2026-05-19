package io.izzel.arclight.common.mixin.core.server.level;

import io.izzel.arclight.common.bridge.core.server.level.ServerChunkCacheBridge;
import net.minecraft.server.level.ServerChunkCache;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ServerChunkCache.class)
public class MixinServerChunkCache implements ServerChunkCacheBridge {
}
