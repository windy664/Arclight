package io.izzel.arclight.common.mixin.bukkit.permissions;

import io.izzel.arclight.common.bridge.bukkit.PermissibleBaseBridge;
import org.bukkit.permissions.PermissibleBase;
import org.bukkit.permissions.ServerOperator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(PermissibleBase.class)
public abstract class PermissibleBaseMixin implements PermissibleBaseBridge {
    @Override
    @Accessor("opable")
    public abstract ServerOperator arclight$unwrap();
}
