package com.teamresourceful.resourcefulbees.common.blockentities.base;

import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public interface BasicContainer extends Container {

    NonNullList<ItemStack> getItems();

    @Override
    default boolean isEmpty() {
        return getItems().stream().allMatch(ItemStack::isEmpty);
    }

    @Override
    default @NotNull ItemStack removeItem(int slot, int amount) {
        ItemStack stack = ContainerHelper.removeItem(getItems(), slot, amount);
        setChanged();
        setSlotChanged(slot, getItem(slot));
        return stack;
    }

    @Override
    default void setItem(int slot, @NotNull ItemStack stack) {
        getItems().set(slot, stack);
        setChanged();
        setSlotChanged(slot, stack);
    }

    @Override
    default @NotNull ItemStack removeItemNoUpdate(int slot) {
        return ContainerHelper.takeItem(getItems(), slot);
    }

    @Override
    default @NotNull ItemStack getItem(int slot) {
        return getItems().get(slot);
    }

    @Override
    default int getContainerSize() {
        return getItems().size();
    }

    @Override
    default void clearContent() {
        getItems().clear();
        setChanged();
    }
    
    default boolean canTakeItem(int slot, ItemStack stack) {
        return true;
    }

    default void setSlotChanged(int slot, ItemStack stack) {

    }

    default CompoundTag serializeContainer() {
        return ContainerHelper.saveAllItems(new CompoundTag(), getItems(), true);
    }

    default void deserializeContainer(CompoundTag tag) {
        ContainerHelper.loadAllItems(tag, getItems());
    }
}
