package com.resourcefulbees.resourcefulbees.container;

import com.resourcefulbees.resourcefulbees.mixin.ContainerAccessor;
import com.resourcefulbees.resourcefulbees.registry.ModContainers;
import com.resourcefulbees.resourcefulbees.tileentity.EnderBeeconTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerListener;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import javax.annotation.Nonnull;

public class EnderBeeconContainer extends AbstractContainerMenu {

    private final EnderBeeconTileEntity enderBeeconTileEntity;
    private final Player player;

    public EnderBeeconContainer(int id, Level world, BlockPos pos, Inventory inv) {
        super(ModContainers.ENDER_BEECON_CONTAINER.get(), id);

        this.player = inv.player;
        enderBeeconTileEntity = (EnderBeeconTileEntity) world.getBlockEntity(pos);

        if (getEnderBeeconTileEntity() != null) {
            this.addSlot(new SlotItemHandlerUnconditioned(getEnderBeeconTileEntity().getTileStackHandler(), EnderBeeconTileEntity.HONEY_BOTTLE_INPUT, 184, 34) {

                @Override
                public boolean mayPlace(ItemStack stack) {
                    return EnderBeeconTileEntity.isItemValid(stack);
                }
            });
            this.addSlot(new OutputSlot(getEnderBeeconTileEntity().getTileStackHandler(), EnderBeeconTileEntity.BOTTLE_OUTPUT, 184, 72));

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

    public int getFluid() {
        return getEnderBeeconTileEntity().getFluidTank().getFluidAmount();
    }

    public int getMaxFluid() {
        return getEnderBeeconTileEntity().getFluidTank().getCapacity();
    } // TODO Unused?

    @Override
    public void broadcastChanges() {
        super.broadcastChanges();
        if (getEnderBeeconTileEntity() == null) {
            return;
        }

        for (ContainerListener listener : ((ContainerAccessor) this).getListeners()) {
            getEnderBeeconTileEntity().sendGUINetworkPacket(listener);
        }
    }

    public EnderBeeconTileEntity getEnderBeeconTileEntity() {
        return enderBeeconTileEntity;
    }

    public Player getPlayer() {
        return player;
    }
}
