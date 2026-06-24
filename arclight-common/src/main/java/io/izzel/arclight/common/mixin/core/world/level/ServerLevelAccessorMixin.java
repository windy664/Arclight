package io.izzel.arclight.common.mixin.core.world.level;

import io.izzel.arclight.common.bridge.core.world.level.ServerLevelAccessorBridge;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerLevelAccessor.class)
public interface ServerLevelAccessorMixin extends LevelAccessor, ServerLevelAccessorBridge {

    @Shadow
    ServerLevel getLevel();

    @Inject(method = "addFreshEntityWithPassengers", at = @At("HEAD"))
    private void arclight$pushSpawnCause(Entity entity, CallbackInfo ci) {
        entity.getSelfAndPassengers().forEach(entity1 -> {
            entity1.arclight$pushAddEntityReason(CreatureSpawnEvent.SpawnReason.DEFAULT);
        });
    }

    @Override
    default void addFreshEntityWithPassengers(Entity entity, org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason reason) {
        entity.getSelfAndPassengers().forEach((e) -> this.addFreshEntity(e, reason));
    }

    @Override
    default ServerLevel getMinecraftWorld() {
        return getLevel();
    }
    // CraftBukkit end
}
