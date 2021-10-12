package com.teamresourceful.resourcefulbees.common.tileentity;

import com.teamresourceful.resourcefulbees.common.config.CommonConfig;
import com.teamresourceful.resourcefulbees.common.container.AutomationSensitiveItemStackHandler;
import com.teamresourceful.resourcefulbees.common.container.HoneyCongealerContainer;
import com.teamresourceful.resourcefulbees.common.lib.constants.NBTConstants;
import com.teamresourceful.resourcefulbees.common.network.NetPacketHandler;
import com.teamresourceful.resourcefulbees.common.network.packets.SyncGUIMessage;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.ModBlockEntityTypes;
import com.teamresourceful.resourcefulbees.common.utils.BeeInfoUtils;
import io.netty.buffer.Unpooled;
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
import net.minecraft.util.Direction;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class HoneyCongealerTileEntity extends AbstractHoneyTank implements ITickableTileEntity, ISyncableGUI {

    public static final int BLOCK_OUTPUT = 0;
    private final AutomationSensitiveItemStackHandler tileStackHandler = new TileStackHandler(2, getAcceptor(), getRemover());
    private final LazyOptional<IItemHandler> lazyOptional = LazyOptional.of(this::getTileStackHandler);
    private boolean dirty;
    private int processingFill;

    public HoneyCongealerTileEntity() {
        super(ModBlockEntityTypes.HONEY_CONGEALER_TILE_ENTITY.get(), 16000);
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

    public boolean canProcessHoney() {
        FluidStack fluidStack = getFluidTank().getFluid();
        ItemStack outputStack = getTileStackHandler().getStackInSlot(BLOCK_OUTPUT);
        Item outputHoney = BeeInfoUtils.getHoneyBlockFromFluid(fluidStack.getFluid());

        boolean isTankReady = !fluidStack.isEmpty() && getFluidTank().getFluidAmount() >= 1000;
        boolean hasHoneyBlock = outputHoney != Items.AIR;
        boolean canOutput = outputStack.isEmpty() || outputStack.getItem() == outputHoney && outputStack.getCount() < outputStack.getMaxStackSize();

        return isTankReady && hasHoneyBlock && canOutput;
    }

    public void processHoney() {
        FluidStack fluidStack = new FluidStack(getFluidTank().getFluid(), 1000);
        ItemStack outputStack = getTileStackHandler().getStackInSlot(BLOCK_OUTPUT);
        if (outputStack.isEmpty()) outputStack = new ItemStack(BeeInfoUtils.getHoneyBlockFromFluid(fluidStack.getFluid()));
        else outputStack.grow(1);
        getTileStackHandler().setStackInSlot(BLOCK_OUTPUT, outputStack);
        getFluidTank().drain(fluidStack, IFluidHandler.FluidAction.EXECUTE);
    }

    public @NotNull AutomationSensitiveItemStackHandler getTileStackHandler() {
        return tileStackHandler;
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
            buffer.writeFluidStack(getFluidTank().getFluid());
            NetPacketHandler.sendToPlayer(new SyncGUIMessage(this.worldPosition, buffer), (ServerPlayerEntity) player);
        }
    }

    public void handleGUINetworkPacket(PacketBuffer buffer) {
        getFluidTank().setFluid(buffer.readFluidStack());
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
        return cap.equals(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) ? lazyOptional.cast() :
                super.getCapability(cap, side);
    }

    @Override
    public CompoundNBT writeNBT(CompoundNBT tag) {
        CompoundNBT inv = this.getTileStackHandler().serializeNBT();
        tag.put(NBTConstants.NBT_INVENTORY, inv);
        return super.writeNBT(tag);
    }

    @Override
    public void readNBT(CompoundNBT tag) {
        super.readNBT(tag);
        CompoundNBT invTag = tag.getCompound(NBTConstants.NBT_INVENTORY);
        getTileStackHandler().deserializeNBT(invTag);
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
