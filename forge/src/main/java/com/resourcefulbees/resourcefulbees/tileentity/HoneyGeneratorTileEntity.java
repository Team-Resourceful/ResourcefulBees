package com.resourcefulbees.resourcefulbees.tileentity;

import com.resourcefulbees.resourcefulbees.block.HoneyGenerator;
import com.resourcefulbees.resourcefulbees.capabilities.CustomEnergyStorage;
import com.resourcefulbees.resourcefulbees.config.Config;
import com.resourcefulbees.resourcefulbees.container.AutomationSensitiveItemStackHandler;
import com.resourcefulbees.resourcefulbees.container.HoneyGeneratorContainer;
import com.resourcefulbees.resourcefulbees.lib.NBTConstants;
import com.resourcefulbees.resourcefulbees.network.NetPacketHandler;
import com.resourcefulbees.resourcefulbees.network.packets.SyncGUIMessage;
import com.resourcefulbees.resourcefulbees.registry.ModBlockEntityTypes;
import com.resourcefulbees.resourcefulbees.utils.BeeInfoUtils;
import io.netty.buffer.Unpooled;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.Tag;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerListener;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.TickableBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.Arrays;

public class HoneyGeneratorTileEntity extends AbstractHoneyTankContainer implements TickableBlockEntity, MenuProvider {

    public static final int HONEY_BOTTLE_INPUT = 0;
    public static final int BOTTLE_OUTPUT = 1;
    public static final int HONEY_DRAIN_AMOUNT = Config.HONEY_DRAIN_AMOUNT.get();
    public static final int ENERGY_FILL_AMOUNT = Config.ENERGY_FILL_AMOUNT.get();
    public static final int ENERGY_TRANSFER_AMOUNT = Config.ENERGY_TRANSFER_AMOUNT.get();
    public static final int MAX_ENERGY_CAPACITY = Config.MAX_ENERGY_CAPACITY.get();
    public static final int MAX_TANK_STORAGE = Config.MAX_TANK_STORAGE.get();


    private final HoneyGeneratorTileEntity.TileStackHandler tileStackHandler = new HoneyGeneratorTileEntity.TileStackHandler(5, getAcceptor(), getRemover());
    private final CustomEnergyStorage energyStorage = createEnergy();
    private final LazyOptional<IItemHandler> lazyOptional = LazyOptional.of(this::getTileStackHandler);
    private final LazyOptional<IEnergyStorage> energyOptional = LazyOptional.of(() -> energyStorage);

    public static final Tag<Fluid> HONEY_FLUID_TAG = BeeInfoUtils.getFluidTag("forge:honey");
    public static final Tag<Item> HONEY_BOTTLE_TAG = BeeInfoUtils.getItemTag("forge:honey_bottle");

    private int fluidFilled;
    private int energyFilled;
    private boolean isProcessing;

    public HoneyGeneratorTileEntity() {
        super(ModBlockEntityTypes.HONEY_GENERATOR_ENTITY.get(), MAX_TANK_STORAGE);
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
        if (getStoredEnergy() > 0 && level != null) {
            Arrays.stream(Direction.values())
                    .map(direction -> Pair.of(level.getBlockEntity(worldPosition.relative(direction)), direction))
                    .filter(pair -> pair.getLeft() != null && pair.getRight() != null)
                    .forEach(this::transferEnergy);
        }
    }

    private void transferEnergy(Pair<BlockEntity, Direction> tileEntityDirectionPair) {
        tileEntityDirectionPair.getLeft().getCapability(CapabilityEnergy.ENERGY, tileEntityDirectionPair.getRight().getOpposite())
                .filter(IEnergyStorage::canReceive)
                .ifPresent(this::transferEnergy);
    }

    private void transferEnergy(IEnergyStorage handler) {
        if (getStoredEnergy() > 0) {
            int received = handler.receiveEnergy(Math.min(getStoredEnergy(), ENERGY_TRANSFER_AMOUNT), false);
            energyStorage.consumeEnergy(received);
        }
    }

    private int getStoredEnergy() {
        return energyStorage.getEnergyStored();
    }

    public boolean canProcessEnergy() {
        return getStoredEnergy() + ENERGY_FILL_AMOUNT <= energyStorage.getMaxEnergyStored() && getFluidTank().getFluidAmount() >= HONEY_DRAIN_AMOUNT;
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
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        CompoundTag nbt = new CompoundTag();
        nbt.put("tank", getFluidTank().writeToNBT(new CompoundTag()));
        nbt.put("power", energyStorage.serializeNBT());
        return new ClientboundBlockEntityDataPacket(worldPosition, 0, nbt);
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
        CompoundTag nbt = pkt.getTag();
        getFluidTank().readFromNBT(nbt.getCompound("tank"));
        energyStorage.deserializeNBT(nbt.getCompound("power"));
    }

    @NotNull
    @Override
    public CompoundTag save(@NotNull CompoundTag tag) {
        CompoundTag inv = this.getTileStackHandler().serializeNBT();
        tag.put(NBTConstants.NBT_INVENTORY, inv);
        tag.put(NBTConstants.NBT_ENERGY, energyStorage.serializeNBT());
        tag.put(NBTConstants.NBT_FLUID, getFluidTank().writeToNBT(new CompoundTag()));
        tag.putInt(NBTConstants.NBT_ENERGY_FILLED, getEnergyFilled());
        tag.putInt(NBTConstants.NBT_FLUID_FILLED, getFluidFilled());
        tag.putBoolean(NBTConstants.NBT_IS_PROCESSING, isProcessing);
        return super.save(tag);
    } //TODO 1.17 - change "fluid" to tank

    @NotNull
    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag nbtTagCompound = new CompoundTag();
        save(nbtTagCompound);
        return nbtTagCompound;
    }

    @Override
    public void load(@NotNull BlockState state, @NotNull CompoundTag tag) {
        CompoundTag invTag = tag.getCompound(NBTConstants.NBT_INVENTORY);
        getTileStackHandler().deserializeNBT(invTag);
        energyStorage.deserializeNBT(tag.getCompound(NBTConstants.NBT_ENERGY));
        getFluidTank().readFromNBT(tag.getCompound(NBTConstants.NBT_FLUID));
        if (tag.contains(NBTConstants.NBT_ENERGY_FILLED)) setEnergyFilled(tag.getInt(NBTConstants.NBT_ENERGY_FILLED));
        if (tag.contains(NBTConstants.NBT_FLUID_FILLED)) setFluidFilled(tag.getInt(NBTConstants.NBT_FLUID_FILLED));
        if (tag.contains(NBTConstants.NBT_IS_PROCESSING)) isProcessing = tag.getBoolean(NBTConstants.NBT_IS_PROCESSING);
        super.load(state, tag);
    }

    @Override
    public void handleUpdateTag(@NotNull BlockState state, CompoundTag tag) {
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
    public AbstractContainerMenu createMenu(int id, @NotNull Inventory playerInventory, @NotNull Player playerEntity) {
        //noinspection ConstantConditions
        return new HoneyGeneratorContainer(id, level, worldPosition, playerInventory);
    }

    @NotNull
    @Override
    public Component getDisplayName() {
        return new TranslatableComponent("gui.resourcefulbees.honey_generator");
    }

    private CustomEnergyStorage createEnergy() {
        return new CustomEnergyStorage(MAX_ENERGY_CAPACITY, 0, ENERGY_TRANSFER_AMOUNT) {
            @Override
            protected void onEnergyChanged() {
                setChanged();
            }
        };
    }

    public void sendGUINetworkPacket(ContainerListener player) {
        if (player instanceof ServerPlayer && (!(player instanceof FakePlayer))) {
            FriendlyByteBuf buffer = new FriendlyByteBuf(Unpooled.buffer());
            buffer.writeFluidStack(getFluidTank().getFluid());
            buffer.writeInt(energyStorage.getEnergyStored());
            NetPacketHandler.sendToPlayer(new SyncGUIMessage(this.worldPosition, buffer), (ServerPlayer) player);
        }
    }

    public void handleGUINetworkPacket(FriendlyByteBuf buffer) {
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

    public CustomEnergyStorage getEnergyStorage() {
        return energyStorage;
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
