package io.izzel.arclight.common.bridge.core.world.entity.raid;

import java.util.Collection;
import net.minecraft.world.entity.raid.Raider;

public interface RaidBridge {

    default Collection<Raider> bridge$getRaiders() {
        return null;
    }
}
