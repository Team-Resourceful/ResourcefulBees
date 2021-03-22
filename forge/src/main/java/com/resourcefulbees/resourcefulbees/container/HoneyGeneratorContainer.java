package com.resourcefulbees.resourcefulbees.container;

import com.resourcefulbees.resourcefulbees.mixin.ContainerAccessor;
import com.resourcefulbees.resourcefulbees.registry.ModContainers;
import com.resourcefulbees.resourcefulbees.tileentity.HoneyGeneratorTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerListener;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;

import javax.annotation.Nonnull;

public class HoneyGeneratorContainer extends AbstractContainerMenu {

    private final HoneyGeneratorTileEntity honeyGeneratorTileEntity;
    private final Player player;

    public HoneyGeneratorContainer(int id, Level world, BlockPos pos, Inventory inv) {
        super(ModContainers.HONEY_GENERATOR_CONTAINER.get(), id);

        this.player = inv.player;
        honeyGeneratorTileEntity = (HoneyGeneratorTileEntity) world.getBlockEntity(pos);

        if (getHoneyGeneratorTileEntity() != null) {
            this.addSlot(new SlotItemHandlerUnconditioned(getHoneyGeneratorTileEntity().getTileStackHandler(), HoneyGeneratorTileEntity.HONEY_BOTTLE_INPUT, 36, 20) {

                @Override
                public boolean mayPlace(ItemStack stack) {
                    return getHoneyGeneratorTileEntity().getTileStackHandler().isItemValid(HoneyGeneratorTileEntity.HONEY_BOTTLE_INPUT, stack);
                }
            });
            this.addSlot(new OutputSlot(getHoneyGeneratorTileEntity().getTileStackHandler(), HoneyGeneratorTileEntity.BOTTLE_OUTPUT, 36, 58));

            for (int i = 0; i < 3; ++i) {
                for (int j = 0; j < 9; ++j) {
                    this.addSlot(new Slot(inv, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
                }
            }

            for (int k = 0; k < 9; ++k) {
                this.addSlot(new Slot(inv, k, 8 + k * 18, 142));
            }
        }
    }

    public int getEnergy() { return getHoneyGeneratorTileEntity().getCapability(CapabilityEnergy.ENERGY).map(IEnergyStorage::getEnergyStored).orElse(0); }
    
    public int getFluid() { return getHoneyGeneratorTileEntity().fluidTank.getFluidAmount(); }
    
    public int getMaxEnergy() { return getHoneyGeneratorTileEntity().energyStorage.getMaxEnergyStored(); }

    public int getMaxFluid() { return getHoneyGeneratorTileEntity().fluidTank.getCapacity(); }

    public int getTime() { return getHoneyGeneratorTileEntity().getFluidFilled(); }

    public int getEnergyTime() { return getHoneyGeneratorTileEntity().getEnergyFilled(); }

    @Override
    public boolean stillValid(@Nonnull Player player) {
        return true;
    }

    @Nonnull
    @Override
    public ItemStack quickMoveStack(@Nonnull Player playerIn, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot != null && slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();

            if (index <= 1) {
                if (!this.moveItemStackTo(itemstack1, 2, slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(itemstack1, 0, 1, false)) {
                return ItemStack.EMPTY;
            }

            if (itemstack1.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }
        return itemstack;
    }

    @Override
    public void broadcastChanges() {
        super.broadcastChanges();
        if (getHoneyGeneratorTileEntity() == null) {
            return;
        }

        for (ContainerListener listener : ((ContainerAccessor) this).getListeners()) {
            getHoneyGeneratorTileEntity().sendGUINetworkPacket(listener);
        }
    }

    public HoneyGeneratorTileEntity getHoneyGeneratorTileEntity() {
        return honeyGeneratorTileEntity;
    }

    public Player getPlayer() {
        return player;
    }
}
