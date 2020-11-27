package com.resourcefulbees.resourcefulbees.container;
import com.resourcefulbees.resourcefulbees.registry.RegistryHandler;
import com.resourcefulbees.resourcefulbees.tileentity.CentrifugeTileEntity;
import com.resourcefulbees.resourcefulbees.utils.CustomEnergyStorage;
import com.resourcefulbees.resourcefulbees.utils.FunctionalIntReferenceHolder;
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
        trackPower();
    }

    private void trackPower() {
        // Unfortunatelly on a dedicated server ints are actually truncated to short so we need
        // to split our integer here (split our 32 bit integer into two 16 bit integers)
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
