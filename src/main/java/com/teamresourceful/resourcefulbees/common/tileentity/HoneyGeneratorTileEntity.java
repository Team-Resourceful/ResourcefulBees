package com.teamresourceful.resourcefulbees.common.tileentity;

import com.teamresourceful.resourcefulbees.common.block.HoneyGenerator;
import com.teamresourceful.resourcefulbees.common.capabilities.CustomEnergyStorage;
import com.teamresourceful.resourcefulbees.common.capabilities.HoneyFluidTank;
import com.teamresourceful.resourcefulbees.common.config.CommonConfig;
import com.teamresourceful.resourcefulbees.common.inventory.containers.HoneyGeneratorContainer;
import com.teamresourceful.resourcefulbees.common.lib.constants.NBTConstants;
import com.teamresourceful.resourcefulbees.common.lib.constants.TranslationConstants;
import com.teamresourceful.resourcefulbees.common.lib.enums.ProcessStage;
import com.teamresourceful.resourcefulbees.common.network.NetPacketHandler;
import com.teamresourceful.resourcefulbees.common.network.packets.SyncGUIMessage;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.ModBlockEntityTypes;
import io.netty.buffer.Unpooled;
import net.minecraft.block.BlockState;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.IContainerListener;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Locale;

public class HoneyGeneratorTileEntity extends BlockEntity implements ISyncableGUI {

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
    private final HoneyFluidTank tank = new HoneyFluidTank(MAX_TANK_STORAGE);

    private final LazyOptional<IEnergyStorage> energyOptional = LazyOptional.of(() -> energyStorage);
    private final LazyOptional<FluidTank> tankOptional = LazyOptional.of(() -> tank);

    private int processingTime = 0;
    private ProcessStage processStage = ProcessStage.IDLE;

    public HoneyGeneratorTileEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityTypes.HONEY_GENERATOR_ENTITY.get(), pos, state);
    }

/*    @Override
    public void tick() {
        if (level == null) return;
        if (level.isClientSide) {
            if (processStage == ProcessStage.IDLE) processingTime = 0;
            else {
                processingTime++;
                processingTime %= 10;
            }
        }else {
            if ((processStage == ProcessStage.COMPLETED || processStage == ProcessStage.IDLE)) {
                changeState(!tank.isEmpty() && canProcessEnergy() ? ProcessStage.PROCESSING : ProcessStage.IDLE, level);
            }

            if (processStage.equals(ProcessStage.PROCESSING)) {
                this.processEnergy(level);
            }
        }
        sendOutPower(level);
    }*/

    private void changeState(ProcessStage stage, Level level) {
        if (stage == ProcessStage.IDLE && processStage != stage) {
            level.setBlockAndUpdate(worldPosition, getBlockState().setValue(HoneyGenerator.PROPERTY_ON, false));
        }
        if (stage != ProcessStage.IDLE && processStage == ProcessStage.IDLE) {
            level.setBlockAndUpdate(worldPosition, getBlockState().setValue(HoneyGenerator.PROPERTY_ON, true));
        }
        processStage = stage;
    }

    private void sendOutPower(Level level) {
        if (getStoredEnergy() > 0) {
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
            energyStorage.consumeEnergy(handler.receiveEnergy(Math.min(getStoredEnergy(), ENERGY_TRANSFER_AMOUNT), false));
        }
    }

    private int getStoredEnergy() {
        return energyStorage.getEnergyStored();
    }

    public boolean canProcessEnergy() {
        return getStoredEnergy() + ENERGY_FILL_AMOUNT <= energyStorage.getMaxEnergyStored() && tank.getFluidAmount() >= HONEY_DRAIN_AMOUNT;
    }

    private void processEnergy(Level level) {
        tank.drain(HONEY_DRAIN_AMOUNT, IFluidHandler.FluidAction.EXECUTE);
        energyStorage.addEnergy(ENERGY_FILL_AMOUNT);
        changeState(ProcessStage.COMPLETED, level);
    }



    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        CompoundTag nbt = new CompoundTag();
        nbt.put(NBTConstants.NBT_TANK, tank.writeToNBT(new CompoundTag()));
        nbt.put(NBTConstants.NBT_ENERGY, energyStorage.serializeNBT());
        nbt.putString(NBTConstants.NBT_PROCESS_STAGE, processStage.toString());
        return ClientboundBlockEntityDataPacket.create(this, blockEntity -> nbt);
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
        CompoundTag nbt = pkt.getTag();
        if (nbt != null) {
            tank.readFromNBT(nbt.getCompound(NBTConstants.NBT_TANK));
            energyStorage.deserializeNBT(nbt.getCompound(NBTConstants.NBT_ENERGY));
            processStage = ProcessStage.valueOf(nbt.getString(NBTConstants.NBT_PROCESS_STAGE).toUpperCase(Locale.ROOT));
        }
    }

    @NotNull
    @Override
    public CompoundTag save(@NotNull CompoundTag tag) {
        tag.put(NBTConstants.NBT_ENERGY, energyStorage.serializeNBT());
        tag.put(NBTConstants.NBT_TANK, tank.writeToNBT(new CompoundTag()));
        tag.putString(NBTConstants.NBT_PROCESS_STAGE, processStage.toString());
        return super.save(tag);
    }

    @NotNull
    @Override
    public CompoundTag getUpdateTag() {
        return save(new CompoundTag());
    }

    //TODO change this to follow new nbt system
/*    @Override
    public void load(@NotNull BlockState state, @NotNull CompoundTag tag) {
        energyStorage.deserializeNBT(tag.getCompound(NBTConstants.NBT_ENERGY));
        tank.readFromNBT(tag.getCompound(NBTConstants.NBT_TANK));
        if (tag.contains(NBTConstants.NBT_PROCESS_STAGE)) processStage = ProcessStage.valueOf(tag.getString(NBTConstants.NBT_PROCESS_STAGE).toUpperCase(Locale.ROOT));
        super.load(state, tag);
    }*/

    @Override
    public void handleUpdateTag(CompoundTag tag) {
        this.load(tag);
    }

    @NotNull
    @Override
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap.equals(CapabilityEnergy.ENERGY)) return energyOptional.cast();
        if (cap.equals(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY)) return tankOptional.cast();
        return super.getCapability(cap, side);
    }

    @Override
    protected void invalidateCaps() {
        this.energyOptional.invalidate();
        this.tankOptional.invalidate();
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
        return TranslationConstants.Guis.GENERATOR;
    }

    public void sendGUINetworkPacket(IContainerListener player) {
        if (player instanceof ServerPlayerEntity && (!(player instanceof FakePlayer))) {
            PacketBuffer buffer = new PacketBuffer(Unpooled.buffer());
            buffer.writeFluidStack(tank.getFluid());
            buffer.writeInt(energyStorage.getEnergyStored());
            NetPacketHandler.sendToPlayer(new SyncGUIMessage(this.worldPosition, buffer), (ServerPlayerEntity) player);
        }
    }

    public void handleGUINetworkPacket(PacketBuffer buffer) {
        tank.setFluid(buffer.readFluidStack());
        energyStorage.setEnergy(buffer.readInt());
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
