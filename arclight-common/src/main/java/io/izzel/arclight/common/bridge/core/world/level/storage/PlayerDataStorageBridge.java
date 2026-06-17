package io.izzel.arclight.common.bridge.core.world.level.storage;

import java.io.File;
import net.minecraft.nbt.CompoundTag;

public interface PlayerDataStorageBridge {

    default File getPlayerDir() {
        return null;
    }

    default CompoundTag getPlayerData(String uuid) {
        return null;
    }
}
