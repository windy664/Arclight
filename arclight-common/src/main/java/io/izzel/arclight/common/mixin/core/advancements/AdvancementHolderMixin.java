package io.izzel.arclight.common.mixin.core.advancements;

import io.izzel.arclight.common.bridge.core.advancements.AdvancementHolderBridge;
import net.minecraft.advancements.AdvancementHolder;
import org.bukkit.craftbukkit.advancement.CraftAdvancement;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(AdvancementHolder.class)
public class AdvancementHolderMixin implements AdvancementHolderBridge {

    @Unique
    @Override
    public org.bukkit.advancement.Advancement toBukkit() {
        return new CraftAdvancement((AdvancementHolder)(Object) this);
    }
}
