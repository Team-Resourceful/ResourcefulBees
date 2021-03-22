package com.resourcefulbees.resourcefulbees.tileentity;

import com.resourcefulbees.resourcefulbees.block.HoneyGenerator;
import com.resourcefulbees.resourcefulbees.capabilities.CustomEnergyStorage;
import com.resourcefulbees.resourcefulbees.config.Config;
import com.resourcefulbees.resourcefulbees.container.AutomationSensitiveItemStackHandler;
import com.resourcefulbees.resourcefulbees.container.HoneyGeneratorContainer;
import com.resourcefulbees.resourcefulbees.item.CustomHoneyBottleItem;
import com.resourcefulbees.resourcefulbees.lib.ModConstants;
import com.resourcefulbees.resourcefulbees.lib.NBTConstants;
import com.resourcefulbees.resourcefulbees.network.NetPacketHandler;
import com.resourcefulbees.resourcefulbees.network.packets.SyncGUIMessage;
import com.resourcefulbees.resourcefulbees.registry.ModFluids;
import com.resourcefulbees.resourcefulbees.registry.ModItems;
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
import net.minecraft.item.Items;
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
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;

import com.resourcefulbees.resourcefulbees.container.AutomationSensitiveItemStackHandler.IAcceptor;
import com.resourcefulbees.resourcefulbees.container.AutomationSensitiveItemStackHandler.IRemover;

public class HoneyGeneratorTileEntity extends TileEntity implements ITickableTileEntity, INamedContainerProvider {

    public static final int HONEY_BOTTLE_INPUT = 0;
    public static final int BOTTLE_OUTPUT = 1;
    public static final int HONEY_FILL_AMOUNT = ModConstants.HONEY_PER_BOTTLE;
    public static final int HONEY_DRAIN_AMOUNT = Config.HONEY_DRAIN_AMOUNT.get();
    public static final int ENERGY_FILL_AMOUNT = Config.ENERGY_FILL_AMOUNT.get();
    public static final int ENERGY_TRANSFER_AMOUNT = Config.ENERGY_TRANSFER_AMOUNT.get();
    public static final int MAX_ENERGY_CAPACITY = Config.MAX_ENERGY_CAPACITY.get();
    public static final int MAX_TANK_STORAGE = Config.MAX_TANK_STORAGE.get();

    private static Predicate<FluidStack> honeyFluidPredicate() {
        return fluidStack -> fluidStack.getFluid().is(HONEY_FLUID_TAG);
    }

    private final HoneyGeneratorTileEntity.TileStackHandler tileStackHandler = new HoneyGeneratorTileEntity.TileStackHandler(5, getAcceptor(), getRemover());
    public final InternalFluidTank fluidTank = new InternalFluidTank(MAX_TANK_STORAGE, honeyFluidPredicate());
    public final CustomEnergyStorage energyStorage = createEnergy();
    private final LazyOptional<IFluidHandler> fluidOptional = LazyOptional.of(() -> fluidTank);
    private final LazyOptional<IItemHandler> lazyOptional = LazyOptional.of(this::getTileStackHandler);
    private final LazyOptional<IEnergyStorage> energyOptional = LazyOptional.of(() -> energyStorage);

    public static final ITag<Fluid> HONEY_FLUID_TAG = BeeInfoUtils.getFluidTag("forge:honey");
    public static final ITag<Item> HONEY_BOTTLE_TAG = BeeInfoUtils.getItemTag("forge:honey_bottle");

    private int fluidFilled;
    private int energyFilled;
    private boolean isProcessing;
    private boolean dirty;

    public HoneyGeneratorTileEntity() {
        super(ModTileEntityTypes.HONEY_GENERATOR_ENTITY.get());
    }

    @Override
    public void tick() {
        if (level != null && !level.isClientSide) {
            if (canStartFluidProcess()) {
                processFluid();
            }
            if (!fluidTank.isEmpty() && this.canProcessEnergy()) {
                this.processEnergy();
            }
            if (!isProcessing && !this.canProcessEnergy()) {
                level.setBlockAndUpdate(worldPosition, getBlockState().setValue(HoneyGenerator.PROPERTY_ON, false));
            }
            if (dirty) {
                this.dirty = false;
                this.setChanged();
            }
        }
        sendOutPower();
    }

    private void sendOutPower() {
        AtomicInteger capacity = new AtomicInteger(energyStorage.getEnergyStored());
        if (capacity.get() > 0) {
            for (Direction direction : Direction.values()) {
                if (level != null) {
                    TileEntity te = level.getBlockEntity(worldPosition.relative(direction));
                    if (te != null) {
                        boolean doContinue = te.getCapability(CapabilityEnergy.ENERGY, direction).map(handler -> {
                            if (handler.canReceive()) {
                                int received = handler.receiveEnergy(Math.min(capacity.get(), ENERGY_TRANSFER_AMOUNT), false);
                                capacity.addAndGet(-received);
                                energyStorage.consumeEnergy(received);
                                setChanged();
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
        ItemStack stack = getTileStackHandler().getStackInSlot(HONEY_BOTTLE_INPUT);
        ItemStack output = getTileStackHandler().getStackInSlot(BOTTLE_OUTPUT);

        boolean stackValid = false;
        boolean isBucket = false;
        boolean outputValid;
        boolean hasRoom;
        if (!stack.isEmpty()) {
            if (stack.getItem() instanceof BucketItem) {
                BucketItem bucket = (BucketItem) stack.getItem();
                stackValid = bucket.getFluid().is(HONEY_FLUID_TAG);
                isBucket = true;
            } else {
                stackValid = stack.getItem().is(HONEY_BOTTLE_TAG);
            }
        }
        if (!output.isEmpty()) {
            if (isBucket) {
                outputValid = output.getItem() == Items.BUCKET;
            } else {
                outputValid = output.getItem() == Items.GLASS_BOTTLE;
            }
            hasRoom = output.getCount() < output.getMaxStackSize();
        } else {
            outputValid = true;
            hasRoom = true;
        }
        return stackValid && outputValid && hasRoom;
    }

    private void processFluid() {
        if (canProcessFluid()) {
            ItemStack stack = getTileStackHandler().getStackInSlot(HONEY_BOTTLE_INPUT);
            ItemStack output = getTileStackHandler().getStackInSlot(BOTTLE_OUTPUT);
            if (stack.getItem() instanceof BucketItem) {
                BucketItem bucket = (BucketItem) stack.getItem();
                fluidTank.fill(new FluidStack(bucket.getFluid(), 1000), IFluidHandler.FluidAction.EXECUTE);
                stack.shrink(1);
                if (output.isEmpty()) {
                    getTileStackHandler().setStackInSlot(BOTTLE_OUTPUT, new ItemStack(Items.BUCKET));
                } else {
                    output.grow(1);
                }
            } else {
                if (stack.getItem() instanceof CustomHoneyBottleItem) {
                    CustomHoneyBottleItem item = (CustomHoneyBottleItem) stack.getItem();
                    FluidStack fluid = new FluidStack(item.getHoneyData().getHoneyStillFluidRegistryObject().get().getSource(), HONEY_FILL_AMOUNT);
                    fluidTank.fill(fluid, IFluidHandler.FluidAction.EXECUTE);
                } else if (stack.getItem() == ModItems.CATNIP_HONEY_BOTTLE.get()) {
                    FluidStack fluid = new FluidStack(ModFluids.CATNIP_HONEY_STILL.get(), HONEY_FILL_AMOUNT);
                    fluidTank.fill(fluid, IFluidHandler.FluidAction.EXECUTE);
                } else {
                    FluidStack fluid = new FluidStack(ModFluids.HONEY_STILL.get(), HONEY_FILL_AMOUNT);
                    fluidTank.fill(fluid, IFluidHandler.FluidAction.EXECUTE);
                }
                stack.shrink(1);
                if (output.isEmpty()) {
                    getTileStackHandler().setStackInSlot(BOTTLE_OUTPUT, new ItemStack(Items.GLASS_BOTTLE));
                } else {
                    output.grow(1);
                }
            }
            this.dirty = true;
        }
    }

    private boolean canProcessFluid() {
        boolean spaceLeft;
        ItemStack stack = getTileStackHandler().getStackInSlot(HONEY_BOTTLE_INPUT);
        Fluid fluid = ModFluids.HONEY_STILL.get();
        if (stack.getItem() instanceof BucketItem) {
            BucketItem item = (BucketItem) stack.getItem();
            spaceLeft = (fluidTank.getFluidAmount() + 1000) <= fluidTank.getCapacity();
            fluid = item.getFluid();
        } else {
            spaceLeft = (fluidTank.getFluidAmount() + HONEY_FILL_AMOUNT) <= fluidTank.getCapacity();
            if (stack.getItem() instanceof CustomHoneyBottleItem) {
                CustomHoneyBottleItem item = (CustomHoneyBottleItem) stack.getItem();
                fluid = item.getHoneyData().getHoneyStillFluidRegistryObject().get().getSource();
            } else if (stack.getItem() == ModItems.CATNIP_HONEY_BOTTLE.get()) {
                fluid = ModFluids.CATNIP_HONEY_STILL.get();
            }
        }
        return spaceLeft && (fluidTank.getFluid().getFluid() == fluid || fluidTank.isEmpty());
    }

    public boolean canProcessEnergy() {
        return energyStorage.getEnergyStored() + ENERGY_FILL_AMOUNT <= energyStorage.getMaxEnergyStored() && fluidTank.getFluidAmount() >= HONEY_DRAIN_AMOUNT;
    }

    private void processEnergy() {
        if (this.canProcessEnergy()) {
            fluidTank.drain(HONEY_DRAIN_AMOUNT, IFluidHandler.FluidAction.EXECUTE);
            energyStorage.addEnergy(ENERGY_FILL_AMOUNT);
            setEnergyFilled(getEnergyFilled() + ENERGY_FILL_AMOUNT);
            if (getEnergyFilled() >= ENERGY_FILL_AMOUNT) setEnergyFilled(0);
            assert level != null : "World is null?";
            level.setBlockAndUpdate(worldPosition, getBlockState().setValue(HoneyGenerator.PROPERTY_ON, true));
            this.dirty = true;
        }
    }

    @Nullable
    @Override
    public SUpdateTileEntityPacket getUpdatePacket() {
        CompoundNBT nbt = new CompoundNBT();
        nbt.put("tank", fluidTank.writeToNBT(new CompoundNBT()));
        nbt.put("power", energyStorage.serializeNBT());
        return new SUpdateTileEntityPacket(worldPosition, 0, nbt);
    }

    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
        CompoundNBT nbt = pkt.getTag();
        fluidTank.readFromNBT(nbt.getCompound("tank"));
        energyStorage.deserializeNBT(nbt.getCompound("power"));
    }

    @Nonnull
    @Override
    public CompoundNBT save(CompoundNBT tag) {
        CompoundNBT inv = this.getTileStackHandler().serializeNBT();
        tag.put(NBTConstants.NBT_INVENTORY, inv);
        tag.put(NBTConstants.NBT_ENERGY, energyStorage.serializeNBT());
        tag.put(NBTConstants.NBT_FLUID, fluidTank.writeToNBT(new CompoundNBT()));
        tag.putInt(NBTConstants.NBT_ENERGY_FILLED, getEnergyFilled());
        tag.putInt(NBTConstants.NBT_FLUID_FILLED, getFluidFilled());
        tag.putBoolean(NBTConstants.NBT_IS_PROCESSING, isProcessing);
        return super.save(tag);
    } //TODO 1.17 - change "fluid" to tank

    @Override
    public void load(@Nonnull BlockState state, CompoundNBT tag) {
        CompoundNBT invTag = tag.getCompound(NBTConstants.NBT_INVENTORY);
        getTileStackHandler().deserializeNBT(invTag);
        energyStorage.deserializeNBT(tag.getCompound(NBTConstants.NBT_ENERGY));
        fluidTank.readFromNBT(tag.getCompound(NBTConstants.NBT_FLUID));
        if (tag.contains(NBTConstants.NBT_ENERGY_FILLED)) setEnergyFilled(tag.getInt(NBTConstants.NBT_ENERGY_FILLED));
        if (tag.contains(NBTConstants.NBT_FLUID_FILLED)) setFluidFilled(tag.getInt(NBTConstants.NBT_FLUID_FILLED));
        if (tag.contains(NBTConstants.NBT_IS_PROCESSING)) isProcessing = tag.getBoolean(NBTConstants.NBT_IS_PROCESSING);
        super.load(state, tag);
    }

    @Nonnull
    @Override
    public CompoundNBT getUpdateTag() {
        CompoundNBT nbtTagCompound = new CompoundNBT();
        save(nbtTagCompound);
        return nbtTagCompound;
    }

    @Override
    public void handleUpdateTag(@Nonnull BlockState state, CompoundNBT tag) {
        this.load(state, tag);
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
        return new HoneyGeneratorContainer(id, level, worldPosition, playerInventory);
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
                setChanged();
            }
        };
    }

    public void sendGUINetworkPacket(IContainerListener player) {
        if (player instanceof ServerPlayerEntity && (!(player instanceof FakePlayer))) {
            PacketBuffer buffer = new PacketBuffer(Unpooled.buffer());
            buffer.writeFluidStack(fluidTank.getFluid());
            buffer.writeInt(energyStorage.getEnergyStored());
            NetPacketHandler.sendToPlayer(new SyncGUIMessage(this.worldPosition, buffer), (ServerPlayerEntity) player);
        }
    }

    public void handleGUINetworkPacket(PacketBuffer buffer) {
        fluidTank.setFluid(buffer.readFluidStack());
        energyStorage.setEnergy(buffer.readInt());
    }

    public int getFluidLevel() {
        float fillPercentage = ((float) fluidTank.getFluidAmount()) / ((float) fluidTank.getTankCapacity(0));
        return (int) Math.ceil(fillPercentage * 100);
    }

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

    public class InternalFluidTank extends FluidTank {

        public InternalFluidTank(int capacity, Predicate<FluidStack> validator) {
            super(capacity, validator);
        }

        @Override
        protected void onContentsChanged() {
            super.onContentsChanged();
            if (level != null) {
                BlockState state = level.getBlockState(worldPosition);
                level.sendBlockUpdated(worldPosition, state, state, 2);
            }
        }
    }
}
