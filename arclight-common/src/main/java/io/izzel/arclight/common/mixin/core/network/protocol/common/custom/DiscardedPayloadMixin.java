package io.izzel.arclight.common.mixin.core.network.protocol.common.custom;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import io.izzel.arclight.common.bridge.core.network.protocol.common.custom.DiscardedPayloadBridge;
import io.izzel.arclight.common.mod.mixins.annotation.CreateConstructor;
import io.izzel.arclight.common.mod.mixins.annotation.ShadowConstructor;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.DiscardedPayload;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(DiscardedPayload.class)
public class DiscardedPayloadMixin implements DiscardedPayloadBridge {

    private io.netty.buffer.ByteBuf data;

    @ShadowConstructor
    public void arclight$constructor(Identifier id) {
        throw new RuntimeException();
    }

    @CreateConstructor
    public void arclight$constructor(Identifier id, io.netty.buffer.ByteBuf data) {
        arclight$constructor(id);
        this.data = data;
    }

    @ModifyReturnValue(method = "codec", at = @At("RETURN"))
    private static <T extends FriendlyByteBuf>StreamCodec<T, DiscardedPayload> arclight$resetPayload(StreamCodec<T, DiscardedPayload> original, @Local(argsOnly = true) Identifier id, @Local(argsOnly = true) int maxPayloadSize) {
        return CustomPacketPayload.codec((payload, buf) -> {
            buf.writeBytes(payload.data()); // CraftBukkit - serialize
        }, (buf) -> {
            int length = buf.readableBytes();
            if (length >= 0 && length <= maxPayloadSize) {
                // CraftBukkit start
                DiscardedPayload payload = new DiscardedPayload(id);
                payload.bridge$pushData(buf.readBytes(length));
                return payload;
                // CraftBukkit end
            } else {
                throw new IllegalArgumentException("Payload may not be larger than " + maxPayloadSize + " bytes");
            }
        });
    }

    @Override
    public ByteBuf data() {
        return data;
    }

    @Override
    public void bridge$pushData(ByteBuf buf) {
        this.data = buf;
    }
}
