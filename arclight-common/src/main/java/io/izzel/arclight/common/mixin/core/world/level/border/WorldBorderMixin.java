package io.izzel.arclight.common.mixin.core.world.level.border;

import io.izzel.arclight.common.bridge.core.world.level.border.WorldBorderBridge;
import net.minecraft.world.level.border.BorderChangeListener;
import net.minecraft.world.level.border.WorldBorder;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(WorldBorder.class)
public abstract class WorldBorderMixin implements WorldBorderBridge {

    @Shadow
    @Final
    private List<BorderChangeListener> listeners;

    @Shadow
    private boolean initialized;

    @Shadow
    public abstract void setCenter(double x, double z);

    @Shadow
    public abstract void setDamagePerBlock(double damagePerBlock);

    @Shadow
    public abstract void setSafeZone(double safeZone);

    @Shadow
    public abstract void setWarningBlocks(int warningBlocks);

    @Shadow
    public abstract void setWarningTime(int warningTime);

    @Shadow
    @Final
    private WorldBorder.Settings settings;

    @Shadow
    public abstract void lerpSizeBetween(double from, double to, long ticks, long gameTime);

    @Shadow
    public abstract void setSize(double size);

    @Inject(method = "addListener", cancellable = true, at = @At("HEAD"))
    private void arclight$removeDuplicateListener(BorderChangeListener listener, CallbackInfo ci) {
        if (listeners.contains(listener)) {
            ci.cancel();
        }
    }

    @Override
    public void applyInitialSettings(long gameTime, boolean force) {
        if (!this.initialized || force) {
            this.setCenter(this.settings.centerX(), this.settings.centerZ());
            this.setDamagePerBlock(this.settings.damagePerBlock());
            this.setSafeZone(this.settings.safeZone());
            this.setWarningBlocks(this.settings.warningBlocks());
            this.setWarningTime(this.settings.warningTime());
            if (this.settings.lerpTime() > 0L) {
                this.lerpSizeBetween(this.settings.size(), this.settings.lerpTarget(), this.settings.lerpTime(), gameTime);
            } else {
                this.setSize(this.settings.size());
            }

            this.initialized = true;
        }

    }
}
