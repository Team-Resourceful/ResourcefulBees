package com.dungeonderps.resourcefulbees.container;

import com.dungeonderps.resourcefulbees.item.UpgradeItem;
import com.dungeonderps.resourcefulbees.registry.RegistryHandler;
import com.dungeonderps.resourcefulbees.tileentity.ApiaryStorageTileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public class ApiaryStorageContainer extends Container {

    public ApiaryStorageTileEntity apiaryStorageTileEntity;
    public PlayerEntity player;
    public int numberOfSlots;

    public ApiaryStorageContainer(int id, World world, BlockPos pos, PlayerInventory inv) {
        super(RegistryHandler.APIARY_STORAGE_CONTAINER.get(), id);

        this.player = inv.player;

        apiaryStorageTileEntity = (ApiaryStorageTileEntity) world.getTileEntity(pos);

        if (apiaryStorageTileEntity != null) {
            numberOfSlots = apiaryStorageTileEntity.numberOfSlots;
            this.addSlot(new SlotItemHandlerUnconditioned(apiaryStorageTileEntity.h, ApiaryStorageTileEntity.UPGRADE_SLOT, -20, 18) {
                public int getSlotStackLimit()
                {
                    return 1;
                }

                public boolean isItemValid(ItemStack stack) { return stack.getItem() instanceof UpgradeItem; }

                public boolean canTakeStack(PlayerEntity playerIn) {





                    return !this.getInv().extractItem(this.getSlotIndex(), 1, true, false).isEmpty();
                }
            });

            int rows;
            if (numberOfSlots != 108) {
                rows = numberOfSlots / 9;
                for (int r = 0; r < rows; ++r) {
                    for (int c = 0; c < 9; ++c) {
                        this.addSlot(new OutputSlot(apiaryStorageTileEntity.h, c + r * 9 + 1, 8 + c * 18, 18 + r * 18));
                    }
                }
            } else {
                rows = 9;
                for (int r = 0; r < 9; ++r) {
                    for (int c = 0; c < 12; ++c){
                        this.addSlot(new OutputSlot(apiaryStorageTileEntity.h, c + r * 12 + 1, 8 + c * 18, 18 + r * 18));
                    }
                }
            }

            int invX = numberOfSlots == 108 ? 35 : 8;

            for (int i = 0; i < 3; ++i) {
                for (int j = 0; j < 9; ++j) {
                    this.addSlot(new Slot(inv, j + i * 9 + 9, invX + j * 18, 32 + (rows * 18) + i * 18));
                }
            }

            for (int k = 0; k < 9; ++k) {
                this.addSlot(new Slot(inv, k, invX + k * 18, 90 + rows * 18));
            }
        }
    }

    /**
     * Determines whether supplied player can use this container
     *
     * @param player the player
     */
    @Override
    public boolean canInteractWith(@Nonnull PlayerEntity player) {
        return true;
    }

    @Nonnull
    @Override
    public ItemStack transferStackInSlot(@Nonnull PlayerEntity playerIn, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.inventorySlots.get(index);
        if (slot != null && slot.getHasStack()) {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();
            if (index <= 4) {
                if (!this.mergeItemStack(itemstack1, 5, inventorySlots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.mergeItemStack(itemstack1, 0, 2, false)) {
                return ItemStack.EMPTY;
            }

            if (itemstack1.isEmpty()) {
                slot.putStack(ItemStack.EMPTY);
            } else {
                slot.onSlotChanged();
            }
        }
        return itemstack;
    }
}
