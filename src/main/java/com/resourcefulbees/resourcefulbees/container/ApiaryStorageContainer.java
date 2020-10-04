package com.resourcefulbees.resourcefulbees.container;

import com.resourcefulbees.resourcefulbees.item.UpgradeItem;
import com.resourcefulbees.resourcefulbees.lib.NBTConstants;
import com.resourcefulbees.resourcefulbees.registry.RegistryHandler;
import com.resourcefulbees.resourcefulbees.tileentity.multiblocks.apiary.ApiaryStorageTileEntity;
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
    public PlayerInventory playerInventory;
    public int numberOfSlots;
    public boolean rebuild;

    public ApiaryStorageContainer(int id, World world, BlockPos pos, PlayerInventory inv) {
        super(RegistryHandler.APIARY_STORAGE_CONTAINER.get(), id);
        this.player = inv.player;
        this.playerInventory = inv;
        apiaryStorageTileEntity = (ApiaryStorageTileEntity) world.getTileEntity(pos);
        setupSlots(false);
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


    public void setupSlots(boolean rebuild) {
        if (apiaryStorageTileEntity != null) {
            this.inventorySlots.clear();
            numberOfSlots = apiaryStorageTileEntity.numberOfSlots;
            this.addSlot(new SlotItemHandlerUnconditioned(apiaryStorageTileEntity.h, ApiaryStorageTileEntity.UPGRADE_SLOT, 7, 18) {
                public int getSlotStackLimit() { return 1; }

                public boolean isItemValid(ItemStack stack) { return UpgradeItem.hasUpgradeData(stack) && (UpgradeItem.getUpgradeType(stack).contains(NBTConstants.NBT_STORAGE_UPGRADE)); }

                public boolean canTakeStack(PlayerEntity playerIn) {
                    boolean flag = true;

                    for (int i = 10; i <= numberOfSlots; ++i) {
                        if (!apiaryStorageTileEntity.h.getStackInSlot(i).isEmpty()) {
                            flag = false;
                            break;
                        }
                    }
                    return flag;
                }
            });

            int rows;
            if (numberOfSlots != 108) {
                rows = numberOfSlots / 9;
                for (int r = 0; r < rows; ++r) {
                    for (int c = 0; c < 9; ++c) {
                        this.addSlot(new OutputSlot(apiaryStorageTileEntity.h, c + r * 9 + 1, 26 + 8 + c * 18, 18 + r * 18));
                    }
                }
            } else {
                rows = 9;
                for (int r = 0; r < 9; ++r) {
                    for (int c = 0; c < 12; ++c) {
                        this.addSlot(new OutputSlot(apiaryStorageTileEntity.h, c + r * 12 + 1, 26 + 8 + c * 18, 18 + r * 18));
                    }
                }
            }

            int invX = numberOfSlots == 108 ? 35 : 8;

            for (int i = 0; i < 3; ++i) {
                for (int j = 0; j < 9; ++j) {
                    this.addSlot(new Slot(playerInventory, j + i * 9 + 9, 26 + invX + j * 18, 32 + (rows * 18) + i * 18));
                }
            }

            for (int k = 0; k < 9; ++k) {
                this.addSlot(new Slot(playerInventory, k, 26 + invX + k * 18, 90 + rows * 18));
            }

            this.rebuild = rebuild;
        }
    }




    @Nonnull
    @Override
    public ItemStack transferStackInSlot(@Nonnull PlayerEntity playerIn, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.inventorySlots.get(index);
        if (slot != null && slot.getHasStack()) {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();
            if (index <= numberOfSlots) {
                if (!this.mergeItemStack(itemstack1, numberOfSlots, inventorySlots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.mergeItemStack(itemstack1, 0, 1, false)) {
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
