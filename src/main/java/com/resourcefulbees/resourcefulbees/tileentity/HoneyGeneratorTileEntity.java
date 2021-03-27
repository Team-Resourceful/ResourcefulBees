package com.resourcefulbees.resourcefulbees.tileentity;

import com.resourcefulbees.resourcefulbees.block.HoneyGenerator;
import com.resourcefulbees.resourcefulbees.capabilities.CustomEnergyStorage;
import com.resourcefulbees.resourcefulbees.config.Config;
import com.resourcefulbees.resourcefulbees.container.AutomationSensitiveItemStackHandler;
import com.resourcefulbees.resourcefulbees.container.HoneyGeneratorContainer;
import com.resourcefulbees.resourcefulbees.lib.NBTConstants;
import com.resourcefulbees.resourcefulbees.network.NetPacketHandler;
import com.resourcefulbees.resourcefulbees.network.packets.SyncGUIMessage;
import com.resourcefulbees.resourcefulbees.registry.ModTileEntityTypes;
import com.resourcefulbees.resourcefulbees.utils.BeeInfoUtils;
import io.netty.buffer.Unpooled;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.IContainerListener;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.BucketItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tags.ITag;
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
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.concurrent.atomic.AtomicInteger;

public class HoneyGeneratorTileEntity extends AbstractHoneyTankContainer implements ITickableTileEntity, INamedContainerProvider {

    public static final int HONEY_BOTTLE_INPUT = 0;
    public static final int BOTTLE_OUTPUT = 1;
    public static final int HONEY_DRAIN_AMOUNT = Config.HONEY_DRAIN_AMOUNT.get();
    public static final int ENERGY_FILL_AMOUNT = Config.ENERGY_FILL_AMOUNT.get();
    public static final int ENERGY_TRANSFER_AMOUNT = Config.ENERGY_TRANSFER_AMOUNT.get();
    public static final int MAX_ENERGY_CAPACITY = Config.MAX_ENERGY_CAPACITY.get();
    public static final int MAX_TANK_STORAGE = Config.MAX_TANK_STORAGE.get();


    private final HoneyGeneratorTileEntity.TileStackHandler tileStackHandler = new HoneyGeneratorTileEntity.TileStackHandler(5, getAcceptor(), getRemover());
    public final CustomEnergyStorage energyStorage = createEnergy();
    private final LazyOptional<IItemHandler> lazyOptional = LazyOptional.of(this::getTileStackHandler);
    private final LazyOptional<IEnergyStorage> energyOptional = LazyOptional.of(() -> energyStorage);

    public static final ITag<Fluid> HONEY_FLUID_TAG = BeeInfoUtils.getFluidTag("forge:honey");
    public static final ITag<Item> HONEY_BOTTLE_TAG = BeeInfoUtils.getItemTag("forge:honey_bottle");

    private int fluidFilled;
    private int energyFilled;
    private boolean isProcessing;

    public HoneyGeneratorTileEntity() {
        super(ModTileEntityTypes.HONEY_GENERATOR_ENTITY.get(), MAX_TANK_STORAGE);
    }

    @Override
    public void tick() {
        if (level != null && !level.isClientSide) {
            if (!getFluidTank().isEmpty() && this.canProcessEnergy()) {
                this.processEnergy();
            }
            if (!isProcessing && !this.canProcessEnergy()) {
                level.setBlockAndUpdate(worldPosition, getBlockState().setValue(HoneyGenerator.PROPERTY_ON, false));
            }
            super.tick();
        }
        sendOutPower();
    }

    private void sendOutPower() {
        AtomicInteger capacity = new AtomicInteger(energyStorage.getEnergyStored());
        if (capacity.get() > 0 && level != null) {
            for (Direction direction : Direction.values()) {
                TileEntity te = level.getBlockEntity(worldPosition.relative(direction));
                if (capacity.get() > 0 && te != null) {
                    te.getCapability(CapabilityEnergy.ENERGY, direction).ifPresent(handler -> {
                        if (handler.canReceive()) {
                            int received = handler.receiveEnergy(Math.min(capacity.get(), ENERGY_TRANSFER_AMOUNT), false);
                            capacity.addAndGet(-received);
                            energyStorage.consumeEnergy(received);
                            setChanged();
                        }
                    });
                }
            }
        }
    }

    public boolean canProcessEnergy() {
        return energyStorage.getEnergyStored() + ENERGY_FILL_AMOUNT <= energyStorage.getMaxEnergyStored() && getFluidTank().getFluidAmount() >= HONEY_DRAIN_AMOUNT;
    }

    private void processEnergy() {
        if (this.canProcessEnergy()) {
            getFluidTank().drain(HONEY_DRAIN_AMOUNT, IFluidHandler.FluidAction.EXECUTE);
            energyStorage.addEnergy(ENERGY_FILL_AMOUNT);
            setEnergyFilled(getEnergyFilled() + ENERGY_FILL_AMOUNT);
            if (getEnergyFilled() >= ENERGY_FILL_AMOUNT) setEnergyFilled(0);
            assert level != null : "World is null?";
            level.setBlockAndUpdate(worldPosition, getBlockState().setValue(HoneyGenerator.PROPERTY_ON, true));
            setDirty();
        }
    }

    @Nullable
    @Override
    public SUpdateTileEntityPacket getUpdatePacket() {
        CompoundNBT nbt = new CompoundNBT();
        nbt.put("tank", getFluidTank().writeToNBT(new CompoundNBT()));
        nbt.put("power", energyStorage.serializeNBT());
        return new SUpdateTileEntityPacket(worldPosition, 0, nbt);
    }

    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
        CompoundNBT nbt = pkt.getTag();
        getFluidTank().readFromNBT(nbt.getCompound("tank"));
        energyStorage.deserializeNBT(nbt.getCompound("power"));
    }

    @NotNull
    @Override
    public CompoundNBT save(CompoundNBT tag) {
        CompoundNBT inv = this.getTileStackHandler().serializeNBT();
        tag.put(NBTConstants.NBT_INVENTORY, inv);
        tag.put(NBTConstants.NBT_ENERGY, energyStorage.serializeNBT());
        tag.put(NBTConstants.NBT_FLUID, getFluidTank().writeToNBT(new CompoundNBT()));
        tag.putInt(NBTConstants.NBT_ENERGY_FILLED, getEnergyFilled());
        tag.putInt(NBTConstants.NBT_FLUID_FILLED, getFluidFilled());
        tag.putBoolean(NBTConstants.NBT_IS_PROCESSING, isProcessing);
        return super.save(tag);
    } //TODO 1.17 - change "fluid" to tank

    @Override
    public void load(@NotNull BlockState state, CompoundNBT tag) {
        CompoundNBT invTag = tag.getCompound(NBTConstants.NBT_INVENTORY);
        getTileStackHandler().deserializeNBT(invTag);
        energyStorage.deserializeNBT(tag.getCompound(NBTConstants.NBT_ENERGY));
        getFluidTank().readFromNBT(tag.getCompound(NBTConstants.NBT_FLUID));
        if (tag.contains(NBTConstants.NBT_ENERGY_FILLED)) setEnergyFilled(tag.getInt(NBTConstants.NBT_ENERGY_FILLED));
        if (tag.contains(NBTConstants.NBT_FLUID_FILLED)) setFluidFilled(tag.getInt(NBTConstants.NBT_FLUID_FILLED));
        if (tag.contains(NBTConstants.NBT_IS_PROCESSING)) isProcessing = tag.getBoolean(NBTConstants.NBT_IS_PROCESSING);
        super.load(state, tag);
    }

    @NotNull
    @Override
    public CompoundNBT getUpdateTag() {
        CompoundNBT nbtTagCompound = new CompoundNBT();
        save(nbtTagCompound);
        return nbtTagCompound;
    }

    @Override
    public void handleUpdateTag(@NotNull BlockState state, CompoundNBT tag) {
        this.load(state, tag);
    }

    @NotNull
    @Override
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap.equals(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)) return lazyOptional.cast();
        if (cap.equals(CapabilityEnergy.ENERGY)) return energyOptional.cast();
        if (cap.equals(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY)) return getFluidOptional().cast();
        return super.getCapability(cap, side);
    }

    @Override
    protected void invalidateCaps() {
        this.lazyOptional.invalidate();
        this.energyOptional.invalidate();
        this.getFluidOptional().invalidate();
    }

    @Nullable
    @Override
    public Container createMenu(int id, @NotNull PlayerInventory playerInventory, @NotNull PlayerEntity playerEntity) {
        //noinspection ConstantConditions
        return new HoneyGeneratorContainer(id, level, worldPosition, playerInventory);
    }

    @NotNull
    @Override
    public ITextComponent getDisplayName() {
        return new TranslationTextComponent("gui.resourcefulbees.honey_generator");
    }

    private CustomEnergyStorage createEnergy() {
        return new CustomEnergyStorage(MAX_ENERGY_CAPACITY, 0, ENERGY_TRANSFER_AMOUNT) {
            @Override
            protected void onEnergyChanged() {
                setChanged();
            }
        };
    }

    public void sendGUINetworkPacket(IContainerListener player) {
        if (player instanceof ServerPlayerEntity && (!(player instanceof FakePlayer))) {
            PacketBuffer buffer = new PacketBuffer(Unpooled.buffer());
            buffer.writeFluidStack(getFluidTank().getFluid());
            buffer.writeInt(energyStorage.getEnergyStored());
            NetPacketHandler.sendToPlayer(new SyncGUIMessage(this.worldPosition, buffer), (ServerPlayerEntity) player);
        }
    }

    public void handleGUINetworkPacket(PacketBuffer buffer) {
        getFluidTank().setFluid(buffer.readFluidStack());
        energyStorage.setEnergy(buffer.readInt());
    }



    @Override
    public @NotNull TileStackHandler getTileStackHandler() {
        return tileStackHandler;
    }

    public int getFluidFilled() {
        return fluidFilled;
    }

    public void setFluidFilled(int fluidFilled) {
        this.fluidFilled = fluidFilled;
    }

    public int getEnergyFilled() {
        return energyFilled;
    }

    public void setEnergyFilled(int energyFilled) {
        this.energyFilled = energyFilled;
    }

    public class TileStackHandler extends AutomationSensitiveItemStackHandler {
        protected TileStackHandler(int slots, IAcceptor acceptor, IRemover remover) {
            super(slots, acceptor, remover);
        }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            if (stack.getItem() instanceof BucketItem) {
                BucketItem bucket = (BucketItem) stack.getItem();
                return bucket.getFluid().is(HONEY_FLUID_TAG);
            } else {
                return stack.getItem().is(HONEY_BOTTLE_TAG);
            }
        }

        @Override
        protected void onContentsChanged(int slot) {
            super.onContentsChanged(slot);
            setChanged();
        }
    }
}
