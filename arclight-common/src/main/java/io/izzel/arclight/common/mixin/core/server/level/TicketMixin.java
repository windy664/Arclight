package io.izzel.arclight.common.mixin.core.server.level;

import io.izzel.arclight.common.bridge.core.server.level.TicketBridge;
import io.izzel.arclight.common.mod.mixins.annotation.TransformAccess;
import net.minecraft.server.level.Ticket;
import net.minecraft.server.level.TicketType;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Ticket.class)
public class TicketMixin implements TicketBridge {

    // CraftBukkit start
    public Object key;

    @TransformAccess(Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC)
    private static Ticket of(TicketType tickettype, int i, Object key) {
        Ticket ticket = new Ticket(tickettype, i);
        ticket.bridge$setKey(key);
        return ticket;
    }
    // CraftBukkit end

    public Object bridge$getKey() {
        return this.key;
    }

    public void bridge$setKey(Object key) {
        this.key = key;
    }
}
