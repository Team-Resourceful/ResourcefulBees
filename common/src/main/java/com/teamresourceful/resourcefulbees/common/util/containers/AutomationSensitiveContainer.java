package com.teamresourceful.resourcefulbees.common.util.containers;

import earth.terrarium.botarium.common.item.SerializableContainer;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;

public abstract class AutomationSensitiveContainer implements SerializableContainer {

    protected final NonNullList<ItemStack> items;
    protected final BlockEntity updatable;
    protected final Predicate<Player> canPlayerAccess;

    public AutomationSensitiveContainer(BlockEntity entity, int size, Predicate<Player> canPlayerAccess) {
        this.items = NonNullList.withSize(size, ItemStack.EMPTY);
        this.updatable = entity;
        this.canPlayerAccess = canPlayerAccess;
    }

    @Override
    public int getContainerSize() {
        return items.size();
    }

    @Override
    public boolean isEmpty() {
        return this.items.stream().allMatch(ItemStack::isEmpty);
    }

    @Override
    public @NotNull ItemStack getItem(int slot) {
        return items.get(slot);
    }

    @Override
    public @NotNull ItemStack removeItem(int slot, int amount) {
        return removeItem(slot, amount, true);
    }

    public ItemStack removeItem(int slot, int amount, boolean byAutomation) {
        if (canRemove(slot, byAutomation)) {
            return removeItemInternal(slot, amount);
        }
        return ItemStack.EMPTY;
    }

    public ItemStack removeItemInternal(int slot, int amount) {
        ItemStack stack = ContainerHelper.removeItem(items, slot, amount);
        setChanged(slot);
        return stack;
    }

    @Override
    public @NotNull ItemStack removeItemNoUpdate(int slot) {
        return ContainerHelper.takeItem(items, slot);
    }

    @Override
    public void setItem(int slot, @NotNull ItemStack stack) {
        setItem(slot, stack, true);
    }

    public void setItem(int slot, @NotNull ItemStack stack, boolean byAutomation) {
        if (canAccept(slot, stack, byAutomation)) {
            setItemInternal(slot, stack);
        }
    }

    public void setItemInternal(int slot, @NotNull ItemStack stack) {
        items.set(slot, stack);
        setChanged(slot);
    }

    @Override
    public void setChanged() {
        updatable.setChanged();
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return canPlayerAccess.test(player);
    }

    @Override
    public void clearContent() {
        this.items.clear();
    }

    public int getMaxStackSize(int slot) {
        return getMaxStackSize();
    }

    public void setChanged(int slot) {
        setChanged();
    }

    public NonNullList<ItemStack> items() {
        return items;
    }

    //region NBT
    @Override
    public void deserialize(CompoundTag nbt) {
        ContainerHelper.loadAllItems(nbt, items);
    }

    @Override
    public CompoundTag serialize(CompoundTag nbt) {
        return ContainerHelper.saveAllItems(nbt, items);
    }
    //endregion

    //region Automation Checks
    @Override
    public boolean canPlaceItem(int index, @NotNull ItemStack stack) {
        return canAccept(index, stack, true);
    }

    @Override
    public boolean canTakeItem(@NotNull Container container, int index, @NotNull ItemStack stack) {
        return canRemove(index, true);
    }

    public abstract boolean canAccept(int slot, ItemStack stack, boolean automation);

    public abstract boolean canRemove(int slot, boolean automation);
    //endregion
}
