package com.resourcefulbees.resourcefulbees.container;

import com.resourcefulbees.resourcefulbees.mixin.ContainerAccessor;
import com.resourcefulbees.resourcefulbees.registry.ModContainers;
import com.resourcefulbees.resourcefulbees.tileentity.AbstractHoneyTankContainer;
import com.resourcefulbees.resourcefulbees.tileentity.EnderBeeconTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerListener;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class EnderBeeconContainer extends ContainerWithStackMove {

    private final EnderBeeconTileEntity enderBeeconTileEntity;
    private final Player player;

    public EnderBeeconContainer(int id, Level world, BlockPos pos, Inventory inv) {
        super(ModContainers.ENDER_BEECON_CONTAINER.get(), id);

        this.player = inv.player;
        enderBeeconTileEntity = (EnderBeeconTileEntity) world.getBlockEntity(pos);

        if (getEnderBeeconTileEntity() != null) {
            this.addSlot(new SlotItemHandlerUnconditioned(getEnderBeeconTileEntity().getTileStackHandler(), AbstractHoneyTankContainer.BOTTLE_INPUT_EMPTY, 184, 34) {

                @Override
                public boolean mayPlace(ItemStack stack) {
                    return AbstractHoneyTankContainer.isItemValid(stack);
                }
            });
            this.addSlot(new OutputSlot(getEnderBeeconTileEntity().getTileStackHandler(), AbstractHoneyTankContainer.BOTTLE_OUTPUT_EMPTY, 184, 72));

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
    public boolean stillValid(@NotNull Player player) {
        return true;
    }

    @Override
    public int getInventoryStart() {
        return 2;
    }

    @Override
    public int getContainerInputEnd() {
        return 1;
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
