package io.izzel.arclight.fabric.mixin.core.world.level;

import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Level.ExplosionInteraction.class)
public enum Level_ExplosionInteractionMixin {
    STANDARD("standard"); // CraftBukkit - Add STANDARD which will always use Explosion.Effect.DESTROY

    @SuppressWarnings("all")
    @Shadow
    Level_ExplosionInteractionMixin(final String id) {
    }
}
