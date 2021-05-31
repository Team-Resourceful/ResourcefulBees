package com.teamresourceful.resourcefulbees.tileentity;

import com.teamresourceful.resourcefulbees.config.Config;
import com.teamresourceful.resourcefulbees.container.AutomationSensitiveItemStackHandler;
import com.teamresourceful.resourcefulbees.container.HoneyCongealerContainer;
import com.teamresourceful.resourcefulbees.lib.constants.NBTConstants;
import com.teamresourceful.resourcefulbees.network.NetPacketHandler;
import com.teamresourceful.resourcefulbees.network.packets.SyncGUIMessage;
import com.teamresourceful.resourcefulbees.registry.ModBlockEntityTypes;
import com.teamresourceful.resourcefulbees.utils.BeeInfoUtils;
import io.netty.buffer.Unpooled;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerListener;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.TickableBlockEntity;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class HoneyCongealerTileEntity extends AbstractHoneyTank implements TickableBlockEntity, MenuProvider {

    public static final int BOTTLE_OUTPUT = 0;
    private final AutomationSensitiveItemStackHandler tileStackHandler = new TileStackHandler(2, getAcceptor(), getRemover());
    private final LazyOptional<IItemHandler> lazyOptional = LazyOptional.of(this::getTileStackHandler);
    private boolean dirty;
    private int processingFill;

    public HoneyCongealerTileEntity() {
        super(ModBlockEntityTypes.HONEY_CONGEALER_TILE_ENTITY.get(), 16000);
    }

    public float getProcessPercent() {
        if (!canProcessFill()) return 0;
        if (processingFill == Config.HONEY_PROCESS_TIME.get() * Config.CONGEALER_TIME_MODIFIER.get()) return 1;
        return processingFill / ((float) Config.HONEY_PROCESS_TIME.get() * Config.CONGEALER_TIME_MODIFIER.get());
    }

    @Override
    @NotNull
    public Component getDisplayName() {
        return new TranslatableComponent("gui.resourcefulbees.honey_congealer");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, @NotNull Inventory inventory, @NotNull Player playerEntity) {
        if (level == null) return null;
        return new HoneyCongealerContainer(id, level, worldPosition, inventory);
    }

    public boolean canProcessFill() {
        FluidStack fluidStack = getFluidTank().getFluid();
        ItemStack outputStack = getTileStackHandler().getStackInSlot(BOTTLE_OUTPUT);
        Item outputHoney = BeeInfoUtils.getHoneyBlockFromFluid(fluidStack.getFluid());

        boolean isTankReady = !fluidStack.isEmpty() && getFluidTank().getFluidAmount() >= 1000;
        boolean hasHoneyBlock = outputHoney != Items.AIR;
        boolean canOutput = outputStack.isEmpty() || outputStack.getItem() == outputHoney && outputStack.getCount() < outputStack.getMaxStackSize();

        return isTankReady && hasHoneyBlock && canOutput;
    }

    public void processFill() {
        FluidStack fluidStack = new FluidStack(getFluidTank().getFluid(), 1000);
        ItemStack outputStack = getTileStackHandler().getStackInSlot(BOTTLE_OUTPUT);
        if (outputStack.isEmpty()) outputStack = new ItemStack(BeeInfoUtils.getHoneyBlockFromFluid(fluidStack.getFluid()));
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

    public void sendGUINetworkPacket(ContainerListener player) {
        if (player instanceof ServerPlayer && (!(player instanceof FakePlayer))) {
            FriendlyByteBuf buffer = new FriendlyByteBuf(Unpooled.buffer());
            buffer.writeFluidStack(getFluidTank().getFluid());
            NetPacketHandler.sendToPlayer(new SyncGUIMessage(this.worldPosition, buffer), (ServerPlayer) player);
        }
    }

    public void handleGUINetworkPacket(FriendlyByteBuf buffer) {
        getFluidTank().setFluid(buffer.readFluidStack());
    }

    @Override
    public void tick() {
        if (canProcessFill()) {
            if (processingFill >= Config.HONEY_PROCESS_TIME.get() * Config.CONGEALER_TIME_MODIFIER.get()) {
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
    public CompoundTag writeNBT(CompoundTag tag) {
        CompoundTag inv = this.getTileStackHandler().serializeNBT();
        tag.put(NBTConstants.NBT_INVENTORY, inv);
        return super.writeNBT(tag);
    }

    @Override
    public void readNBT(CompoundTag tag) {
        super.readNBT(tag);
        CompoundTag invTag = tag.getCompound(NBTConstants.NBT_INVENTORY);
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
