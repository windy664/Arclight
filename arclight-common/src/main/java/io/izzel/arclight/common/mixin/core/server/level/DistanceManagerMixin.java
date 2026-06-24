package io.izzel.arclight.common.mixin.core.server.level;

import com.llamalad7.mixinextras.sugar.Local;
import io.izzel.arclight.common.bridge.core.server.level.DistanceManagerBridge;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import net.minecraft.core.SectionPos;
import net.minecraft.server.level.*;
import net.minecraft.world.level.TicketStorage;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.*;

@Mixin(DistanceManager.class)
public abstract class DistanceManagerMixin implements DistanceManagerBridge {

    // @formatter:off
    @Shadow @Final private TicketStorage ticketStorage;
    @Shadow @Final @Mutable private Set<ChunkHolder> chunksToUpdateFutures;
    // @formatter:on

    @Unique
    private Queue<ChunkHolder> arclight$scheduleUpdatingQueue = new LinkedList<>();

    @Override
    public void arclight$offerUpdate(ChunkHolder holder) {
        arclight$scheduleUpdatingQueue.add(holder);
    }

    @Inject(method = "runAllUpdates", at = @At(value = "INVOKE", target = "Ljava/util/Set;isEmpty()Z", shift = At.Shift.AFTER))
    private void arclight$runQueuedUpdates(ChunkMap scheduler, CallbackInfoReturnable<Boolean> cir) {
        final var queue = arclight$scheduleUpdatingQueue;
        for (ChunkHolder now = queue.poll(); now != null; now = queue.poll()) {
            now.callEventIfUnloading(scheduler);
        }
    }

    @Inject(method = "removePlayer", at = @At(value = "INVOKE", target = "Lit/unimi/dsi/fastutil/objects/ObjectSet;remove(Ljava/lang/Object;)Z"), cancellable = true)
    private void arclight$nullsafeRemovePlayer(SectionPos pos, ServerPlayer player, CallbackInfo ci, @Local ObjectSet<ServerPlayer> chunkPlayers) {
        if (chunkPlayers == null) ci.cancel(); return; // CraftBukkit - SPIGOT-6208
    }

    boolean removeTicket(long chunkPosIn, Ticket ticketIn) {
        boolean removed = this.ticketStorage.removeTicket(chunkPosIn, ticketIn);
        if (removed && bridge$platform$isTicketForceTick(ticketIn)) {
            this.bridge$forge$removeForcedTicket(chunkPosIn, ticketIn);
        }
        return removed;
    }

    @Override
    public boolean bridge$removeTicket(long chunkPos, Ticket ticket) {
        return removeTicket(chunkPos, ticket);
    }

    boolean addTicket(long chunkPosIn, Ticket ticketIn) {
        boolean added = this.ticketStorage.addTicket(chunkPosIn, ticketIn);
        if (added && bridge$platform$isTicketForceTick(ticketIn)) {
            this.bridge$forge$addForcedTicket(chunkPosIn, ticketIn);
        }
        return added;
    }

    @Override
    public boolean bridge$addTicket(long chunkPos, Ticket ticket) {
        return addTicket(chunkPos, ticket);
    }

    public void removeAllTicketsFor(TicketType ticketType, int ticketLevel, Object ticketIdentifier) {
        this.ticketStorage.removeTicketIf((ticket, chunkPos) -> ticket.getType() == ticketType && ticket.getTicketLevel() == ticketLevel, null);
    }

    @Override
    public void bridge$removeAllTicketsFor(TicketType ticketType, int ticketLevel, Object ticketIdentifier) {
        removeAllTicketsFor(ticketType, ticketLevel, ticketIdentifier);
    }
}
