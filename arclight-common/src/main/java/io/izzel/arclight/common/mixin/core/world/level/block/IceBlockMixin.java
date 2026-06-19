package io.izzel.arclight.common.mixin.core.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.attribute.EnvironmentAttributes;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.IceBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(IceBlock.class)
public abstract class IceBlockMixin {

    @Shadow
    public static BlockState meltsInto() {
        return null;
    }

    @Inject(method = "melt", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/attribute/EnvironmentAttributeSystem;getValue(Lnet/minecraft/world/attribute/EnvironmentAttribute;Lnet/minecraft/core/BlockPos;)Ljava/lang/Object;"), cancellable = true)
    private void arclight$callBlockFadeEvent(BlockState state, Level level, BlockPos pos, CallbackInfo ci) {
        // CraftBukkit start
        if (org.bukkit.craftbukkit.event.CraftEventFactory.callBlockFadeEvent(level, pos, level.environmentAttributes().getValue(EnvironmentAttributes.WATER_EVAPORATES, pos) ? Blocks.AIR.defaultBlockState() : meltsInto()).isCancelled()) {
            ci.cancel();
            return;
        }
        // CraftBukkit end
    }
}
