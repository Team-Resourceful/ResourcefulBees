package com.teamresourceful.resourcefulbees.common.tileentity;

import com.teamresourceful.resourcefulbees.common.capabilities.HoneyFluidTank;
import com.teamresourceful.resourcefulbees.common.network.packets.SyncGUIMessage;
import com.teamresourceful.resourcefulbees.common.config.CommonConfig;
import com.teamresourceful.resourcefulbees.common.inventory.AutomationSensitiveItemStackHandler;
import com.teamresourceful.resourcefulbees.common.inventory.containers.HoneyCongealerContainer;
import com.teamresourceful.resourcefulbees.common.lib.constants.NBTConstants;
import com.teamresourceful.resourcefulbees.common.network.NetPacketHandler;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.ModBlockEntityTypes;
import com.teamresourceful.resourcefulbees.common.utils.BeeInfoUtils;
import io.netty.buffer.Unpooled;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.IContainerListener;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class HoneyCongealerTileEntity extends TileEntity implements ITickableTileEntity, ISyncableGUI {

    public static final int BLOCK_OUTPUT = 0;

    private final HoneyFluidTank tank = new HoneyFluidTank(16000);
    private final LazyOptional<FluidTank> tankOptional = LazyOptional.of(() -> tank);

    private final AutomationSensitiveItemStackHandler inventory = new TileStackHandler(2, getAcceptor(), getRemover());
    private final LazyOptional<IItemHandler> inventoryOptional = LazyOptional.of(this::getInventory);

    private boolean dirty;
    private int processingFill;

    public HoneyCongealerTileEntity() {
        super(ModBlockEntityTypes.HONEY_CONGEALER_TILE_ENTITY.get());
    }

    public float getProcessPercent() {
        if (!canProcessHoney()) return 0;
        if (processingFill == CommonConfig.HONEY_PROCESS_TIME.get() * CommonConfig.CONGEALER_TIME_MODIFIER.get()) return 1;
        return processingFill / ((float) CommonConfig.HONEY_PROCESS_TIME.get() * CommonConfig.CONGEALER_TIME_MODIFIER.get());
    }

    @Override
    @NotNull
    public ITextComponent getDisplayName() {
        return new TranslationTextComponent("gui.resourcefulbees.honey_congealer");
    }

    @Nullable
    @Override
    public Container createMenu(int id, @NotNull PlayerInventory playerInventory, @NotNull PlayerEntity playerEntity) {
        if (level == null) return null;
        return new HoneyCongealerContainer(id, level, worldPosition, playerInventory);
    }

    public HoneyFluidTank getTank() {
        return tank;
    }

    public boolean canProcessHoney() {
        FluidStack fluidStack = tank.getFluid();
        ItemStack outputStack = getInventory().getStackInSlot(BLOCK_OUTPUT);
        Item outputHoney = BeeInfoUtils.getHoneyBlockFromFluid(fluidStack.getFluid());

        boolean isTankReady = !fluidStack.isEmpty() && tank.getFluidAmount() >= 1000;
        boolean hasHoneyBlock = outputHoney != Items.AIR;
        boolean canOutput = outputStack.isEmpty() || outputStack.getItem() == outputHoney && outputStack.getCount() < outputStack.getMaxStackSize();

        return isTankReady && hasHoneyBlock && canOutput;
    }

    public void processHoney() {
        FluidStack fluidStack = new FluidStack(tank.getFluid(), 1000);
        ItemStack outputStack = getInventory().getStackInSlot(BLOCK_OUTPUT);
        if (outputStack.isEmpty()) outputStack = new ItemStack(BeeInfoUtils.getHoneyBlockFromFluid(fluidStack.getFluid()));
        else outputStack.grow(1);
        getInventory().setStackInSlot(BLOCK_OUTPUT, outputStack);
        tank.drain(fluidStack, IFluidHandler.FluidAction.EXECUTE);
    }

    public @NotNull AutomationSensitiveItemStackHandler getInventory() {
        return inventory;
    }

    public AutomationSensitiveItemStackHandler.IAcceptor getAcceptor() {
        return (slot, stack, automation) -> false;
    }

    public AutomationSensitiveItemStackHandler.IRemover getRemover() {
        return (slot, automation) -> !automation || slot == BLOCK_OUTPUT;
    }

    public void sendGUINetworkPacket(IContainerListener player) {
        if (player instanceof ServerPlayerEntity && (!(player instanceof FakePlayer))) {
            PacketBuffer buffer = new PacketBuffer(Unpooled.buffer());
            buffer.writeFluidStack(tank.getFluid());
            NetPacketHandler.sendToPlayer(new SyncGUIMessage(this.worldPosition, buffer), (ServerPlayerEntity) player);
        }
    }

    public void handleGUINetworkPacket(PacketBuffer buffer) {
        tank.setFluid(buffer.readFluidStack());
    }

    @Override
    public void tick() {
        if (canProcessHoney()) {
            if (processingFill >= CommonConfig.HONEY_PROCESS_TIME.get() * CommonConfig.CONGEALER_TIME_MODIFIER.get()) {
                processHoney();
                processingFill = 0;
            }
            processingFill++;
        }

        if (dirty) {
            this.dirty = false;
            this.setChanged();
        }
    }

    @NotNull
    @Override
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap.equals(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)) return inventoryOptional.cast();
        if (cap.equals(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY)) return tankOptional.cast();
        return super.getCapability(cap, side);
    }

    @Override
    public @NotNull CompoundNBT save(@NotNull CompoundNBT nbt) {
        nbt.put(NBTConstants.NBT_INVENTORY, inventory.serializeNBT());
        nbt.put(NBTConstants.NBT_TANK, tank.writeToNBT(new CompoundNBT()));
        return super.save(nbt);
    }

    @Override
    public void load(@NotNull BlockState state, @NotNull CompoundNBT nbt) {
        super.load(state, nbt);
        inventory.deserializeNBT(nbt.getCompound(NBTConstants.NBT_INVENTORY));
        tank.readFromNBT(nbt.getCompound(NBTConstants.NBT_TANK));
    }

    protected class TileStackHandler extends AutomationSensitiveItemStackHandler {
        protected TileStackHandler(int slots, IAcceptor acceptor, IRemover remover) {
            super(slots, acceptor, remover);
        }

        @Override
        protected void onContentsChanged(int slot) {
            super.onContentsChanged(slot);
            setChanged();
        }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            return stack.getItem() == Items.GLASS_BOTTLE;
        }
    }
}
