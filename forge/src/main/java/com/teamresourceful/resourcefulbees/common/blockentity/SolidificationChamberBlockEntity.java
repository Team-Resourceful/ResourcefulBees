package com.teamresourceful.resourcefulbees.common.blockentity;

import com.teamresourceful.resourcefulbees.common.blockentities.base.GUISyncedBlockEntity;
import com.teamresourceful.resourcefulbees.common.blocks.base.InstanceBlockEntityTicker;
import com.teamresourceful.resourcefulbees.common.config.SolidficationConfig;
import com.teamresourceful.resourcefulbees.common.inventory.AutomationSensitiveItemStackHandler;
import com.teamresourceful.resourcefulbees.common.inventory.menus.SolidificationChamberMenu;
import com.teamresourceful.resourcefulbees.common.lib.constants.NBTConstants;
import com.teamresourceful.resourcefulbees.common.lib.constants.translations.GuiTranslations;
import com.teamresourceful.resourcefulbees.common.recipes.SolidificationRecipe;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.ModBlockEntityTypes;
import com.teamresourceful.resourcefulbees.common.utils.FluidUtils;
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
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SolidificationChamberBlockEntity extends GUISyncedBlockEntity implements InstanceBlockEntityTicker {

    public static final int BLOCK_OUTPUT = 0;

    private final FluidTank tank = new FluidTank(16000, fluid -> this.level != null && SolidificationRecipe.matches(level.getRecipeManager(), fluid.getFluid(), fluid.getTag())) {
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
        if (processingFill == SolidficationConfig.honeyProcessTime * SolidficationConfig.solidficationTimeMultiplier) return 1;
        return processingFill / ((float) SolidficationConfig.honeyProcessTime * SolidficationConfig.solidficationTimeMultiplier);
    }

    @Override
    @NotNull
    public Component getDisplayName() {
        return GuiTranslations.SOLIDIFICATION_CHAMBER;
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
        FluidStack stack = tank.getFluid();
        if (stack.isEmpty()) {
            cachedRecipe = null;
            lastFluid = null;
            return false;
        }
        ItemStack outputStack = getInventory().getStackInSlot(BLOCK_OUTPUT);
        final SolidificationRecipe recipe = stack.getFluid().equals(lastFluid) && cachedRecipe != null ?
                cachedRecipe : SolidificationRecipe.findRecipe(level.getRecipeManager(), stack.getFluid(), stack.getTag()).orElse(null);
        if (recipe == null) return false;

        boolean isTankReady = !stack.isEmpty() && tank.getFluidAmount() >= recipe.fluid().amount();
        boolean canOutput = outputStack.isEmpty() || ItemStack.isSameItemSameTags(recipe.stack(), outputStack) && outputStack.getCount() < outputStack.getMaxStackSize();

        cachedRecipe = recipe;
        lastFluid = stack.getFluid();
        return isTankReady && canOutput;
    }

    public void processHoney() {
        ItemStack outputStack = getInventory().getStackInSlot(BLOCK_OUTPUT);
        if (outputStack.isEmpty()) outputStack = cachedRecipe.stack().copy();
        else outputStack.grow(1);
        getInventory().setStackInSlot(BLOCK_OUTPUT, outputStack);
        tank.drain(FluidUtils.fromRecipe(cachedRecipe.fluid()), IFluidHandler.FluidAction.EXECUTE);
    }

    public @NotNull AutomationSensitiveItemStackHandler getInventory() {
        return inventory;
    }

    @Override
    public Side getSide() {
        return Side.SERVER;
    }

    @Override
    public void serverTick(Level level, BlockPos pos, BlockState state) {
        if (this.canProcessHoney()) {
            if (this.processingFill >= SolidficationConfig.honeyProcessTime * SolidficationConfig.solidficationTimeMultiplier) {
                this.processHoney();
                this.processingFill = 0;
            }
            this.processingFill++;
        }

        if (this.dirty) {
            this.dirty = false;
            this.setChanged();
        }
    }

    @NotNull
    @Override
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap.equals(ForgeCapabilities.ITEM_HANDLER)) return inventoryOptional.cast();
        if (cap.equals(ForgeCapabilities.FLUID_HANDLER)) return tankOptional.cast();
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
