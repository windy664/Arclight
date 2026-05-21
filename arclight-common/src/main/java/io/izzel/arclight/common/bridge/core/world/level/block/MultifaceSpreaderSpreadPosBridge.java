package io.izzel.arclight.common.bridge.core.world.level.block;

import net.minecraft.core.BlockPos;

public interface MultifaceSpreaderSpreadPosBridge {
    default BlockPos source() {
        return null;
    }

    default void arclight$setSource(BlockPos source) {

    }
}
