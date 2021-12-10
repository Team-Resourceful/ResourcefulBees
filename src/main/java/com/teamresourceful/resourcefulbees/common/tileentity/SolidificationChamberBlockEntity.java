package com.teamresourceful.resourcefulbees.common.tileentity;

import com.teamresourceful.resourcefulbees.common.config.CommonConfig;
import com.teamresourceful.resourcefulbees.common.inventory.AutomationSensitiveItemStackHandler;
import com.teamresourceful.resourcefulbees.common.inventory.containers.HoneyCongealerContainer;
import com.teamresourceful.resourcefulbees.common.lib.constants.NBTConstants;
import com.teamresourceful.resourcefulbees.common.lib.constants.TranslationConstants;
import com.teamresourceful.resourcefulbees.common.network.NetPacketHandler;
import com.teamresourceful.resourcefulbees.common.network.packets.SyncGUIMessage;
import com.teamresourceful.resourcefulbees.common.recipe.SolidificationRecipe;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.ModBlockEntityTypes;
import io.netty.buffer.Unpooled;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerListener;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
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

public class SolidificationChamberBlockEntity extends BlockEntity implements ISyncableGUI {

    public static final int BLOCK_OUTPUT = 0;

    private final FluidTank tank = new FluidTank(16000, fluid -> this.level != null && SolidificationRecipe.matches(level.getRecipeManager(), fluid));
    private final LazyOptional<FluidTank> tankOptional = LazyOptional.of(() -> tank);

    private final AutomationSensitiveItemStackHandler inventory = new TileStackHandler(2, getAcceptor(), getRemover());
    private final LazyOptional<IItemHandler> inventoryOptional = LazyOptional.of(this::getInventory);

    private boolean dirty;
    private int processingFill;

    private SolidificationRecipe cachedRecipe;
    private Fluid lastFluid;

    public SolidificationChamberBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityTypes.SOLIDIFICATION_CHAMBER_TILE_ENTITY.get(), pos, state);
    }

    public float getProcessPercent() {
        if (!canProcessHoney()) return 0;
        if (processingFill == CommonConfig.HONEY_PROCESS_TIME.get() * CommonConfig.CONGEALER_TIME_MODIFIER.get()) return 1;
        return processingFill / ((float) CommonConfig.HONEY_PROCESS_TIME.get() * CommonConfig.CONGEALER_TIME_MODIFIER.get());
    }

    @Override
    @NotNull
    public Component getDisplayName() {
        return TranslationConstants.Guis.SOLIDIFICATION_CHAMBER;
    }



    @Override
    public AbstractContainerMenu createMenu(int id, @NotNull Inventory playerInventory, @NotNull Player playerEntity) {
        if (level == null) return null;
        return new HoneyCongealerContainer(id, level, worldPosition, playerInventory);
    }

    public FluidTank getTank() {
        return tank;
    }

    public boolean canProcessHoney() {
        if (level == null) return false;
        FluidStack fluidStack = tank.getFluid();
        if (fluidStack.isEmpty()) {
            cachedRecipe = null;
            lastFluid = null;
            return false;
        }
        ItemStack outputStack = getInventory().getStackInSlot(BLOCK_OUTPUT);
        final SolidificationRecipe recipe = fluidStack.getFluid().equals(lastFluid) && cachedRecipe != null ?
                cachedRecipe : SolidificationRecipe.findRecipe(level.getRecipeManager(), fluidStack).orElse(null);
        if (recipe == null) return false;

        boolean isTankReady = !fluidStack.isEmpty() && tank.getFluidAmount() >= recipe.getFluid().getAmount();
        boolean canOutput = outputStack.isEmpty() || ItemStack.isSameItemSameTags(recipe.getStack(), outputStack) && outputStack.getCount() < outputStack.getMaxStackSize();

        cachedRecipe = recipe;
        lastFluid = fluidStack.getFluid();
        return isTankReady && canOutput;
    }

    public void processHoney() {
        ItemStack outputStack = getInventory().getStackInSlot(BLOCK_OUTPUT);
        if (outputStack.isEmpty()) outputStack = cachedRecipe.getStack().copy();
        else outputStack.grow(1);
        getInventory().setStackInSlot(BLOCK_OUTPUT, outputStack);
        tank.drain(cachedRecipe.getFluid().copy(), IFluidHandler.FluidAction.EXECUTE);
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

    public void sendGUINetworkPacket(ContainerListener player) {
        if (player instanceof ServerPlayer serverPlayer && (!(player instanceof FakePlayer))) {
            FriendlyByteBuf buffer = new FriendlyByteBuf(Unpooled.buffer());
            buffer.writeFluidStack(tank.getFluid());
            NetPacketHandler.sendToPlayer(new SyncGUIMessage(this.worldPosition, buffer), serverPlayer);
        }
    }

    public void handleGUINetworkPacket(FriendlyByteBuf buffer) {
        tank.setFluid(buffer.readFluidStack());
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, SolidificationChamberBlockEntity entity) {
        if (entity.canProcessHoney()) {
            if (entity.processingFill >= CommonConfig.HONEY_PROCESS_TIME.get() * CommonConfig.CONGEALER_TIME_MODIFIER.get()) {
                entity.processHoney();
                entity.processingFill = 0;
            }
            entity.processingFill++;
        }

        if (entity.dirty) {
            entity.dirty = false;
            entity.setChanged();
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
    public @NotNull CompoundTag save(@NotNull CompoundTag nbt) {
        nbt.put(NBTConstants.NBT_INVENTORY, inventory.serializeNBT());
        nbt.put(NBTConstants.NBT_TANK, tank.writeToNBT(new CompoundTag()));
        return super.save(nbt);
    }

    //TODO convert to new way of handling nbt
/*    @Override
    public void load(@NotNull BlockState state, @NotNull CompoundTag nbt) {
        super.load(state, nbt);
        inventory.deserializeNBT(nbt.getCompound(NBTConstants.NBT_INVENTORY));
        tank.readFromNBT(nbt.getCompound(NBTConstants.NBT_TANK));
    }*/

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
