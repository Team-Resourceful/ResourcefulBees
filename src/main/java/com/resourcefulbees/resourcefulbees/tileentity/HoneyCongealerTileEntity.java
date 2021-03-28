package com.resourcefulbees.resourcefulbees.tileentity;

import com.resourcefulbees.resourcefulbees.config.Config;
import com.resourcefulbees.resourcefulbees.container.AutomationSensitiveItemStackHandler;
import com.resourcefulbees.resourcefulbees.container.HoneyCongealerContainer;
import com.resourcefulbees.resourcefulbees.lib.ModConstants;
import com.resourcefulbees.resourcefulbees.lib.NBTConstants;
import com.resourcefulbees.resourcefulbees.network.NetPacketHandler;
import com.resourcefulbees.resourcefulbees.network.packets.SyncGUIMessage;
import com.resourcefulbees.resourcefulbees.registry.ModTileEntityTypes;
import com.resourcefulbees.resourcefulbees.utils.BeeInfoUtils;
import io.netty.buffer.Unpooled;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.IContainerListener;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tags.ITag;
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

public class HoneyCongealerTileEntity extends AbstractHoneyTank implements ITickableTileEntity, INamedContainerProvider {

    public static final int BOTTLE_OUTPUT = 0;
    private final AutomationSensitiveItemStackHandler tileStackHandler = new TileStackHandler(2, getAcceptor(), getRemover());
    private final LazyOptional<IItemHandler> lazyOptional = LazyOptional.of(this::getTileStackHandler);
    private boolean dirty;
    private int processingFill;

    public HoneyCongealerTileEntity() {
        super(ModTileEntityTypes.HONEY_CONGEALER_TILE_ENTITY.get(), 16000);
    }

    public float getProcessPercent() {
        if (!canProcessFill()) return 0;
        if (processingFill == Config.HONEY_PROCEESS_TIME.get() * Config.CONGEALER_TIME_MODIFIER.get()) return 1;
        return processingFill / ((float) Config.HONEY_PROCEESS_TIME.get() * Config.CONGEALER_TIME_MODIFIER.get());
    }

    @Override
    @NotNull
    public ITextComponent getDisplayName() {
        return new TranslationTextComponent("gui.resourcefulbees.honey_congealer");
    }

    @Nullable
    @Override
    public Container createMenu(int id, @NotNull PlayerInventory inventory, @NotNull PlayerEntity playerEntity) {
        if (level == null) return null;
        return new HoneyCongealerContainer(id, level, worldPosition, inventory);
    }

    public boolean canProcessFill() {
        FluidStack fluidStack = getFluidTank().getFluid();
        ItemStack outputStack = getTileStackHandler().getStackInSlot(BOTTLE_OUTPUT);
        Item outputHoney = BeeInfoUtils.getHoneyBlock(fluidStack.getFluid());

        boolean isTankReady = !fluidStack.isEmpty() && getFluidTank().getFluidAmount() >= 1000;
        boolean hasHoneyBlock = outputHoney != Items.AIR;
        boolean canOutput = outputStack.isEmpty() || outputStack.getItem() == outputHoney && outputStack.getCount() < outputStack.getMaxStackSize();

        return isTankReady && hasHoneyBlock && canOutput;
    }

    public void processFill() {
        FluidStack fluidStack = new FluidStack(getFluidTank().getFluid(), 1000);
        ItemStack outputStack = getTileStackHandler().getStackInSlot(BOTTLE_OUTPUT);
        if (outputStack.isEmpty()) outputStack = new ItemStack(BeeInfoUtils.getHoneyBlock(fluidStack.getFluid()));
        else outputStack.grow(1);
        getTileStackHandler().setStackInSlot(BOTTLE_OUTPUT, outputStack);
        getFluidTank().drain(fluidStack, IFluidHandler.FluidAction.EXECUTE);
    }

    public @NotNull AutomationSensitiveItemStackHandler getTileStackHandler() {
        return tileStackHandler;
    }

    public AutomationSensitiveItemStackHandler.IAcceptor getAcceptor() {
        return (slot, stack, automation) -> false;
    }

    public AutomationSensitiveItemStackHandler.IRemover getRemover() {
        return (slot, automation) -> !automation || slot == BOTTLE_OUTPUT;
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
        if (canProcessFill()) {
            if (processingFill >= Config.HONEY_PROCEESS_TIME.get() * Config.CONGEALER_TIME_MODIFIER.get()) {
                processFill();
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
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @org.jetbrains.annotations.Nullable Direction side) {
        return cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY ? lazyOptional.cast() :
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
