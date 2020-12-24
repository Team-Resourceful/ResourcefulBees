package com.resourcefulbees.resourcefulbees.tileentity;

import com.resourcefulbees.resourcefulbees.block.HoneyGenerator;
import com.resourcefulbees.resourcefulbees.capabilities.CustomEnergyStorage;
import com.resourcefulbees.resourcefulbees.config.Config;
import com.resourcefulbees.resourcefulbees.container.AutomationSensitiveItemStackHandler;
import com.resourcefulbees.resourcefulbees.container.HoneyGeneratorContainer;
import com.resourcefulbees.resourcefulbees.lib.ModConstants;
import com.resourcefulbees.resourcefulbees.network.NetPacketHandler;
import com.resourcefulbees.resourcefulbees.network.packets.SyncGUIMessage;
import com.resourcefulbees.resourcefulbees.registry.ModFluids;
import com.resourcefulbees.resourcefulbees.registry.ModTileEntityTypes;
import com.resourcefulbees.resourcefulbees.utils.BeeInfoUtils;
import io.netty.buffer.Unpooled;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.IContainerListener;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;

public class HoneyGeneratorTileEntity extends TileEntity implements ITickableTileEntity, INamedContainerProvider {

    public static final int HONEY_BOTTLE_INPUT = 0;
    public static final int BOTTLE_OUTPUT = 1;
    public static final int HONEY_FILL_AMOUNT = Config.HONEY_FILL_AMOUNT.get();
    public static final int HONEY_DRAIN_AMOUNT = Config.HONEY_DRAIN_AMOUNT.get();
    public static final int ENERGY_FILL_AMOUNT = Config.ENERGY_FILL_AMOUNT.get();
    public static final int ENERGY_TRANSFER_AMOUNT = Config.ENERGY_TRANSFER_AMOUNT.get();
    public static final int MAX_ENERGY_CAPACITY = Config.MAX_ENERGY_CAPACITY.get();
    public static final int MAX_TANK_STORAGE = Config.MAX_TANK_STORAGE.get();

    private static Predicate<FluidStack> honeyFluidPredicate() {
        return fluidStack -> fluidStack.getFluid().isIn(BeeInfoUtils.getFluidTag("forge:honey"));
    }

    public AutomationSensitiveItemStackHandler h = new HoneyGeneratorTileEntity.TileStackHandler(5, getAcceptor(), getRemover());
    public final FluidTank fluidTank = new FluidTank(MAX_TANK_STORAGE, honeyFluidPredicate());
    public final CustomEnergyStorage energyStorage = createEnergy();
    private final LazyOptional<IFluidHandler> fluidOptional = LazyOptional.of(() -> fluidTank);
    private final LazyOptional<IItemHandler> lazyOptional = LazyOptional.of(() -> h);
    private final LazyOptional<IEnergyStorage> energyOptional = LazyOptional.of(() -> energyStorage);
    public int fluidFilled;
    public int energyFilled;
    private boolean isProcessing;
    private boolean dirty;

    public HoneyGeneratorTileEntity() { super(ModTileEntityTypes.HONEY_GENERATOR_ENTITY.get()); }

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
            fluidTank.fill(new FluidStack(ModFluids.HONEY_STILL.get(), HONEY_FILL_AMOUNT), IFluidHandler.FluidAction.EXECUTE);
            fluidFilled += HONEY_FILL_AMOUNT;
            isProcessing = fluidFilled < ModConstants.HONEY_PER_BOTTLE;
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
            //energyStorage.receiveEnergy(ENERGY_FILL_AMOUNT, true);
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
        nbt.put("tank", fluidTank.writeToNBT(new CompoundNBT()));
        nbt.put("power", energyStorage.serializeNBT());
        return new SUpdateTileEntityPacket(pos,0,nbt);
    }

    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
        CompoundNBT nbt = pkt.getNbtCompound();
        fluidTank.readFromNBT(nbt.getCompound("tank"));
        energyStorage.deserializeNBT(nbt.getCompound("power"));
    }

    @Nonnull
    @Override
    public CompoundNBT write(CompoundNBT tag) {
        CompoundNBT inv = this.h.serializeNBT();
        tag.put("inv", inv);
        tag.put("energy", energyStorage.serializeNBT());
        tag.putInt("energyFilled", energyFilled);
        tag.put("fluid", fluidTank.writeToNBT(new CompoundNBT()));
        tag.putInt("bottleFilled", fluidFilled);
        tag.putBoolean("isProcessing", isProcessing);
        return super.write(tag);
    } //TODO 1.17 - change "fluid" to tank

    @Override
    public void fromTag(@Nonnull BlockState state, CompoundNBT tag) {
        CompoundNBT invTag = tag.getCompound("inv");
        h.deserializeNBT(invTag);
        energyStorage.deserializeNBT(tag.getCompound("energy"));
        fluidTank.readFromNBT(tag.getCompound("fluid"));
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
        if (cap.equals(CapabilityEnergy.ENERGY)) return energyOptional.cast();
        if (cap.equals(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY)) return fluidOptional.cast();
        return super.getCapability(cap, side);
    }

    @Override
    protected void invalidateCaps() {
        this.lazyOptional.invalidate();
        this.energyOptional.invalidate();
        this.fluidOptional.invalidate();
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

    private CustomEnergyStorage createEnergy() {
        return new CustomEnergyStorage(MAX_ENERGY_CAPACITY, 0, ENERGY_TRANSFER_AMOUNT) {
            @Override
            protected void onEnergyChanged() {
                markDirty();
            }
        };
    }

    public void sendGUINetworkPacket(IContainerListener player) {
        if (player instanceof ServerPlayerEntity && (!(player instanceof FakePlayer))) {
            PacketBuffer buffer = new PacketBuffer(Unpooled.buffer());
            buffer.writeFluidStack(fluidTank.getFluid());
            buffer.writeInt(energyStorage.getEnergyStored());
            NetPacketHandler.sendToPlayer(new SyncGUIMessage(this.pos, buffer), (ServerPlayerEntity) player);
        }
    }

    public void handleGUINetworkPacket(PacketBuffer buffer) {
        fluidTank.setFluid(buffer.readFluidStack());
        energyStorage.setEnergy(buffer.readInt());
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
