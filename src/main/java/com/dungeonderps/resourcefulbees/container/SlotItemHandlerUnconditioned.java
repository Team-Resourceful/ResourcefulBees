package com.dungeonderps.resourcefulbees.container;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.SlotItemHandler;

import javax.annotation.Nonnull;

public class SlotItemHandlerUnconditioned extends SlotItemHandler {

  private final AutomationSensitiveItemStackHandler inv;

  public SlotItemHandlerUnconditioned(AutomationSensitiveItemStackHandler h, int index, int xPosition, int yPosition) {
    super(h, index, xPosition, yPosition);
    this.inv = h;
  }

  @Override
  public boolean isItemValid(ItemStack stack) {
    if (stack.isEmpty() || !this.inv.canAccept(this.getSlotIndex(), stack, false)) return false;

    ItemStack currentStack = this.inv.getStackInSlot(this.getSlotIndex());
    this.inv.setStackInSlot(this.getSlotIndex(), ItemStack.EMPTY);
    ItemStack remainder = this.inv.insertItem(this.getSlotIndex(), stack, true, false);
    this.inv.setStackInSlot(this.getSlotIndex(), currentStack);
    return remainder.isEmpty() || remainder.getCount() < stack.getCount();
  }

  /**
   * Helper fnct to get the stack in the slot.
   */
  @Override
  @Nonnull
  public ItemStack getStack() {
    return this.inv.getStackInSlot(this.getSlotIndex());
  }

  @Override
  public void putStack(ItemStack stack) {
    this.inv.setStackInSlot(this.getSlotIndex(), stack);
    this.onSlotChanged();
  }

  @Override
  public int getItemStackLimit(ItemStack stack) {
    ItemStack maxAdd = stack.copy();
    maxAdd.setCount(stack.getMaxStackSize());
    ItemStack currentStack = this.inv.getStackInSlot(this.getSlotIndex());
    this.inv.setStackInSlot(this.getSlotIndex(), ItemStack.EMPTY);
    ItemStack remainder = this.inv.insertItem(this.getSlotIndex(), maxAdd, true, false);
    this.inv.setStackInSlot(this.getSlotIndex(), currentStack);
    return stack.getMaxStackSize() - remainder.getCount();
  }

  @Override
  public boolean canTakeStack(PlayerEntity playerIn) {
    return !this.inv.extractItem(this.getSlotIndex(), 1, true, false).isEmpty();
  }

  @Override
  public ItemStack decrStackSize(int amount) {
    return this.inv.extractItem(this.getSlotIndex(), amount, false, false);
  }
}
