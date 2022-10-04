package com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.entities;

import com.teamresourceful.resourcefulbees.common.lib.constants.NBTConstants;
import com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.containers.CentrifugeItemOutputContainer;
import com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.entities.base.AbstractGUICentrifugeEntity;
import com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.entities.base.ICentrifugeOutput;
import com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.helpers.CentrifugeTier;
import com.teamresourceful.resourcefulbees.common.recipe.recipes.centrifuge.CentrifugeRecipe;
import com.teamresourceful.resourcefulbees.common.recipe.recipes.centrifuge.outputs.ItemOutput;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
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
        //TODO this falls under the same issue as changing the processing stage in the in the input block.
        // ideally this would not send a full packet of data and would instead only send a packet containing
        // the changed data to players that are actively tracking the block, thus reducing the size, number, and frequency of packets being sent.
        // see read/write nbt regarding amount of data being sent
        if (level == null) return;
        this.voidExcess = voidExcess;
        level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_CLIENTS);
    }

    public boolean voidsExcess() {
        return voidExcess;
    }

    @Override
    public void purgeContents() {
        int slots = inventoryHandler.getSlots();
        for (int i = 0; i < slots; i++) {
            inventoryHandler.setStackInSlot(i, ItemStack.EMPTY);
        }
    }

    public boolean depositResult(CentrifugeRecipe.Output<ItemOutput> recipeOutput, int processQuantity) {
        ItemStack result = recipeOutput.pool().next().itemStack();
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
        voidExcess = tag.getBoolean("void_excess");
        super.readNBT(tag);
    }

    @NotNull
    @Override
    protected CompoundTag writeNBT() {
        CompoundTag tag = super.writeNBT();
        tag.put(NBTConstants.NBT_INVENTORY, inventoryHandler.serializeNBT());
        tag.putBoolean("void_excess", voidExcess);
        return tag;
    }
    //endregion

    //region CAPABILITIES
    @NotNull
    @Override
    public <T> LazyOptional<T> capability(@NotNull Capability<T> cap, @Nullable Direction side) {
        return cap.equals(ForgeCapabilities.ITEM_HANDLER) ? lazyOptional.cast() : super.capability(cap, side);
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
