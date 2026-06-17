package io.izzel.arclight.neoforge.mixin.core.world.entity.vehicle;

import io.izzel.arclight.common.bridge.core.world.entity.vehicle.minecart.AbstractMinecartBridge;
import net.minecraft.world.entity.vehicle.minecart.AbstractMinecart;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(AbstractMinecart.class)
public abstract class AbstractMinecartMixin_NeoForge implements AbstractMinecartBridge {



    @Override
    public boolean bridge$forge$canUseRail() {
        return false;
    }
}
