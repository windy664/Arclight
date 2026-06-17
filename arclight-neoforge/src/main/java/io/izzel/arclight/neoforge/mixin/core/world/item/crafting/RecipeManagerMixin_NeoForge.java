package io.izzel.arclight.neoforge.mixin.core.world.item.crafting;

import com.google.gson.Gson;
import com.mojang.serialization.Codec;
import io.izzel.arclight.common.bridge.core.world.item.crafting.RecipeManagerBridge;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.world.item.crafting.RecipeManager;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(RecipeManager.class)
public abstract class RecipeManagerMixin_NeoForge extends SimpleJsonResourceReloadListener implements RecipeManagerBridge {


    protected RecipeManagerMixin_NeoForge(HolderLookup.Provider registries, Codec codec, ResourceKey registryKey) {
        super(registries, codec, registryKey);
    }
}
