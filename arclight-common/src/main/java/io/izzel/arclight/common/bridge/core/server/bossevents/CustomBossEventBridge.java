package io.izzel.arclight.common.bridge.core.server.bossevents;

import org.bukkit.boss.KeyedBossBar;

public interface CustomBossEventBridge {

    default KeyedBossBar bridge$getBossBar() {
        return null;
    }

    default void bridget$setBossBar(KeyedBossBar bossBar) {

    }

    default KeyedBossBar getBukkitEntity() {
        return null;
    }
}
