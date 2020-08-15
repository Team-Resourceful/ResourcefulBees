package com.dungeonderps.resourcefulbees.container;

import com.dungeonderps.resourcefulbees.registry.RegistryHandler;
import com.dungeonderps.resourcefulbees.tileentity.HoneyGeneratorTileEntity;
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

public class HoneyGeneratorContainer extends Container {

    public HoneyGeneratorTileEntity honeyGeneratorTileEntity;
    public PlayerEntity player;

    public HoneyGeneratorContainer(int id, World world, BlockPos pos, PlayerInventory inv) {
        super(RegistryHandler.HONEY_GENERATOR_CONTAINER.get(), id);

        this.player = inv.player;

        honeyGeneratorTileEntity = (HoneyGeneratorTileEntity) world.getTileEntity(pos);

        this.trackInt(new FunctionalIntReferenceHolder(() -> honeyGeneratorTileEntity.time, v -> honeyGeneratorTileEntity.time = v));
        this.trackInt(new FunctionalIntReferenceHolder(() -> honeyGeneratorTileEntity.totalTime, v -> honeyGeneratorTileEntity.totalTime = v));
        this.trackInt(new FunctionalIntReferenceHolder(() -> honeyGeneratorTileEntity.energyTime, v -> honeyGeneratorTileEntity.energyTime = v));
        this.trackInt(new FunctionalIntReferenceHolder(() -> honeyGeneratorTileEntity.energyTotalTime, v -> honeyGeneratorTileEntity.energyTotalTime = v));

        this.addSlot(new SlotItemHandlerUnconditioned(honeyGeneratorTileEntity.h, HoneyGeneratorTileEntity.HONEY_BOTTLE_INPUT, 36, 20){
            public boolean isItemValid(ItemStack stack){
                return stack.getItem().equals(Items.HONEY_BOTTLE);
            }
        });
        this.addSlot(new OutputSlot(honeyGeneratorTileEntity.h, HoneyGeneratorTileEntity.BOTTLE_OUPUT, 36, 58));

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
}
