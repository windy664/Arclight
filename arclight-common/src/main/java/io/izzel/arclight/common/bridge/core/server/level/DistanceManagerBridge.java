package io.izzel.arclight.common.bridge.core.server.level;

import net.minecraft.server.level.ChunkHolder;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.Ticket;
import net.minecraft.server.level.TicketType;
import net.minecraft.world.level.ChunkPos;

public interface DistanceManagerBridge {

    default boolean bridge$addTicketAtLevel(TicketType type, ChunkPos pos, int level, Object value) {
        return false;
    }

    default boolean bridge$removeTicketAtLevel(TicketType type, ChunkPos pos, int level, Object value) {
        return false;
    }

    default boolean bridge$addTicket(long chunkPos, Ticket ticket) {
        return false;
    }

    default boolean bridge$removeTicket(long chunkPos, Ticket ticket) {
        return false;
    }

    default void bridge$tick(ChunkMap chunkMap) {

    }

    default void bridge$removeAllTicketsFor(TicketType ticketType, int ticketLevel, Object ticketIdentifier) {

    }

    default void arclight$offerUpdate(ChunkHolder holder) {

    }

    default boolean bridge$platform$isTicketForceTick(Ticket ticket) {
        return false;
    }

    default void bridge$forge$addForcedTicket(long chunkPosIn, Ticket ticketIn) {}

    default void bridge$forge$removeForcedTicket(long chunkPosIn, Ticket ticketIn) {}
}
