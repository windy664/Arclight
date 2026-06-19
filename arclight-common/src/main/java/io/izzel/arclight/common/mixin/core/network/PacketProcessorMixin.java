package io.izzel.arclight.common.mixin.core.network;

import net.minecraft.network.PacketListener;
import net.minecraft.server.network.ServerCommonPacketListenerImpl;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "net.minecraft.network.PacketProcessor.ListenerAndPacket")
public class PacketProcessorMixin<T extends PacketListener> {

    @Shadow
    @Final
    private T listener;

    @Inject(method = "handle", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/PacketListener;shouldHandleMessage(Lnet/minecraft/network/protocol/Packet;)Z"), cancellable = true)
    private void arclight$skipHandlePackets(CallbackInfo ci) {
        if (this.listener instanceof ServerCommonPacketListenerImpl serverCommonPacketListener && serverCommonPacketListener.bridge$processedDisconnect()) ci.cancel(); return; // CraftBukkit - Don't handle sync packets for kicked players
    }
}
