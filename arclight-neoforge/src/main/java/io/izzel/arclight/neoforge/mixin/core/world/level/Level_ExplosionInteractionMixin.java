package io.izzel.arclight.neoforge.mixin.core.world.level;

import io.izzel.arclight.common.mod.mixins.annotation.TransformAccess;
import net.minecraft.world.level.Level;
import net.neoforged.fml.common.asm.enumextension.ExtensionInfo;
import net.neoforged.fml.common.asm.enumextension.IExtensibleEnum;
import net.neoforged.fml.common.asm.enumextension.IndexedEnum;
import net.neoforged.fml.common.asm.enumextension.NamedEnum;
import net.neoforged.fml.common.asm.enumextension.NetworkedEnum;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;

@NetworkedEnum(NetworkedEnum.NetworkCheck.BIDIRECTIONAL)
@IndexedEnum
@NamedEnum(1)
@Mixin(Level.ExplosionInteraction.class)
public enum Level_ExplosionInteractionMixin implements IExtensibleEnum {
    ;

    @TransformAccess(Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC)
    private static ExtensionInfo getExtensionInfo() {
        return ExtensionInfo.nonExtended(Level_ExplosionInteractionMixin.class);
    }
}
