package io.izzel.arclight.common.mixin.core.world.entity;

import io.izzel.arclight.api.ArclightPlatform;
import io.izzel.arclight.common.bridge.core.command.ICommandSourceBridge;
import io.izzel.arclight.common.bridge.core.entity.EntityBridge;
import io.izzel.arclight.common.bridge.core.entity.InternalEntityBridge;
import io.izzel.arclight.common.mod.mixins.annotation.OnlyInPlatform;
import io.izzel.arclight.mixin.Decorate;
import io.izzel.arclight.mixin.DecorationOps;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Entity.class)
@OnlyInPlatform(value = {ArclightPlatform.VANILLA, ArclightPlatform.FABRIC})
public abstract class EntityMixin_Vanilla implements InternalEntityBridge, EntityBridge, ICommandSourceBridge {

    @Decorate(method = "updateFluidHeightAndDoFluidPushing", require = 0, at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/material/FluidState;getFlow(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/phys/Vec3;"))
    private Vec3 arclight$setLava(FluidState fluid, BlockGetter level, BlockPos pos) throws Throwable {
        if (fluid.getType().is(FluidTags.LAVA)) {
            bridge$setLastLavaContact(pos.immutable());
        }
        return (Vec3) DecorationOps.callsite().invoke(fluid, level, pos);
    }
}
