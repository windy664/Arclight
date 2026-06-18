package io.izzel.arclight.common.mixin.core.world;

import io.izzel.arclight.common.mod.mixins.annotation.CreateConstructor;
import io.izzel.arclight.common.mod.mixins.annotation.ShadowConstructor;
import net.minecraft.core.NonNullList;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.inventory.StackedContentsCompatible;
import net.minecraft.world.item.ItemStack;
import org.bukkit.Location;
import org.bukkit.craftbukkit.entity.CraftHumanEntity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.InventoryHolder;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;

@Mixin(SimpleContainer.class)
public abstract class SimpleContainerMixin implements Container, StackedContentsCompatible {

    @Shadow
    @Final
    public NonNullList<ItemStack> items;
    // CraftBukkit start - add fields and methods
    public List<HumanEntity> transaction = new java.util.ArrayList<HumanEntity>();
    private int maxStack = MAX_STACK;
    protected org.bukkit.inventory.InventoryHolder bukkitOwner;

    @ShadowConstructor
    public void arclight$constructor(int numSlots) {
        throw new RuntimeException();
    }

    @CreateConstructor
    public void arclight$constructor(int numSlots, InventoryHolder owner) {
        this.arclight$constructor(numSlots);
        this.bukkitOwner = owner;
    }

    @CreateConstructor
    public void arclight$constructor(SimpleContainer original) {
        this.arclight$constructor(original.getContainerSize());
        for (int slot = 0; slot < original.getContainerSize(); slot++) {
            this.items.set(slot, original.items.get(slot).copy());
        }
    }

    @Override
    public List<ItemStack> getContents() {
        return this.items;
    }

    @Override
    public void onOpen(CraftHumanEntity who) {
        transaction.add(who);
    }

    @Override
    public void onClose(CraftHumanEntity who) {
        transaction.remove(who);
    }

    @Override
    public List<HumanEntity> getViewers() {
        return transaction;
    }

    @Override
    public int getMaxStackSize() {
        return maxStack;
    }

    @Override
    public void setMaxStackSize(int i) {
        maxStack = i;
    }

    @Override
    public org.bukkit.inventory.InventoryHolder getOwner() {
        return bukkitOwner;
    }

    @Override
    public Location getLocation() {
        return null;
    }
}
