package io.izzel.arclight.common.mixin.core.network.syncher;

import io.izzel.arclight.common.bridge.core.network.syncher.SynchedEntityDataBridge;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.SyncedDataHolder;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(SynchedEntityData.class)
public abstract class SynchedEntityDataMixin implements SynchedEntityDataBridge {

    @Shadow
    protected abstract <T> SynchedEntityData.DataItem<T> getItem(EntityDataAccessor<T> accessor);

    @Shadow
    private boolean isDirty;

    @Shadow
    public abstract @Nullable List<SynchedEntityData.DataValue<?>> getNonDefaultValues();

    @Shadow
    @Final
    private SyncedDataHolder entity;

    @Inject(method = "set(Lnet/minecraft/network/syncher/EntityDataAccessor;Ljava/lang/Object;Z)V", at = @At("HEAD"))
    private <T> void arclight$syncHealth(EntityDataAccessor<T> accessor, T value, boolean forceDirty, CallbackInfo ci) {
        if (accessor == LivingEntity.DATA_HEALTH_ID && this.entity instanceof ServerPlayer
                && ((ServerPlayer) this.entity).bridge$initialized()) {
            CraftPlayer player = ((ServerPlayer) this.entity).getBukkitEntity();
            player.setRealHealth(((Float) value));
        }
    }

    @Override
    // CraftBukkit start - add method from above
    public <T> void markDirty(EntityDataAccessor<T> entitydataaccessor) {
        this.getItem(entitydataaccessor).setDirty(true);
        this.isDirty = true;
    }
    // CraftBukkit end

    @Override
    public void refresh(ServerPlayer player) {
        var list = this.getNonDefaultValues();
        if (list != null && this.entity instanceof Entity entity) {
            player.connection.send(new ClientboundSetEntityDataPacket(entity.getId(), list));
        }
    }
}
