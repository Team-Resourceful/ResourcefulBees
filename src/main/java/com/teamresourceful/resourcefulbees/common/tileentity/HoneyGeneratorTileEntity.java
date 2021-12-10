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
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerListener;
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

    public static void clientTick(Level level, BlockPos pos, BlockState state, HoneyGeneratorTileEntity entity) {
        if (entity.processStage == ProcessStage.IDLE) entity.processingTime = 0;
        else {
            entity.processingTime++;
            entity.processingTime %= 10;
        }
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, HoneyGeneratorTileEntity entity) {
        if ((entity.processStage == ProcessStage.COMPLETED || entity.processStage == ProcessStage.IDLE)) {
            changeState(!entity.tank.isEmpty() && canProcessEnergy(entity) ? ProcessStage.PROCESSING : ProcessStage.IDLE, level, pos, state, entity);
        }

        if (entity.processStage.equals(ProcessStage.PROCESSING)) {
            entity.processEnergy(level, pos, state, entity);
        }
        sendOutPower(level, pos, entity);
    }

    private static void changeState(ProcessStage stage, Level level, BlockPos pos, BlockState state, HoneyGeneratorTileEntity entity) {
        if (stage == ProcessStage.IDLE && entity.processStage != stage) {
            level.setBlockAndUpdate(pos, state.setValue(HoneyGenerator.PROPERTY_ON, false));
        }
        if (stage != ProcessStage.IDLE && entity.processStage == ProcessStage.IDLE) {
            level.setBlockAndUpdate(pos, state.setValue(HoneyGenerator.PROPERTY_ON, true));
        }
        entity.processStage = stage;
    }

    private static void sendOutPower(Level level, BlockPos pos, HoneyGeneratorTileEntity entity) {
        if (entity.getStoredEnergy() > 0) {
            Arrays.stream(Direction.values())
                    .map(direction -> Pair.of(level.getBlockEntity(pos.relative(direction)), direction))
                    .filter(pair -> pair.getLeft() != null && pair.getRight() != null)
                    .forEach((pair) -> transferEnergy(pair, entity));
        }
    }

    private static void transferEnergy(Pair<BlockEntity, Direction> tileEntityDirectionPair, HoneyGeneratorTileEntity entity) {
        tileEntityDirectionPair.getLeft().getCapability(CapabilityEnergy.ENERGY, tileEntityDirectionPair.getRight().getOpposite())
                .filter(IEnergyStorage::canReceive)
                .ifPresent((handler) -> transferEnergy(handler, entity));
    }

    private static void transferEnergy(IEnergyStorage handler, HoneyGeneratorTileEntity entity) {
        if (entity.getStoredEnergy() > 0) {
            entity.energyStorage.consumeEnergy(handler.receiveEnergy(Math.min(entity.getStoredEnergy(), ENERGY_TRANSFER_AMOUNT), false));
        }
    }

    private int getStoredEnergy() {
        return energyStorage.getEnergyStored();
    }

    public static boolean canProcessEnergy(HoneyGeneratorTileEntity entity) {
        return entity.getStoredEnergy() + ENERGY_FILL_AMOUNT <= entity.energyStorage.getMaxEnergyStored() && entity.tank.getFluidAmount() >= HONEY_DRAIN_AMOUNT;
    }

    private void processEnergy(Level level, BlockPos pos, BlockState state, HoneyGeneratorTileEntity entity) {
        tank.drain(HONEY_DRAIN_AMOUNT, IFluidHandler.FluidAction.EXECUTE);
        energyStorage.addEnergy(ENERGY_FILL_AMOUNT);
        changeState(ProcessStage.COMPLETED, level, pos, state, entity);
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
    public void invalidateCaps() {
        this.energyOptional.invalidate();
        this.tankOptional.invalidate();
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
        return TranslationConstants.Guis.GENERATOR;
    }

    public void sendGUINetworkPacket(ContainerListener player) {
        if (player instanceof ServerPlayer serverPlayer && (!(player instanceof FakePlayer))) {
            FriendlyByteBuf buffer = new FriendlyByteBuf(Unpooled.buffer());
            buffer.writeFluidStack(tank.getFluid());
            buffer.writeInt(energyStorage.getEnergyStored());
            NetPacketHandler.sendToPlayer(new SyncGUIMessage(this.worldPosition, buffer), serverPlayer);
        }
    }

    public void handleGUINetworkPacket(FriendlyByteBuf buffer) {
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
