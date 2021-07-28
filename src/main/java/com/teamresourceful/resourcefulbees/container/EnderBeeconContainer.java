package com.teamresourceful.resourcefulbees.container;

import com.teamresourceful.resourcefulbees.mixin.ContainerAccessor;
import com.teamresourceful.resourcefulbees.registry.ModContainers;
import com.teamresourceful.resourcefulbees.tileentity.AbstractHoneyTankContainer;
import com.teamresourceful.resourcefulbees.tileentity.EnderBeeconTileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.IContainerListener;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

public class EnderBeeconContainer extends ContainerWithStackMove {

    private final EnderBeeconTileEntity enderBeeconTileEntity;
    private final PlayerEntity player;

    public EnderBeeconContainer(int id, World world, BlockPos pos, PlayerInventory inv) {
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
    public boolean stillValid(@NotNull PlayerEntity player) {
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

        for (IContainerListener listener : ((ContainerAccessor) this).getListeners()) {
            getEnderBeeconTileEntity().sendGUINetworkPacket(listener);
        }
    }

    public EnderBeeconTileEntity getEnderBeeconTileEntity() {
        return enderBeeconTileEntity;
    }

    public PlayerEntity getPlayer() {
        return player;
    }
}
