package io.izzel.arclight.common.mixin.core.world.level.block;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageSources;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.MagmaBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(MagmaBlock.class)
public class MagmaBlockMixin {

    @WrapOperation(method = "stepOn", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/damagesource/DamageSources;hotFloor()Lnet/minecraft/world/damagesource/DamageSource;"))
    private DamageSource arclight$putDmgSrc(DamageSources instance,
                                            Operation<DamageSource> original,
                                            @Local(argsOnly = true) Level level,
                                            @Local(argsOnly = true) BlockPos pos) {
        return original.call(instance).directBlock(level, pos);
    }
}
