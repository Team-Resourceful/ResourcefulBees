package com.resourcefulbees.resourcefulbees.container;

import com.resourcefulbees.resourcefulbees.lib.CustomStorageContainers;
import com.resourcefulbees.resourcefulbees.registry.RegistryHandler;
import com.resourcefulbees.resourcefulbees.tileentity.multiblocks.centrifuge.CentrifugeControllerTileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.IIntArray;
import net.minecraft.util.IntArray;
import net.minecraft.util.IntReferenceHolder;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;

import javax.annotation.Nonnull;

public class CentrifugeMultiblockContainer extends Container {

    public CentrifugeControllerTileEntity centrifugeTileEntity;
    public PlayerEntity player;
    public final IIntArray times;

    public CentrifugeMultiblockContainer(int id, World world, BlockPos pos, PlayerInventory inv) {
        this(id, world, pos, inv, new IntArray(4));
    }

    public CentrifugeMultiblockContainer(int id, World world, BlockPos pos, PlayerInventory inv, IIntArray times) {
        super(RegistryHandler.CENTRIFUGE_MULTIBLOCK_CONTAINER.get(), id);

        this.player = inv.player;
        this.times = times;

        centrifugeTileEntity = (CentrifugeControllerTileEntity) world.getTileEntity(pos);

        if (centrifugeTileEntity != null) {
            this.addSlot(new SlotItemHandlerUnconditioned(centrifugeTileEntity.h, CentrifugeControllerTileEntity.BOTTLE_SLOT, 8, 8) {
                public boolean isItemValid(ItemStack stack) {
                    return stack.getItem().equals(Items.GLASS_BOTTLE);
                }
            });

            int x = 53;
            for (int i = 0; i < 3; i++) {
                this.addSlot(new SlotItemHandlerUnconditioned(centrifugeTileEntity.h, CentrifugeControllerTileEntity.HONEYCOMB_SLOT[i], x, 8) {
                    public boolean isItemValid(ItemStack stack) {
                        return !stack.getItem().equals(Items.GLASS_BOTTLE);
                    }
                });
                x += 36;
            }


            for (int i = 0; i < 6; i++) {
                this.addSlot(new OutputSlot(centrifugeTileEntity.h, CentrifugeControllerTileEntity.OUTPUT_SLOTS[i], 44 + i * 18, 44));
                this.addSlot(new OutputSlot(centrifugeTileEntity.h, CentrifugeControllerTileEntity.OUTPUT_SLOTS[i + 6], 44 + i * 18, 62));
                this.addSlot(new OutputSlot(centrifugeTileEntity.h, CentrifugeControllerTileEntity.OUTPUT_SLOTS[i + 12], 44 + i * 18, 80));
            }
        }

        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(inv, j + i * 9 + 9, 8 + j * 18, 104 + i * 18));
            }
        }

        for (int k = 0; k < 9; ++k) {
            this.addSlot(new Slot(inv, k, 8 + k * 18, 162));
        }
        trackPower();
        this.trackIntArray(times);
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
                    ((CustomStorageContainers.CustomEnergyStorage)h).setEnergy(energyStored + (value & 0xffff));
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
                    ((CustomStorageContainers.CustomEnergyStorage)h).setEnergy(energyStored | (value << 16));
                });
            }
        });
    }

    public int getEnergy() {
        return centrifugeTileEntity.getCapability(CapabilityEnergy.ENERGY).map(IEnergyStorage::getEnergyStored).orElse(0);
    }

    public int getTime(int i) {
        return this.times.get(i);
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
            if (index < 22) {
                if (!this.mergeItemStack(itemstack1, 22, inventorySlots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            }
            else if (itemstack1.getItem().equals(Items.GLASS_BOTTLE) && !this.mergeItemStack(itemstack1, 0, 1, false)) {
                return ItemStack.EMPTY;
            }
            else if (!this.mergeItemStack(itemstack1, 0, 3, false)) {
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

