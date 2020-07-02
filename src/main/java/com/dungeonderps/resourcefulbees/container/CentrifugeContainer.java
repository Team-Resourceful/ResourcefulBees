package com.dungeonderps.resourcefulbees.container;
import com.dungeonderps.resourcefulbees.registry.RegistryHandler;
import com.dungeonderps.resourcefulbees.tileentity.CentrifugeTileEntity;
import com.dungeonderps.resourcefulbees.utils.FunctionalIntReferenceHolder;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public class CentrifugeContainer extends Container {

    public CentrifugeTileEntity centrifugeTileEntity;
    public PlayerEntity player;

    public CentrifugeContainer(int id, World world, BlockPos pos, PlayerInventory inv) {
        super(RegistryHandler.CENTRIFUGE_CONTAINER.get(), id);

        this.player = inv.player;

        centrifugeTileEntity = (CentrifugeTileEntity) world.getTileEntity(pos);

        this.trackInt(new FunctionalIntReferenceHolder(() -> centrifugeTileEntity.time, v -> centrifugeTileEntity.time = v));
        this.trackInt(new FunctionalIntReferenceHolder(() -> centrifugeTileEntity.totalTime, v -> centrifugeTileEntity.totalTime = v));

        this.addSlot(new SlotItemHandlerUnconditioned(centrifugeTileEntity.h, CentrifugeTileEntity.HONEYCOMB_SLOT, 30, 20){
            public boolean isItemValid(ItemStack stack){
                return !stack.getItem().equals(Items.GLASS_BOTTLE);
            }
        });
        this.addSlot(new SlotItemHandlerUnconditioned(centrifugeTileEntity.h, CentrifugeTileEntity.BOTTLE_SLOT, 30, 38){
            public boolean isItemValid(ItemStack stack){
                return stack.getItem().equals(Items.GLASS_BOTTLE);
            }
        });
        this.addSlot(new OutputSlot(centrifugeTileEntity.h, CentrifugeTileEntity.HONEY_BOTTLE, 80, 59));
        this.addSlot(new OutputSlot(centrifugeTileEntity.h, CentrifugeTileEntity.OUTPUT1, 129, 20));
        this.addSlot(new OutputSlot(centrifugeTileEntity.h, CentrifugeTileEntity.OUTPUT2, 129, 38));

        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(inv, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }

        for (int k = 0; k < 9; ++k) {
            this.addSlot(new Slot(inv, k, 8 + k * 18, 142));
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
