package com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.entities;

import com.teamresourceful.resourcefulbees.common.recipe.recipes.centrifuge.outputs.ItemOutput;
import com.teamresourceful.resourcefulbees.common.lib.constants.NBTConstants;
import com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.containers.CentrifugeItemOutputContainer;
import com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.entities.base.AbstractGUICentrifugeEntity;
import com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.entities.base.ICentrifugeOutput;
import com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.helpers.CentrifugeTier;
import com.teamresourceful.resourcefulbees.common.recipe.recipes.centrifuge.CentrifugeRecipe;
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
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CentrifugeItemOutputEntity extends AbstractGUICentrifugeEntity implements ICentrifugeOutput<ItemOutput> {

    private final InventoryHandler inventoryHandler;
    private final LazyOptional<IItemHandler> lazyOptional;
    private boolean voidExcess = true;

    public CentrifugeItemOutputEntity(RegistryObject<BlockEntityType<CentrifugeItemOutputEntity>> tileType, CentrifugeTier tier, BlockPos pos, BlockState state) {
        super(tileType.get(), tier, pos, state);
        this.inventoryHandler = new InventoryHandler(tier.getSlots());
        this.lazyOptional = LazyOptional.of(() -> inventoryHandler);
    }

    @Override
    protected Component getDefaultName() {
        return Component.translatable("gui.centrifuge.output.item." + tier.getName());
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, @NotNull Inventory playerInventory, @NotNull Player playerEntity) {
        return new CentrifugeItemOutputContainer(id, playerInventory, this, centrifugeState);
    }

    public void setVoidExcess(boolean voidExcess) {
        this.voidExcess = voidExcess;
    }

    public boolean depositResult(CentrifugeRecipe.Output<ItemOutput> recipeOutput, int processQuantity) {
        ItemStack result = recipeOutput.pool().next().getItemStack();
        result.setCount(result.getCount() * processQuantity);
        return inventoryHandler.depositResult(result);
    }

    public InventoryHandler getInventoryHandler() {
        return inventoryHandler;
    }

    //region NBT HANDLING
    @Override
    protected void readNBT(@NotNull CompoundTag tag) {
        inventoryHandler.deserializeNBT(tag.getCompound(NBTConstants.NBT_INVENTORY));
        super.readNBT(tag);
    }

    @NotNull
    @Override
    protected CompoundTag writeNBT() {
        CompoundTag tag = super.writeNBT();
        tag.put(NBTConstants.NBT_INVENTORY, inventoryHandler.serializeNBT());
        return tag;
    }
    //endregion

    //region CAPABILITIES
    @NotNull
    @Override
    public <T> LazyOptional<T> capability(@NotNull Capability<T> cap, @Nullable Direction side) {
        return cap.equals(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) ? lazyOptional.cast() : super.capability(cap, side);
    }
    //endregion

    private class InventoryHandler extends ItemStackHandler {
        protected InventoryHandler(int slots) {
            super(slots);
        }

        @Override
        protected void onContentsChanged(int slot) {
            super.onContentsChanged(slot);
            setChanged();
        }

        @NotNull
        @Override
        public ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
            return ItemStack.EMPTY;
        }

        @Override
        public int getStackLimit(int slot, @NotNull ItemStack stack) {
            return super.getStackLimit(slot, stack);
        }

        private boolean depositResult(ItemStack result) {
            if (result.isEmpty() || controller().dumpsContainItem(result)) return true;

            boolean canDeposit = voidExcess || simulateDeposit(result);


            int slotIndex = 0;
            while (canDeposit && !result.isEmpty() && slotIndex < stacks.size()) {
                depositResult(slotIndex, result);
                slotIndex++;
            }

            return canDeposit;
        }

        private void depositResult(int slotIndex, ItemStack result) {
            ItemStack slotStack = getStackInSlot(slotIndex);
            int itemMaxStackSize = getStackLimit(slotIndex, result);

            if (slotStack.isEmpty()) {
                setStackInSlot(slotIndex, result.split(itemMaxStackSize));
            } else if (ItemStack.isSameItemSameTags(result, slotStack) && slotStack.getCount() != itemMaxStackSize) {
                int combinedCount = result.getCount() + slotStack.getCount();
                if (combinedCount <= itemMaxStackSize) {
                    result.setCount(0);
                    slotStack.setCount(combinedCount);
                } else {
                    result.shrink(itemMaxStackSize - slotStack.getCount());
                    slotStack.setCount(itemMaxStackSize);
                }
                setStackInSlot(slotIndex, slotStack);
            }
        }

        private boolean simulateDeposit(ItemStack result) {
            int slotIndex = 0;
            int count = result.getCount();

            while (count > 0 && slotIndex < stacks.size()) {
                ItemStack slotStack = getStackInSlot(slotIndex);
                int itemMaxStackSize = getStackLimit(slotIndex, result);

                if (slotStack.isEmpty()) {
                    count -= itemMaxStackSize;
                } else if (ItemStack.isSameItemSameTags(result, slotStack) && slotStack.getCount() != itemMaxStackSize) {
                    count -= (itemMaxStackSize - slotStack.getCount());
                }
            }

            return count == 0;
        }
    }
}
