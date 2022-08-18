package com.teamresourceful.resourcefulbees.common.blockentity;

import com.teamresourceful.resourcefulbees.common.block.HoneyGenerator;
import com.teamresourceful.resourcefulbees.common.block.base.InstanceBlockEntityTicker;
import com.teamresourceful.resourcefulbees.common.blockentity.base.GUISyncedBlockEntity;
import com.teamresourceful.resourcefulbees.common.capabilities.CustomEnergyStorage;
import com.teamresourceful.resourcefulbees.common.capabilities.HoneyFluidTank;
import com.teamresourceful.resourcefulbees.common.config.CommonConfig;
import com.teamresourceful.resourcefulbees.common.inventory.menus.HoneyGeneratorMenu;
import com.teamresourceful.resourcefulbees.common.lib.constants.NBTConstants;
import com.teamresourceful.resourcefulbees.common.lib.constants.TranslationConstants;
import com.teamresourceful.resourcefulbees.common.lib.enums.ProcessStage;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.ModBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Locale;

public class HoneyGeneratorBlockEntity extends GUISyncedBlockEntity implements InstanceBlockEntityTicker {

    public static final int HONEY_DRAIN_AMOUNT = CommonConfig.HONEY_DRAIN_AMOUNT.get();
    public static final int ENERGY_FILL_AMOUNT = CommonConfig.ENERGY_FILL_AMOUNT.get();
    public static final int ENERGY_TRANSFER_AMOUNT = CommonConfig.ENERGY_TRANSFER_AMOUNT.get();
    public static final int MAX_ENERGY_CAPACITY = CommonConfig.MAX_ENERGY_CAPACITY.get();
    public static final int MAX_TANK_STORAGE = CommonConfig.MAX_TANK_STORAGE.get();

    private final CustomEnergyStorage energyStorage = new CustomEnergyStorage(MAX_ENERGY_CAPACITY, 0, ENERGY_TRANSFER_AMOUNT) {
        @Override
        protected void onEnergyChanged() {
            setChanged();
        }
    };
    private final HoneyFluidTank tank = new HoneyFluidTank(MAX_TANK_STORAGE) {
        @Override
        protected void onContentsChanged() {
            setChanged();
        }
    };

    private final LazyOptional<IEnergyStorage> energyOptional = LazyOptional.of(() -> energyStorage);
    private final LazyOptional<FluidTank> tankOptional = LazyOptional.of(() -> tank);

    private int processingTime = 0;
    private ProcessStage processStage = ProcessStage.IDLE;

    public HoneyGeneratorBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityTypes.HONEY_GENERATOR_ENTITY.get(), pos, state);
    }

    @Override
    public Side getSide() {
        return Side.BOTH;
    }

    @Override
    public void clientTick(Level level, BlockPos pos, BlockState state) {
        if (this.processStage == ProcessStage.IDLE) this.processingTime = 0;
        else {
            this.processingTime++;
            this.processingTime %= 10;
        }
    }

    @Override
    public void serverTick(Level level, BlockPos pos, BlockState state) {
        if (this.processStage.equals(ProcessStage.IDLE) && this.canProcess()) this.startProcess(level);
        if (this.processStage.equals(ProcessStage.PROCESSING)) {
            if (this.canProcess()) this.processEnergy();
            else this.processCompleted(level);
        }
        this.sendOutPower(level);
    }

    private void sendOutPower(Level level) {
        if (hasEnergy()) {
            Arrays.stream(Direction.values())
                    .map(direction -> Pair.of(level.getBlockEntity(getBlockPos().relative(direction)), direction))
                    .filter(pair -> pair.getLeft() != null && pair.getRight() != null)
                    .forEach(this::transferEnergy);
        }
    }

    private void transferEnergy(Pair<BlockEntity, Direction> tileEntityDirectionPair) {
        tileEntityDirectionPair.getLeft().getCapability(ForgeCapabilities.ENERGY, tileEntityDirectionPair.getRight().getOpposite())
                .filter(IEnergyStorage::canReceive)
                .ifPresent(this::transferEnergy);
    }

    private void transferEnergy(IEnergyStorage handler) {
        if (hasEnergy()) {
            energyStorage.consumeEnergy(handler.receiveEnergy(Math.min(getStoredEnergy(), ENERGY_TRANSFER_AMOUNT), false));
        }
    }

    private int getStoredEnergy() {
        return energyStorage.getEnergyStored();
    }

    private boolean hasEnergy() {
        return getStoredEnergy() > 0;
    }

    private boolean canProcess() {
        return hasHoney() && energyStorage.canAddEnergy(ENERGY_FILL_AMOUNT) && canDrain();
    }

    private boolean hasHoney() {
        return !tank.isEmpty();
    }

    private boolean canDrain() {
        return tank.getFluidAmount() >= HONEY_DRAIN_AMOUNT;
    }

    private void startProcess(Level level) {
        processStage = ProcessStage.PROCESSING;
        level.setBlockAndUpdate(getBlockPos(), getBlockState().setValue(HoneyGenerator.PROPERTY_ON, true));
    }

    private void processEnergy() {
        tank.drain(HONEY_DRAIN_AMOUNT, IFluidHandler.FluidAction.EXECUTE);
        energyStorage.addEnergy(ENERGY_FILL_AMOUNT);
    }

    private void processCompleted(Level level) {
        processStage = ProcessStage.IDLE;
        level.setBlockAndUpdate(getBlockPos(), getBlockState().setValue(HoneyGenerator.PROPERTY_ON, false));
    }

    @Override
    public void setChanged() {
        super.setChanged();
        sendToPlayersTrackingChunk();
    }

    @Override
    public CompoundTag getSyncData() {
        CompoundTag tag = new CompoundTag();
        tag.put(NBTConstants.NBT_ENERGY, energyStorage.serializeNBT());
        tag.put(NBTConstants.NBT_TANK, tank.writeToNBT(new CompoundTag()));
        return tag;
    }

    @Override
    public void readSyncData(@NotNull CompoundTag tag) {
        tank.readFromNBT(tag.getCompound(NBTConstants.NBT_TANK));
        energyStorage.deserializeNBT(tag.getCompound(NBTConstants.NBT_ENERGY));
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put(NBTConstants.SYNC_DATA, getSyncData());
        tag.putString(NBTConstants.NBT_PROCESS_STAGE, processStage.toString());
    }

    @Override
    public void load(@NotNull CompoundTag tag) {
        super.load(tag);
        readSyncData(tag.getCompound(NBTConstants.SYNC_DATA));
        if (tag.contains(NBTConstants.NBT_PROCESS_STAGE)) processStage = ProcessStage.valueOf(tag.getString(NBTConstants.NBT_PROCESS_STAGE).toUpperCase(Locale.ROOT));
    }

    @NotNull
    @Override
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap.equals(ForgeCapabilities.ENERGY)) return energyOptional.cast();
        if (cap.equals(ForgeCapabilities.FLUID_HANDLER)) return tankOptional.cast();
        return super.getCapability(cap, side);
    }

    @Override
    public void invalidateCaps() {
        this.energyOptional.invalidate();
        this.tankOptional.invalidate();
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, @NotNull Inventory playerInventory, @NotNull Player playerEntity) {
        return new HoneyGeneratorMenu(id, playerInventory, this);
    }

    @NotNull
    @Override
    public Component getDisplayName() {
        return TranslationConstants.Guis.GENERATOR;
    }

    public int getProcessingTime() {
        return processingTime;
    }

    public HoneyFluidTank getTank() {
        return tank;
    }

    public CustomEnergyStorage getEnergyStorage() {
        return energyStorage;
    }
}
