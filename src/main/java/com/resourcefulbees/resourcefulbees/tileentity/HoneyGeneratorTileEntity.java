package com.resourcefulbees.resourcefulbees.tileentity;

import com.resourcefulbees.resourcefulbees.block.HoneyGenerator;
import com.resourcefulbees.resourcefulbees.container.AutomationSensitiveItemStackHandler;
import com.resourcefulbees.resourcefulbees.container.HoneyGeneratorContainer;
import com.resourcefulbees.resourcefulbees.lib.CustomStorageContainers;
import com.resourcefulbees.resourcefulbees.registry.FluidRegistry;
import com.resourcefulbees.resourcefulbees.registry.RegistryHandler;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.concurrent.atomic.AtomicInteger;

public class HoneyGeneratorTileEntity extends TileEntity implements ITickableTileEntity, INamedContainerProvider {

    public static final int HONEY_BOTTLE_INPUT = 0;
    public static final int BOTTLE_OUTPUT = 1;
    public static final int HONEY_FILL_AMOUNT = 10;
    public static final int HONEY_DRAIN_AMOUNT = 5;
    public static final int ENERGY_FILL_AMOUNT = 125;
    public static final int ENERGY_TRANSFER_AMOUNT = 100;
    public static final int MAX_ENERGY_CAPACITY = 100000;
    public static final int MAX_TANK_STORAGE = 10000;
    public static final int HONEY_PER_BOTTLE = 250;
    public static final int ENERGY_PER_BOTTLE = 6250;



    public AutomationSensitiveItemStackHandler h = new HoneyGeneratorTileEntity.TileStackHandler(5, getAcceptor(), getRemover());
    public final CustomStorageContainers.CustomTankStorage fluidTank = new CustomStorageContainers.CustomTankStorage(MAX_TANK_STORAGE);
    public final CustomStorageContainers.CustomEnergyStorage energyStorage = createEnergy();
    private final LazyOptional<IFluidHandler> fluidOptional = LazyOptional.of(() -> fluidTank);
    private final LazyOptional<IItemHandler> lazyOptional = LazyOptional.of(() -> h);
    private final LazyOptional<IEnergyStorage> energy = LazyOptional.of(() -> energyStorage);
    public int fluidFilled;
    public int energyFilled;
    private boolean isProcessing;
    private boolean dirty;

    public HoneyGeneratorTileEntity() { super(RegistryHandler.HONEY_GENERATOR_ENTITY.get()); }

    @Override
    public void tick() {
        if (world != null && !world.isRemote) {
            if (canStartFluidProcess()) {
                startFluidProcess();
            }
            if (isProcessing) {
                processFluid();
            }
            if (!fluidTank.isEmpty() && this.canProcessEnergy()) {
                this.processEnergy();
            }
            if (!isProcessing && !this.canProcessEnergy()) {
                world.setBlockState(pos, getBlockState().with(HoneyGenerator.PROPERTY_ON, false));
            }
            if (dirty) {
                this.dirty = false;
                this.markDirty();
            }
        }
        sendOutPower();
    }

    private void sendOutPower() {
        AtomicInteger capacity = new AtomicInteger(energyStorage.getEnergyStored());
        if (capacity.get() > 0) {
            for (Direction direction : Direction.values()) {
                if (world != null) {
                    TileEntity te = world.getTileEntity(pos.offset(direction));
                    if (te != null) {
                        boolean doContinue = te.getCapability(CapabilityEnergy.ENERGY, direction).map(handler -> {
                            if (handler.canReceive()) {
                                int received = handler.receiveEnergy(Math.min(capacity.get(), ENERGY_TRANSFER_AMOUNT), false);
                                capacity.addAndGet(-received);
                                energyStorage.consumeEnergy(received);
                                markDirty();
                                return capacity.get() > 0;
                            } else {
                                return true;
                            }
                        }).orElse(true);
                        if (!doContinue) {
                            return;
                        }
                    }
                }
            }
        }
    }

    private boolean canStartFluidProcess() {
        return !isProcessing && !h.getStackInSlot(HONEY_BOTTLE_INPUT).isEmpty()&& h.getStackInSlot(HONEY_BOTTLE_INPUT).getItem().equals(Items.HONEY_BOTTLE) &&
                (h.getStackInSlot(BOTTLE_OUTPUT).isEmpty() || h.getStackInSlot(BOTTLE_OUTPUT).getCount() < h.getStackInSlot(BOTTLE_OUTPUT).getMaxStackSize());
    }

    private void startFluidProcess() {
        ItemStack honey_bottle = h.getStackInSlot(HONEY_BOTTLE_INPUT);
        ItemStack glass_bottle = h.getStackInSlot(BOTTLE_OUTPUT);
        honey_bottle.shrink(1);
        if (glass_bottle.isEmpty()) h.setStackInSlot(BOTTLE_OUTPUT, new ItemStack(Items.GLASS_BOTTLE));
        else glass_bottle.grow(1);
        fluidFilled = 0;
        isProcessing = true;
    }

    private void processFluid() {
        if(canProcessFluid()) {
            fluidTank.fill(new FluidStack(FluidRegistry.HONEY_FLUID.get(), HONEY_FILL_AMOUNT), IFluidHandler.FluidAction.EXECUTE);
            fluidFilled += HONEY_FILL_AMOUNT;
            isProcessing = fluidFilled < HONEY_PER_BOTTLE;
            if (!isProcessing) fluidFilled = 0;
            assert world != null : "World is null?";
            world.setBlockState(pos, getBlockState().with(HoneyGenerator.PROPERTY_ON, true));
            this.dirty = true;
        }
    }

    private boolean canProcessFluid() {
        return (fluidTank.getFluidAmount() + HONEY_FILL_AMOUNT) <= fluidTank.getCapacity();
    }

    public boolean canProcessEnergy(){
        return energyStorage.getEnergyStored() + ENERGY_FILL_AMOUNT <= energyStorage.getMaxEnergyStored() && fluidTank.getFluidAmount() >= HONEY_DRAIN_AMOUNT;
    }

    private void processEnergy() {
        if (this.canProcessEnergy()) {
            fluidTank.drain(HONEY_DRAIN_AMOUNT, IFluidHandler.FluidAction.EXECUTE);
            energyStorage.addEnergy(ENERGY_FILL_AMOUNT);
            energyFilled += ENERGY_FILL_AMOUNT;
            if (energyFilled >= ENERGY_FILL_AMOUNT) energyFilled = 0;
            assert world != null : "World is null?";
            world.setBlockState(pos, getBlockState().with(HoneyGenerator.PROPERTY_ON, true));
            this.dirty = true;
        }
    }

    @Nullable
    @Override
    public SUpdateTileEntityPacket getUpdatePacket() {
        CompoundNBT nbt = new CompoundNBT();
        nbt.put("tank",fluidTank.serializeNBT());
        nbt.put("power",energyStorage.serializeNBT());
        return new SUpdateTileEntityPacket(pos,0,nbt);
    }

    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
        CompoundNBT nbt = pkt.getNbtCompound();
        fluidTank.deserializeNBT(nbt.getCompound("tank"));
        energyStorage.deserializeNBT(nbt.getCompound("power"));
    }

    @Nonnull
    @Override
    public CompoundNBT write(CompoundNBT tag) {
        CompoundNBT inv = this.h.serializeNBT();
        tag.put("inv", inv);
        tag.put("energy", energyStorage.serializeNBT());
        tag.putInt("energyFilled", energyFilled);
        tag.put("fluid", fluidTank.serializeNBT());
        tag.putInt("bottleFilled", fluidFilled);
        tag.putBoolean("isProcessing", isProcessing);
        return super.write(tag);
    }

    @Override
    public void fromTag(@Nonnull BlockState state, CompoundNBT tag) {
        CompoundNBT invTag = tag.getCompound("inv");
        h.deserializeNBT(invTag);
        energyStorage.deserializeNBT(tag.getCompound("energy"));
        fluidTank.deserializeNBT(tag.getCompound("fluid"));
        if(tag.contains("energyFilled")) energyFilled = tag.getInt("energyFilled");
        if(tag.contains("fluidFilled")) fluidFilled = tag.getInt("fluidFilled");
        if(tag.contains("isProcessing")) isProcessing = tag.getBoolean("isProcessing");
        super.fromTag(state, tag);
    }

    @Nonnull
    @Override
    public CompoundNBT getUpdateTag() {
        CompoundNBT nbtTagCompound = new CompoundNBT();
        write(nbtTagCompound);
        return nbtTagCompound;
    }

    @Override
    public void handleUpdateTag(@Nonnull BlockState state, CompoundNBT tag) {
        this.fromTag(state, tag);
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (cap.equals(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)) return lazyOptional.cast();
        if (cap.equals(CapabilityEnergy.ENERGY)) return energy.cast();
        if (cap.equals(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY)) return fluidOptional.cast();
        return super.getCapability(cap, side);
    }

    public AutomationSensitiveItemStackHandler.IAcceptor getAcceptor() {
        return (slot, stack, automation) -> !automation || slot == 0;
    }

    public AutomationSensitiveItemStackHandler.IRemover getRemover() {
        return (slot, automation) -> !automation || slot == 1;
    }

    @Nullable
    @Override
    public Container createMenu(int id, @Nonnull PlayerInventory playerInventory, @Nonnull PlayerEntity playerEntity) {
        //noinspection ConstantConditions
        return new HoneyGeneratorContainer(id, world, pos, playerInventory);
    }

    @Nonnull
    @Override
    public ITextComponent getDisplayName() {
        return new TranslationTextComponent("gui.resourcefulbees.honey_generator");
    }

    private CustomStorageContainers.CustomEnergyStorage createEnergy() {
        return new CustomStorageContainers.CustomEnergyStorage(MAX_ENERGY_CAPACITY, 0, ENERGY_TRANSFER_AMOUNT) {
            @Override
            protected void onEnergyChanged() {
                markDirty();
            }
        };
    }

    protected class TileStackHandler extends AutomationSensitiveItemStackHandler {
        protected TileStackHandler(int slots, IAcceptor acceptor, IRemover remover) {
            super(slots,acceptor,remover);
        }
        @Override
        protected void onContentsChanged(int slot) {
            super.onContentsChanged(slot);
            markDirty();
        }
    }
}
