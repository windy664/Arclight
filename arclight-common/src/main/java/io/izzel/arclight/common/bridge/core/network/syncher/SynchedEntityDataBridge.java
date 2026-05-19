package io.izzel.arclight.common.bridge.core.network.syncher;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.server.level.ServerPlayer;

public interface SynchedEntityDataBridge {

    default <T> void markDirty(EntityDataAccessor<T> entitydataaccessor) {

    }

    default void refresh(ServerPlayer player) {

    }
}
