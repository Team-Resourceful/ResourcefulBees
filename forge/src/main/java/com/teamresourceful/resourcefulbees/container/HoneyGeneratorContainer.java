package com.teamresourceful.resourcefulbees.container;

import com.teamresourceful.resourcefulbees.mixin.ContainerAccessor;
import com.teamresourceful.resourcefulbees.registry.ModContainers;
import com.teamresourceful.resourcefulbees.tileentity.HoneyGeneratorTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerListener;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import org.jetbrains.annotations.NotNull;

public class HoneyGeneratorContainer extends ContainerWithStackMove {

    private final HoneyGeneratorTileEntity honeyGeneratorTileEntity;
    private final Player player;

    public HoneyGeneratorContainer(int id, Level world, BlockPos pos, Inventory inv) {
        super(ModContainers.HONEY_GENERATOR_CONTAINER.get(), id);

        this.player = inv.player;

        honeyGeneratorTileEntity = (HoneyGeneratorTileEntity) world.getBlockEntity(pos);
        if (honeyGeneratorTileEntity != null) {
            this.addSlot(new SlotItemHandlerUnconditioned(honeyGeneratorTileEntity.getTileStackHandler(), HoneyGeneratorTileEntity.HONEY_BOTTLE_INPUT, 36, 20) {

                @Override
                public boolean mayPlace(ItemStack stack) {
                    return honeyGeneratorTileEntity.getTileStackHandler().isItemValid(HoneyGeneratorTileEntity.HONEY_BOTTLE_INPUT, stack);
                }
            });
            this.addSlot(new OutputSlot(honeyGeneratorTileEntity.getTileStackHandler(), HoneyGeneratorTileEntity.BOTTLE_OUTPUT, 36, 58));

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
    
    public int getFluidAmount() { return getHoneyGeneratorTileEntity().getFluidTank().getFluidAmount(); }
    
    public int getMaxEnergy() { return getHoneyGeneratorTileEntity().getEnergyStorage().getMaxEnergyStored(); }

    public int getMaxFluid() { return getHoneyGeneratorTileEntity().getFluidTank().getCapacity(); }

    public int getTime() { return getHoneyGeneratorTileEntity().getFluidFilled(); }

    public int getEnergyTime() { return getHoneyGeneratorTileEntity().getEnergyFilled(); }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return true;
    }

    @Override
    public int getContainerInputEnd() {
        return 1;
    }

    @Override
    public int getInventoryStart() {
        return 2;
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
