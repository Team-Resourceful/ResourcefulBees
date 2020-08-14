package com.dungeonderps.resourcefulbees.container;

import com.dungeonderps.resourcefulbees.registry.RegistryHandler;
import com.dungeonderps.resourcefulbees.tileentity.centrifuge.CentrifugeControllerTileEntity;
import com.dungeonderps.resourcefulbees.utils.CustomEnergyStorage;
import com.dungeonderps.resourcefulbees.utils.FunctionalIntReferenceHolder;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.IntReferenceHolder;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;

import javax.annotation.Nonnull;

public class CentrifugeMultiblockContainer extends Container {

    public CentrifugeControllerTileEntity centrifugeTileEntity;
    public PlayerEntity player;

    public CentrifugeMultiblockContainer(int id, World world, BlockPos pos, PlayerInventory inv) {
        super(RegistryHandler.CENTRIFUGE_MULTIBLOCK_CONTAINER.get(), id);

        this.player = inv.player;

        centrifugeTileEntity = (CentrifugeControllerTileEntity) world.getTileEntity(pos);

        int x = 53;
        for (int i=0; i < 3; i++) {
            int finalI = i;
            this.trackInt(new FunctionalIntReferenceHolder(() -> centrifugeTileEntity.time[finalI], v -> centrifugeTileEntity.time[finalI] = v));
            this.trackInt(new FunctionalIntReferenceHolder(() -> centrifugeTileEntity.totalTime[finalI], v -> centrifugeTileEntity.totalTime[finalI] = v));

            this.addSlot(new SlotItemHandlerUnconditioned(centrifugeTileEntity.h, CentrifugeControllerTileEntity.HONEYCOMB_SLOT[i], x, 8){
                public boolean isItemValid(ItemStack stack){
                    return !stack.getItem().equals(Items.GLASS_BOTTLE);
                }
            });
            x += 36;
        }


        for (int i = 0; i < 9; i++) {
            this.addSlot(new OutputSlot(centrifugeTileEntity.h, CentrifugeControllerTileEntity.OUTPUT_SLOTS[i], 53 + i * 18, 80));
        }
        for (int i = 0; i < 9; i++) {
            this.addSlot(new OutputSlot(centrifugeTileEntity.h, CentrifugeControllerTileEntity.OUTPUT_SLOTS[i + 9], 53 + i * 18, 98));
        }


        this.addSlot(new SlotItemHandlerUnconditioned(centrifugeTileEntity.h, CentrifugeControllerTileEntity.BOTTLE_SLOT, 8, 8){
            public boolean isItemValid(ItemStack stack){
                return stack.getItem().equals(Items.GLASS_BOTTLE);
            }
        });

        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(inv, j + i * 9 + 9, 8 + j * 18, 104 + i * 18));
            }
        }

        for (int k = 0; k < 9; ++k) {
            this.addSlot(new Slot(inv, k, 8 + k * 18, 162));
        }
        trackPower();
    }

    private void trackPower() {
        trackInt(new IntReferenceHolder() {
            @Override
            public int get() {
                return getEnergy() & 0xffff;
            }

            @Override
            public void set(int value) {
                centrifugeTileEntity.getCapability(CapabilityEnergy.ENERGY).ifPresent(h -> {
                    int energyStored = h.getEnergyStored() & 0xffff0000;
                    ((CustomEnergyStorage)h).setEnergy(energyStored + (value & 0xffff));
                });
            }
        });
        trackInt(new IntReferenceHolder() {
            @Override
            public int get() {
                return (getEnergy() >> 16) & 0xffff;
            }

            @Override
            public void set(int value) {
                centrifugeTileEntity.getCapability(CapabilityEnergy.ENERGY).ifPresent(h -> {
                    int energyStored = h.getEnergyStored() & 0x0000ffff;
                    ((CustomEnergyStorage)h).setEnergy(energyStored | (value << 16));
                });
            }
        });
    }

    public int getEnergy() {
        return centrifugeTileEntity.getCapability(CapabilityEnergy.ENERGY).map(IEnergyStorage::getEnergyStored).orElse(0);
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
            if (index <= 22) {
                if (!this.mergeItemStack(itemstack1, 22, inventorySlots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.mergeItemStack(itemstack1, 0, 21, false)) {
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

