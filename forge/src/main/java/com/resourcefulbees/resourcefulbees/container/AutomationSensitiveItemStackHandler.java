/*
 * This file ("ItemStackHandlerAA.java") is part of the Actually Additions mod for Minecraft.
 * It is created and owned by Ellpeck and distributed
 * under the Actually Additions License to be found at
 * http://ellpeck.de/actaddlicense
 * View the source code at https://github.com/Ellpeck/ActuallyAdditions
 *
 * © 2015-2017 Ellpeck
 *
 * ###########-NOTE-###############
 *  This files name has been changed.
 *  and additional code has been implemented.
 */
package com.resourcefulbees.resourcefulbees.container;

import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

/**
 * @author Shadows
 * Taken from Actually Additions owned by Ellpeck
 * Edited to to update to offical mappings
 */
@SuppressWarnings("unused")
public class AutomationSensitiveItemStackHandler extends ItemStackHandler {

    public static final IAcceptor ACCEPT_TRUE = (a, b, c) -> true;
    public static final IRemover REMOVE_TRUE = (a, b) -> true;
    public static final IAcceptor ACCEPT_FALSE = (a, b, c) -> false;
    public static final IRemover REMOVE_FALSE = (a, b) -> false;

    final IAcceptor acceptor;
    final IRemover remover;

    public AutomationSensitiveItemStackHandler(NonNullList<ItemStack> stacks, IAcceptor acceptor, IRemover remover) {
        super(stacks);
        this.acceptor = acceptor;
        this.remover = remover;
    }

    public AutomationSensitiveItemStackHandler(int slots, IAcceptor acceptor, IRemover remover) {
        super(slots);
        this.acceptor = acceptor;
        this.remover = remover;
    }

    public AutomationSensitiveItemStackHandler(NonNullList<ItemStack> stacks) {
        this(stacks, ACCEPT_TRUE, REMOVE_TRUE);
    }

    public AutomationSensitiveItemStackHandler(int slots) {
        this(slots, ACCEPT_TRUE, REMOVE_TRUE);
    }

    public NonNullList<ItemStack> getItems() {
        return this.stacks;
    }

    @NotNull
    @Override
    public ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
        return this.insertItem(slot, stack, simulate, true);
    }

    public ItemStack insertItem(int slot, ItemStack stack, boolean simulate, boolean fromAutomation) {
        if (this.canAccept(slot, stack, fromAutomation)) return super.insertItem(slot, stack, simulate);
        return stack;
    }

    public void deserializeNBTWithoutCheckingSize(CompoundTag nbt)
    {
        ListTag tagList = nbt.getList("Items", Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < tagList.size(); i++)
        {
            CompoundTag itemTags = tagList.getCompound(i);
            int slot = itemTags.getInt("Slot");

            if (slot >= 0 && slot < stacks.size())
            {
                stacks.set(slot, ItemStack.of(itemTags));
            }
        }
        onLoad();
    }

    @NotNull
    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        return this.extractItem(slot, amount, simulate, true);
    }

    public ItemStack extractItem(int slot, int amount, boolean simulate, boolean byAutomation) {
        if (!this.canRemove(slot, byAutomation)) return ItemStack.EMPTY;
        return super.extractItem(slot, amount, simulate);
    }

    public final boolean canAccept(int slot, ItemStack stack, boolean automation) {
        return this.getAcceptor().canAccept(slot, stack, automation);
    }

    public final boolean canRemove(int slot, boolean automation) {
        return this.getRemover().canRemove(slot, automation);
    }

    public IAcceptor getAcceptor() {
        return this.acceptor;
    }

    public IRemover getRemover() {
        return this.remover;
    }

    public interface IAcceptor {
        boolean canAccept(int slot, ItemStack stack, boolean automation);
    }

    public interface IRemover {
        boolean canRemove(int slot, boolean automation);
    }
}
