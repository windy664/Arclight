package io.izzel.arclight.common.mixin.core.network.chat;

import io.izzel.arclight.common.bridge.core.network.chat.ComponentBridge;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

@Mixin(Component.class)
public interface ComponentMixin extends ComponentBridge {

    // @formatter:off
    @Shadow List<Component> getSiblings();
    // @formatter:on

    @Override
    default Stream<Component> stream() {
        return com.google.common.collect.Streams.concat(new Stream[]{Stream.of(this), this.getSiblings().stream().flatMap(Component::stream)});
    }

    @Override
    default @NotNull Iterator<Component> iterator() {
        return this.stream().iterator();
    }
}
