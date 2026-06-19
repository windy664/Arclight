package io.izzel.arclight.common.mixin.core.server.level;

import io.izzel.arclight.common.bridge.core.server.level.TicketTypeBridge;
import io.izzel.arclight.common.mod.mixins.annotation.TransformAccess;
import net.minecraft.server.level.TicketType;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(TicketType.class)
public abstract class TicketTypeMixin implements TicketTypeBridge {

    @Shadow
    private static TicketType register(String name, long timeout, @TicketType.Flags int flags) {
        return null;
    }

    private static final int PLUGIN_FLAGS = TicketType.FLAG_LOADING | TicketType.FLAG_SIMULATION | TicketType.FLAG_CAN_EXPIRE_IF_UNLOADED;

    @Shadow
    @Final
    private long timeout;
    // CraftBukkit start
    @TransformAccess(Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC)
    private static long pluginTimeout = 0L;
    @TransformAccess(Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC)
    private static final TicketType PLUGIN = register("plugin", 0L, 14);
    @TransformAccess(Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC)
    private static final TicketType PLUGIN_TICKET = register("plugin_ticket", 0L, 14);

    @Override
    public long timeout() {
        return (((TicketType) (Object) this) != PLUGIN) ? this.timeout : pluginTimeout;
    }
    // CraftBukkit end

    @Override
    @Accessor("timeout")
    @Mutable
    public abstract void bridge$setLifespan(long lifespan);
}
