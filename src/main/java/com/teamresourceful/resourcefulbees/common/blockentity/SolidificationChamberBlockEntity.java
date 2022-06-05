package com.teamresourceful.resourcefulbees.common.blockentity;

import com.teamresourceful.resourcefulbees.common.blockentity.base.GUISyncedBlockEntity;
import com.teamresourceful.resourcefulbees.common.config.CommonConfig;
import com.teamresourceful.resourcefulbees.common.inventory.AutomationSensitiveItemStackHandler;
import com.teamresourceful.resourcefulbees.common.inventory.menus.SolidificationChamberMenu;
import com.teamresourceful.resourcefulbees.common.lib.constants.NBTConstants;
import com.teamresourceful.resourcefulbees.common.lib.constants.TranslationConstants;
import com.teamresourceful.resourcefulbees.common.recipe.recipes.SolidificationRecipe;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.ModBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SolidificationChamberBlockEntity extends GUISyncedBlockEntity {

    public static final int BLOCK_OUTPUT = 0;

    private final FluidTank tank = new FluidTank(16000, fluid -> this.level != null && SolidificationRecipe.matches(level.getRecipeManager(), fluid)) {
        @Override
        protected void onContentsChanged() {
            sendToPlayersTrackingChunk();
        }
    };
    private final LazyOptional<FluidTank> tankOptional = LazyOptional.of(() -> tank);

    private final AutomationSensitiveItemStackHandler inventory = new TileStackHandler();
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
        return new SolidificationChamberMenu(id, playerInventory, this);
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

        boolean isTankReady = !fluidStack.isEmpty() && tank.getFluidAmount() >= recipe.fluid().getAmount();
        boolean canOutput = outputStack.isEmpty() || ItemStack.isSameItemSameTags(recipe.stack(), outputStack) && outputStack.getCount() < outputStack.getMaxStackSize();

        cachedRecipe = recipe;
        lastFluid = fluidStack.getFluid();
        return isTankReady && canOutput;
    }

    public void processHoney() {
        ItemStack outputStack = getInventory().getStackInSlot(BLOCK_OUTPUT);
        if (outputStack.isEmpty()) outputStack = cachedRecipe.stack().copy();
        else outputStack.grow(1);
        getInventory().setStackInSlot(BLOCK_OUTPUT, outputStack);
        tank.drain(cachedRecipe.fluid().copy(), IFluidHandler.FluidAction.EXECUTE);
    }

    public @NotNull AutomationSensitiveItemStackHandler getInventory() {
        return inventory;
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
    protected void saveAdditional(@NotNull CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put(NBTConstants.SYNC_DATA, getSyncData());
        tag.put(NBTConstants.NBT_INVENTORY, inventory.serializeNBT());
    }

    @Override
    public void load(@NotNull CompoundTag tag) {
        super.load(tag);
        inventory.deserializeNBT(tag.getCompound(NBTConstants.NBT_INVENTORY));
        readSyncData(tag.getCompound(NBTConstants.SYNC_DATA));
    }

    @Override
    public CompoundTag getSyncData() {
        CompoundTag tag = new CompoundTag();
        tag.put(NBTConstants.NBT_TANK, tank.writeToNBT(new CompoundTag()));
        return tag;
    }

    @Override
    public void readSyncData(@NotNull CompoundTag tag) {
        tank.readFromNBT(tag.getCompound(NBTConstants.NBT_TANK));
    }

    protected class TileStackHandler extends AutomationSensitiveItemStackHandler {
        protected TileStackHandler() {
            super(2, (slot, stack, automation) -> false, (slot, automation) -> !automation || slot == BLOCK_OUTPUT);
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
