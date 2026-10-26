package com.teamresourceful.resourcefulbees.common.blockentities.base;

import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import net.neoforged.neoforge.transfer.TransferPreconditions;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;
import org.jspecify.annotations.NonNull;

public class IOResourceHandler extends ItemStacksResourceHandler {

    private final int outputIndex;

    public IOResourceHandler(int size, int outputIndex) {
        super(size);
        this.outputIndex = outputIndex;
    }

    public ItemContainerContents toContainerContents() {
        return ItemContainerContents.fromItems(this.copyToList());
    }

    public void fromContainerContents(ItemContainerContents contents) {
        NonNullList<ItemStack> items = NonNullList.withSize(this.size(), ItemStack.EMPTY);
        contents.copyInto(items);

        for (int slot = 0; slot < this.size(); slot++) {
            ItemStack stack = items.get(slot);

            if (stack.isEmpty()) {
                this.set(slot, ItemResource.EMPTY, 0);
            } else {
                this.set(slot, ItemResource.of(stack), stack.getCount());
            }
        }
    }

    @Override
    public int insert(int index, @NonNull ItemResource resource, int amount, @NonNull TransactionContext transaction) {
        return index <= outputIndex ? super.insert(index, resource, amount, transaction) : 0;
    }

    public void insertOutput(ItemResource resource, int amount, TransactionContext transactionContext) {
        TransferPreconditions.checkNonEmptyNonNegative(resource, amount);

        int inserted = 0;
        int size = size();
        for (int index = outputIndex; index < size; index++) {
            inserted += super.insert(index, resource, amount - inserted, transactionContext);
            if (inserted == amount) break;
        }
    }
}
