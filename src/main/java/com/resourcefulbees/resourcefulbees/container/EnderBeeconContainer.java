package com.resourcefulbees.resourcefulbees.container;

import com.resourcefulbees.resourcefulbees.mixin.ContainerAccessor;
import com.resourcefulbees.resourcefulbees.registry.ModContainers;
import com.resourcefulbees.resourcefulbees.tileentity.EnderBeeconTileEntity;
import com.resourcefulbees.resourcefulbees.tileentity.HoneyGeneratorTileEntity;
import com.resourcefulbees.resourcefulbees.utils.BeeInfoUtils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.IContainerListener;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.BucketItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public class EnderBeeconContainer extends Container {

    public EnderBeeconTileEntity enderBeeconTileEntity;
    public PlayerEntity player;

    public EnderBeeconContainer(int id, World world, BlockPos pos, PlayerInventory inv) {
        super(ModContainers.ENDER_BEECON_CONTAINER.get(), id);

        this.player = inv.player;
        enderBeeconTileEntity = (EnderBeeconTileEntity) world.getTileEntity(pos);

        if (enderBeeconTileEntity != null) {
            this.addSlot(new SlotItemHandlerUnconditioned(enderBeeconTileEntity.h, EnderBeeconTileEntity.HONEY_BOTTLE_INPUT, 184, 34) {
                public boolean isItemValid(ItemStack stack) {
                    if (stack.getItem() instanceof BucketItem) {
                        BucketItem bucket = (BucketItem) stack.getItem();
                        return bucket.getFluid().isIn(enderBeeconTileEntity.honeyFluidTag);
                    } else {
                        return stack.getItem().isIn(enderBeeconTileEntity.honeyBottleTag);
                    }
                }
            });
            this.addSlot(new OutputSlot(enderBeeconTileEntity.h, EnderBeeconTileEntity.BOTTLE_OUTPUT, 184, 72));

            for (int i = 0; i < 3; ++i) {
                for (int j = 0; j < 9; ++j) {
                    this.addSlot(new Slot(inv, j + i * 9 + 9, 36 + j * 18, 118 + i * 18));
                }
            }

            for (int k = 0; k < 9; ++k) {
                this.addSlot(new Slot(inv, k, 36 + k * 18, 176));
            }
        }
    }

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

            if (index <= 1) {
                if (!this.mergeItemStack(itemstack1, 2, inventorySlots.size(), true)) {
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

    public int getFluid() {
        return enderBeeconTileEntity.fluidTank.getFluidAmount();
    }

    public int getMaxFluid() {
        return enderBeeconTileEntity.fluidTank.getCapacity();
    }

    @Override
    public void detectAndSendChanges() {
        super.detectAndSendChanges();
        if (enderBeeconTileEntity == null) {
            return;
        }

        for (IContainerListener listener : ((ContainerAccessor) this).getListeners()) {
            enderBeeconTileEntity.sendGUINetworkPacket(listener);
        }
    }
}
