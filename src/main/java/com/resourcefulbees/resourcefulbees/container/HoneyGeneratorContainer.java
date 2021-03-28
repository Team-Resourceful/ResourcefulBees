package com.resourcefulbees.resourcefulbees.container;

import com.resourcefulbees.resourcefulbees.mixin.ContainerAccessor;
import com.resourcefulbees.resourcefulbees.registry.ModContainers;
import com.resourcefulbees.resourcefulbees.tileentity.HoneyGeneratorTileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.IContainerListener;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import org.jetbrains.annotations.NotNull;

public class HoneyGeneratorContainer extends ContainerWithStackMove {

    private final HoneyGeneratorTileEntity honeyGeneratorTileEntity;
    private final PlayerEntity player;

    public HoneyGeneratorContainer(int id, World world, BlockPos pos, PlayerInventory inv) {
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
    
    public int getFluidAmount() { return getHoneyGeneratorTileEntity().getFluidTank().getFluidAmount(); }
    
    public int getMaxEnergy() { return getHoneyGeneratorTileEntity().energyStorage.getMaxEnergyStored(); }

    public int getMaxFluid() { return getHoneyGeneratorTileEntity().getFluidTank().getCapacity(); }

    public int getTime() { return getHoneyGeneratorTileEntity().getFluidFilled(); }

    public int getEnergyTime() { return getHoneyGeneratorTileEntity().getEnergyFilled(); }

    @Override
    public boolean stillValid(@NotNull PlayerEntity player) {
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

        for (IContainerListener listener : ((ContainerAccessor) this).getListeners()) {
            getHoneyGeneratorTileEntity().sendGUINetworkPacket(listener);
        }
    }

    public HoneyGeneratorTileEntity getHoneyGeneratorTileEntity() {
        return honeyGeneratorTileEntity;
    }

    public PlayerEntity getPlayer() {
        return player;
    }
}
