package com.teamresourceful.resourcefulbees.common.blockentity;

import com.teamresourceful.resourcefulbees.common.recipe.recipes.centrifuge.outputs.FluidOutput;
import com.teamresourceful.resourcefulbees.common.recipe.recipes.centrifuge.outputs.ItemOutput;
import com.teamresourceful.resourcefulbees.common.blockentity.base.InventorySyncedBlockEntity;
import com.teamresourceful.resourcefulbees.common.capabilities.SelectableMultiFluidTank;
import com.teamresourceful.resourcefulbees.common.inventory.AutomationSensitiveItemStackHandler;
import com.teamresourceful.resourcefulbees.common.lib.constants.NBTConstants;
import com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.helpers.CentrifugeUtils;
import com.teamresourceful.resourcefulbees.common.recipe.recipes.centrifuge.CentrifugeRecipe;
import com.teamresourceful.resourcefulbees.common.utils.ModUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CentrifugeBlockEntity extends InventorySyncedBlockEntity {

    private final SelectableMultiFluidTank tank = new SelectableMultiFluidTank(32000, fluid -> false);
    private final LazyOptional<SelectableMultiFluidTank> tankOptional = LazyOptional.of(() -> tank);

    private CentrifugeRecipe cachedRecipe;
    private int uses = 0;
    private boolean outputFull = false;

    protected CentrifugeBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    //region NBT
    @Override
    protected void saveAdditional(@NotNull CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put(NBTConstants.SYNC_DATA, getSyncData());
    }

    @Override
    public void load(@NotNull CompoundTag tag) {
        super.load(tag);
        readSyncData(tag.getCompound(NBTConstants.SYNC_DATA));
    }

    @Override
    public CompoundTag getSyncData() {
        CompoundTag tag = new CompoundTag();
        tag.put(NBTConstants.NBT_TANK, tank.serializeNBT());
        return tag;
    }

    @Override
    public void readSyncData(@NotNull CompoundTag tag) {
        tank.deserializeNBT(tag.getCompound(NBTConstants.NBT_TANK));
    }
    //endregion

    private void updateCachedRecipe() {
        var tempRecipe = CentrifugeUtils.getRecipe(getLevel(), getInventory().getStackInSlot(0)).orElse(null);
        if (tempRecipe != cachedRecipe) {
            uses = 0;
        }
        cachedRecipe = tempRecipe;
    }

    /**
     * This method is the method used by other blocks to activate this block, this block does not start to initiate any of the processing itself.
     */
    public void use() {
        if (canProcess()) {
            if (uses >= cachedRecipe.getUses()) {
                finishRecipe();
            } else {
                uses++;
            }
        } else {
            uses = 0;
        }
    }

    private boolean canProcess() {
        return cachedRecipe != null && (cachedRecipe.itemOutputs().isEmpty() || !outputFull) && (cachedRecipe.fluidOutputs().isEmpty() || !tank.isFull());
    }

    private void finishRecipe() {
        uses = 0;
        if (cachedRecipe != null && level != null && !level.isClientSide()) {
            getInventory().getStackInSlot(0).shrink(1);
            cachedRecipe.itemOutputs()
                    .stream()
                    .filter(item -> item.chance() < level.random.nextDouble())
                    .map(item -> item.pool().next())
                    .map(ItemOutput::getItemStack)
                    .forEach(this::deliverItem);
            cachedRecipe.fluidOutputs()
                    .stream()
                    .filter(fluid -> fluid.chance() < level.random.nextDouble())
                    .map(fluid -> fluid.pool().next())
                    .map(FluidOutput::getFluidStack)
                    .forEach(this::deliverFluid);
            outputFull = getInventory().getItems().stream().noneMatch(ItemStack::isEmpty);
        }
    }

    private void deliverItem(ItemStack stack) {
        for (int i = 1; i < 13 && !stack.isEmpty(); i++) {
            stack = ModUtils.insertItem(getInventory(), i, stack);
        }
    }

    private void deliverFluid(FluidStack stack) {
        tank.forceFill(stack, IFluidHandler.FluidAction.EXECUTE, true);
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap.equals(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY)) return tankOptional.cast();
        return super.getCapability(cap, side);
    }

    @Override
    public @NotNull Component getDisplayName() {
        return Component.literal("Test");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int syncID, @NotNull Inventory inventory, @NotNull Player player) {
        return null;
    }

    @Override
    public AutomationSensitiveItemStackHandler createInventory() {
        return new TileStackHandler();
    }

    protected class TileStackHandler extends AutomationSensitiveItemStackHandler {
        protected TileStackHandler() {
            super(13, (slot, stack, automation) -> slot == 0, (slot, automation) -> !automation || slot != 0);
        }

        @Override
        protected void onContentsChanged(int slot) {
            super.onContentsChanged(slot);
            setChanged();
            if (slot == 0) {
                updateCachedRecipe();
            }
        }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            return CentrifugeUtils.getRecipe(getLevel(), stack).isPresent();
        }
    }
}
