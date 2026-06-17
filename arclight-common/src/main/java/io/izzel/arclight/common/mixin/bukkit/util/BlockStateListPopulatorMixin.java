package io.izzel.arclight.common.mixin.bukkit.util;

import io.izzel.arclight.common.bridge.core.world.level.LevelWriterBridge;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.LevelAccessor;
import org.bukkit.craftbukkit.v.util.BlockStateListPopulator;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(BlockStateListPopulator.class)
public class BlockStateListPopulatorMixin implements LevelWriterBridge {
    @Shadow @Final private LevelAccessor world;

    @Override
    public boolean bridge$addEntity(Entity entity, CreatureSpawnEvent.SpawnReason reason) {
        return ((LevelWriterBridge) world).bridge$addEntity(entity, reason);
    }

    @Override
    public void bridge$pushAddEntityReason(CreatureSpawnEvent.SpawnReason reason) {
        ((LevelWriterBridge) world).bridge$pushAddEntityReason(reason);
    }

    @Override
    public CreatureSpawnEvent.SpawnReason bridge$getAddEntityReason() {
        return ((LevelWriterBridge) world).bridge$getAddEntityReason();
    }
}
