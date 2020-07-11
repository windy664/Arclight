package io.izzel.arclight.impl.mixin.v1_14.core.world.dimension;

import io.izzel.arclight.common.bridge.world.dimension.DimensionTypeBridge;
import net.minecraft.world.World;
import net.minecraft.world.dimension.Dimension;
import net.minecraft.world.dimension.DimensionType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.BiFunction;

@Mixin(DimensionType.class)
public class DimensionTypeMixin_1_14 implements DimensionTypeBridge {

    protected void arclight$constructor(int idIn, String suffixIn, String directoryIn, BiFunction<World, DimensionType, ? extends Dimension> p_i49935_4_, boolean p_i49935_5_) {
        throw new RuntimeException();
    }

    public void arclight$constructor(int idIn, String suffixIn, String directoryIn, BiFunction<World, DimensionType, ? extends Dimension> p_i49935_4_, boolean p_i49935_5_, DimensionType type) {
        arclight$constructor(idIn, suffixIn, directoryIn, p_i49935_4_, p_i49935_5_);
        this.type = type;
    }

    private DimensionType type;

    public DimensionType getType() {
        return (type == null) ? (DimensionType) (Object) this : type;
    }

    @Override
    public void bridge$setType(DimensionType type) {
        this.type = type;
    }

    @Override
    public DimensionType bridge$getType() {
        return getType();
    }

    @Inject(method = "isVanilla", remap = false, cancellable = true, at = @At("HEAD"))
    private void arclight$vanillaCheck(CallbackInfoReturnable<Boolean> cir) {
        if (this.type != null) {
            cir.setReturnValue(this.type.isVanilla());
        }
    }
}
