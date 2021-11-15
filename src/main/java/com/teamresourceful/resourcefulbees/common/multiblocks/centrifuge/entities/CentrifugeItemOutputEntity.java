package com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.entities;

import com.teamresourceful.resourcefulbees.api.beedata.outputs.ItemOutput;
import com.teamresourceful.resourcefulbees.common.lib.constants.NBTConstants;
import com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.containers.CentrifugeItemOutputContainer;
import com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.entities.base.AbstractTieredCentrifugeEntity;
import com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.entities.base.ICentrifugeOutput;
import com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.helpers.CentrifugeTier;
import com.teamresourceful.resourcefulbees.common.recipe.CentrifugeRecipe;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CentrifugeItemOutputEntity extends AbstractTieredCentrifugeEntity implements ICentrifugeOutput<ItemOutput>, INamedContainerProvider {

    private final InventoryHandler inventoryHandler;
    private final LazyOptional<IItemHandler> lazyOptional;
    private boolean voidExcess = true;

    public CentrifugeItemOutputEntity(RegistryObject<TileEntityType<CentrifugeItemOutputEntity>> tileType, CentrifugeTier tier) {
        super(tileType.get(), tier);
        this.inventoryHandler = new InventoryHandler(tier.getSlots());
        this.lazyOptional = LazyOptional.of(() -> inventoryHandler);
    }

    @Override
    public @NotNull ITextComponent getDisplayName() {
        return new TranslationTextComponent("gui.resourcefulbees.centrifuge_input");
    }

    @Nullable
    @Override
    public Container createMenu(int id, @NotNull PlayerInventory playerInventory, @NotNull PlayerEntity playerEntity) {
        return new CentrifugeItemOutputContainer(id, playerInventory, this);
    }

    public void setVoidExcess(boolean voidExcess) {
        this.voidExcess = voidExcess;
    }

    public boolean depositResult(CentrifugeRecipe.Output<ItemOutput> recipeOutput, int processQuantity) {
        ItemStack result = recipeOutput.getPool().next().getItemStack();
        result.setCount(result.getCount() * processQuantity);
        return inventoryHandler.depositResult(result);
    }

    public InventoryHandler getInventoryHandler() {
        return inventoryHandler;
    }

    //region NBT HANDLING
    @NotNull
    @Override
    public CompoundNBT getUpdateTag() {
        CompoundNBT nbtTagCompound = new CompoundNBT();
        save(nbtTagCompound);
        return nbtTagCompound;
    }

    @Override
    public void handleUpdateTag(@NotNull BlockState state, CompoundNBT tag) {
        this.load(state, tag);
    }

    @Override
    protected void readNBT(@NotNull CompoundNBT tag) {
        inventoryHandler.deserializeNBT(tag.getCompound(NBTConstants.NBT_INVENTORY));
        super.readNBT(tag);
    }

    @NotNull
    @Override
    protected CompoundNBT writeNBT() {
        CompoundNBT tag = new CompoundNBT();
        tag.put(NBTConstants.NBT_INVENTORY, inventoryHandler.serializeNBT());
        return tag;
    }
    //endregion

    //region CAPABILITIES
    @NotNull
    @Override
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap.equals(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)) return lazyOptional.cast();
        return super.getCapability(cap, side);
    }

    @Override
    protected void invalidateCaps() {
        super.invalidateCaps();
        this.lazyOptional.invalidate();
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
            if (result.isEmpty() || controller.dumpsContainItem(result)) return true;

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
            } else if (Container.consideredTheSameItem(result, slotStack) && slotStack.getCount() != itemMaxStackSize) {
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
                } else if (Container.consideredTheSameItem(result, slotStack) && slotStack.getCount() != itemMaxStackSize) {
                    count -= (itemMaxStackSize - slotStack.getCount());
                }
            }

            return count == 0;
        }
    }
}
