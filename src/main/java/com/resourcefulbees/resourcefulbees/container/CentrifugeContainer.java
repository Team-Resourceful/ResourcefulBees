package com.resourcefulbees.resourcefulbees.container;

import com.resourcefulbees.resourcefulbees.capabilities.MultiFluidTank;
import com.resourcefulbees.resourcefulbees.config.Config;
import com.resourcefulbees.resourcefulbees.container.sync.SyncPower;
import com.resourcefulbees.resourcefulbees.registry.ModContainers;
import com.resourcefulbees.resourcefulbees.tileentity.CentrifugeTileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.IntArray;
import net.minecraft.util.IntReferenceHolder;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;

import javax.annotation.Nonnull;

public class CentrifugeContainer extends Container {

    protected final CentrifugeTileEntity centrifugeTileEntity;
    private final PlayerInventory inv;
    private final IntArray times;
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

    public CentrifugeContainer(ContainerType<?> containerType, int id, World world, BlockPos pos, PlayerInventory inv, IntArray times) {
        super(containerType, id);
        initialize();
        this.inv = inv;
        this.times = times;
        centrifugeTileEntity = (CentrifugeTileEntity) world.getTileEntity(pos);
        this.trackIntArray(SyncPower.getSyncableArray(centrifugeTileEntity, getEnergy()));
        this.trackIntArray(times);
        this.trackInt(requiresRedstone);
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
            this.inventorySlots.clear();
            numInputs = centrifugeTileEntity.getNumberOfInputs();

            this.addSlot(new SlotItemHandlerUnconditioned(centrifugeTileEntity.getItemStackHandler(), CentrifugeTileEntity.BOTTLE_SLOT, 26, 8) {
                public boolean isItemValid(ItemStack stack) { return stack.getItem().equals(Items.GLASS_BOTTLE); }
            });

            int x = inputXPos + 18;
            for (int i = 0; i < centrifugeTileEntity.getHoneycombSlots().length; i++) {
                this.addSlot(new SlotItemHandlerUnconditioned(centrifugeTileEntity.getItemStackHandler(), centrifugeTileEntity.getHoneycombSlots()[i], x, 8) {
                    public boolean isItemValid(ItemStack stack) { return !stack.getItem().equals(Items.GLASS_BOTTLE); }
                });
                x += 36;
            }
            for (int i = 0; i < getSlotsPerRow(); i++) {
                x = 18 + outputXPos + i * 18;
                this.addSlot(new OutputSlot(centrifugeTileEntity.getItemStackHandler(), centrifugeTileEntity.getOutputSlots()[i], x, 44) {
                    @Override
                    public boolean isEnabled() {
                        return !displayFluids;
                    }
                });
                this.addSlot(new OutputSlot(centrifugeTileEntity.getItemStackHandler(), centrifugeTileEntity.getOutputSlots()[i + (numInputs * 2)], x, 62) {
                    @Override
                    public boolean isEnabled() {
                        return !displayFluids;
                    }
                });
                this.addSlot(new OutputSlot(centrifugeTileEntity.getItemStackHandler(), centrifugeTileEntity.getOutputSlots()[i + (numInputs * 4)], x, 80) {
                    @Override
                    public boolean isEnabled() {
                        return !displayFluids;
                    }
                });
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
    public boolean canInteractWith(@Nonnull PlayerEntity player) { return true; }

    @Nonnull
    @Override
    public ItemStack transferStackInSlot(@Nonnull PlayerEntity playerIn, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.inventorySlots.get(index);
        int totalSlots = centrifugeTileEntity.getTotalSlots();
        int totalInputs = 1 + centrifugeTileEntity.getNumberOfInputs();

        if (slot != null && slot.getHasStack()) {
            ItemStack slotStack = slot.getStack();
            itemstack = slotStack.copy();
            if (index < totalSlots) {
                if (!this.mergeItemStack(slotStack, totalSlots, inventorySlots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.mergeItemStack(slotStack, 0, totalInputs, false)) {
                return ItemStack.EMPTY;
            }

            if (slotStack.isEmpty()) {
                slot.putStack(ItemStack.EMPTY);
            } else {
                slot.onSlotChanged();
            }
        }
        return itemstack;
    }


    @Override
    public void detectAndSendChanges() {
        super.detectAndSendChanges();
    }
}
