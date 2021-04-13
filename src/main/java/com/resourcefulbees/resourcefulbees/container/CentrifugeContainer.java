package com.resourcefulbees.resourcefulbees.container;

import com.resourcefulbees.resourcefulbees.capabilities.MultiFluidTank;
import com.resourcefulbees.resourcefulbees.config.Config;
import com.resourcefulbees.resourcefulbees.mixin.ContainerAccessor;
import com.resourcefulbees.resourcefulbees.registry.ModContainers;
import com.resourcefulbees.resourcefulbees.tileentity.CentrifugeTileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.IContainerListener;
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
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import org.jetbrains.annotations.NotNull;

public class CentrifugeContainer extends ContainerWithStackMove {

    protected final CentrifugeTileEntity centrifugeTileEntity;
    private final PlayerInventory inv;
    private final IIntArray times;
    private int numInputs;
    private boolean displayFluids;
    protected int inputXPos;
    protected int outputXPos;
    protected IntReferenceHolder requiresRedstone = new IntReferenceHolder() {
        @Override
        public int get() { return centrifugeTileEntity.getRequiresRedstone() ? 1 : 0; }

        @Override
        public void set(int value) { centrifugeTileEntity.setRequiresRedstone(value == 1); }
    };

    public CentrifugeContainer(int id, World world, BlockPos pos, PlayerInventory inv) {
        this(ModContainers.CENTRIFUGE_CONTAINER.get(), id, world, pos, inv, new IntArray(1));
    }

    public CentrifugeContainer(ContainerType<?> containerType, int id, World world, BlockPos pos, PlayerInventory inv, IIntArray times) {
        super(containerType, id);
        initialize();
        this.inv = inv;
        this.times = times;
        centrifugeTileEntity = (CentrifugeTileEntity) world.getBlockEntity(pos);
        this.addDataSlots(times);
        this.addDataSlot(requiresRedstone);
        setupSlots();
    }

    protected void initialize() {
        inputXPos = 89;
        outputXPos = 80;
    }

    public void setDisplayFluids(boolean displayFluids) {
        this.displayFluids = displayFluids;
    }

    public boolean shouldDisplayFluids() {
        return displayFluids;
    }

    public void setupSlots() {
        if (centrifugeTileEntity != null) {
            this.slots.clear();
            numInputs = centrifugeTileEntity.getNumberOfInputs();

            this.addSlot(new SlotItemHandlerUnconditioned(centrifugeTileEntity.getItemStackHandler(), CentrifugeTileEntity.BOTTLE_SLOT, 26, 8) {

                @Override
                public boolean mayPlace(ItemStack stack) { return stack.getItem().equals(Items.GLASS_BOTTLE); }
            });

            int x = inputXPos + 18;
            for (int i = 0; i < centrifugeTileEntity.getHoneycombSlots().length; i++) {
                this.addSlot(new SlotItemHandlerUnconditioned(centrifugeTileEntity.getItemStackHandler(), centrifugeTileEntity.getHoneycombSlots()[i], x, 8) {

                    @Override
                    public boolean mayPlace(ItemStack stack) { return !stack.getItem().equals(Items.GLASS_BOTTLE); }
                });
                x += 36;
            }
            for (int i = 0; i < getSlotsPerRow(); i++) {
                x = 18 + outputXPos + i * 18;
                this.addSlot(new OutputSlot(centrifugeTileEntity.getItemStackHandler(), centrifugeTileEntity.getOutputSlots()[i], x, 44) {
                    @Override
                    public boolean isActive() {
                        return !displayFluids;
                    }
                });
                this.addSlot(new OutputSlot(centrifugeTileEntity.getItemStackHandler(), centrifugeTileEntity.getOutputSlots()[i + (numInputs * 2)], x, 62) {
                    @Override
                    public boolean isActive() {
                        return !displayFluids;
                    }
                });
                this.addSlot(new OutputSlot(centrifugeTileEntity.getItemStackHandler(), centrifugeTileEntity.getOutputSlots()[i + (numInputs * 4)], x, 80) {
                    @Override
                    public boolean isActive() {
                        return !displayFluids;
                    }
                });
            }
        }

        int xStart = (int) (Math.max(numInputs * 36 + 70, 178) * 0.5) - 81;
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(inv, j + i * 9 + 9, xStart + j * 18, 104 + i * 18));
            }
        }

        for (int k = 0; k < 9; ++k) {
            this.addSlot(new Slot(inv, k, xStart + k * 18, 162));
        }

    }

    public CentrifugeTileEntity getCentrifugeTileEntity() { return centrifugeTileEntity; }

    protected int getSlotsPerRow() { return numInputs * 2; }

    public int getEnergy() {
        return centrifugeTileEntity.getCapability(CapabilityEnergy.ENERGY).map(IEnergyStorage::getEnergyStored).orElse(0);
    }

    public int getMaxEnergy() {
        return centrifugeTileEntity.getCapability(CapabilityEnergy.ENERGY).map(IEnergyStorage::getMaxEnergyStored).orElse(Config.MAX_CENTRIFUGE_RF.get());
    }

    public FluidStack getFluidInTank(int i) {
        return centrifugeTileEntity.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY).map(iFluidHandler -> iFluidHandler.getFluidInTank(i)).orElse(null);
    }

    public int getMaxTankCapacity() {
        return centrifugeTileEntity.getMaxTankCapacity();
    }

    public int getFluidAmountInTank(int i) {
        return centrifugeTileEntity.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY)
                .map(iFluidHandler -> ((MultiFluidTank) iFluidHandler).getFluidAmountInTank(i))
                .orElse(0);
    }

    public int getTime(int i) { return this.times.get(i); }

    public int getTotalTime(int i) { return this.centrifugeTileEntity.getRecipeTime(i); }

    public boolean getRequiresRedstone() {
        return centrifugeTileEntity.getRequiresRedstone();
    }

    /**
     * Determines whether supplied player can use this container
     *
     * @param player the player
     */
    @Override
    public boolean stillValid(@NotNull PlayerEntity player) { return true; }

    @Override
    public int getContainerInputEnd() {
        return 1 + centrifugeTileEntity.getNumberOfInputs();
    }

    @Override
    public int getInventoryStart() {
        return centrifugeTileEntity.getTotalSlots();
    }

    @Override
    public void broadcastChanges() {
        super.broadcastChanges();
        if (centrifugeTileEntity == null) {
            return;
        }

        for (IContainerListener listener : ((ContainerAccessor) this).getListeners()) {
            centrifugeTileEntity.sendGUINetworkPacket(listener);
        }
    }
}
