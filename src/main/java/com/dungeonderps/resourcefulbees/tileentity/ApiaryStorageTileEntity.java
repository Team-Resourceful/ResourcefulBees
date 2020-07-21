package com.dungeonderps.resourcefulbees.tileentity;

import com.dungeonderps.resourcefulbees.config.BeeInfo;
import com.dungeonderps.resourcefulbees.config.Config;
import com.dungeonderps.resourcefulbees.container.ApiaryStorageContainer;
import com.dungeonderps.resourcefulbees.container.AutomationSensitiveItemStackHandler;
import com.dungeonderps.resourcefulbees.item.UpgradeItem;
import com.dungeonderps.resourcefulbees.lib.ApiaryOutput;
import com.dungeonderps.resourcefulbees.lib.BeeConstants;
import com.dungeonderps.resourcefulbees.registry.RegistryHandler;
import com.dungeonderps.resourcefulbees.utils.MathUtils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;

import static net.minecraft.inventory.container.Container.areItemsAndTagsEqual;

public class ApiaryStorageTileEntity extends TileEntity implements INamedContainerProvider, ITickableTileEntity {

    public final HashMap<String, Honeycomb> HONEYCOMBS = new HashMap<>();

    public static final int UPGRADE_SLOT = 0;

    public int numberOfSlots = 9;

    public AutomationSensitiveItemStackHandler h = new ApiaryStorageTileEntity.TileStackHandler(110);
    public LazyOptional<IItemHandler> lazyOptional = LazyOptional.of(() -> h);

    public ApiaryStorageTileEntity() {
        super(RegistryHandler.APIARY_STORAGE_TILE_ENTITY.get());
    }

    @Nonnull
    @Override
    public TileEntityType<?> getType() {
        return RegistryHandler.APIARY_STORAGE_TILE_ENTITY.get();
    }

    @Nonnull
    @Override
    public ITextComponent getDisplayName() {
        return new TranslationTextComponent("gui.resourcefulbees.apiary_storage");
    }

    @Nullable
    @Override
    public Container createMenu(int i, @Nonnull PlayerInventory playerInventory, @Nonnull PlayerEntity playerEntity) {
        if (world != null)
            return new ApiaryStorageContainer(i, world, pos, playerInventory);
        return null;
    }

    @Override
    public void tick() {
        numberOfSlots = h.getStackInSlot(0).isEmpty() ? 9 : updateNumberOfSlots();
    }

    private int updateNumberOfSlots() {
        int count = 9;
        ItemStack upgradeItem = h.getStackInSlot(0);
        if (UpgradeItem.isUpgradeItem(upgradeItem)) {
            CompoundNBT data = UpgradeItem.getUpgradeData(upgradeItem);

            if (data != null && data.getString(BeeConstants.NBT_UPGRADE_TYPE).equals(BeeConstants.NBT_STORAGE_UPGRADE)) {
                count = (int) MathUtils.clamp(data.getFloat(BeeConstants.NBT_SLOT_UPGRADE), 1F, 108F);
            }
        }
        return count;
    }

    @Override
    public void read(@Nonnull CompoundNBT nbt) {
        super.read(nbt);
        this.loadFromNBT(nbt);
    }

    @Nonnull
    @Override
    public CompoundNBT write(@Nonnull CompoundNBT nbt) {
        super.write(nbt);
        return this.saveToNBT(nbt);
    }

    public void loadFromNBT(CompoundNBT nbt) {
        CompoundNBT invTag = nbt.getCompound("inv");
        h.deserializeNBT(invTag);
    }

    public CompoundNBT saveToNBT(CompoundNBT nbt) {
        CompoundNBT inv = this.h.serializeNBT();
        nbt.put("inv", inv);
        return nbt;
    }

    @Nonnull
    @Override
    public CompoundNBT getUpdateTag() {
        CompoundNBT nbtTagCompound = new CompoundNBT();
        write(nbtTagCompound);
        return nbtTagCompound;
    }

    @Override
    public void handleUpdateTag(CompoundNBT tag) {
        this.read(tag);
    }

    public void deliverHoneycomb(String beeType, int apiaryTier) {
        boolean flag = false;
        ItemStack itemstack;
        int slotIndex = 1;

        switch (apiaryTier) {
            case 8:
                if (Config.T1_APIARY_OUTPUT.get() == ApiaryOutput.BLOCK)
                    itemstack = new ItemStack(RegistryHandler.HONEYCOMB_BLOCK_ITEM.get(), Config.T4_APIARY_QUANTITY.get());
                else
                    itemstack = new ItemStack(RegistryHandler.RESOURCEFUL_HONEYCOMB.get(), Config.T4_APIARY_QUANTITY.get());
                break;
            case 7:
                if (Config.T2_APIARY_OUTPUT.get() == ApiaryOutput.BLOCK)
                    itemstack = new ItemStack(RegistryHandler.HONEYCOMB_BLOCK_ITEM.get(), Config.T3_APIARY_QUANTITY.get());
                else
                    itemstack = new ItemStack(RegistryHandler.RESOURCEFUL_HONEYCOMB.get(), Config.T3_APIARY_QUANTITY.get());
                break;
            case 6:
                if (Config.T3_APIARY_OUTPUT.get() == ApiaryOutput.BLOCK)
                    itemstack = new ItemStack(RegistryHandler.HONEYCOMB_BLOCK_ITEM.get(), Config.T2_APIARY_QUANTITY.get());
                else
                    itemstack = new ItemStack(RegistryHandler.RESOURCEFUL_HONEYCOMB.get(), Config.T2_APIARY_QUANTITY.get());
                break;
            default:
                if (Config.T4_APIARY_OUTPUT.get() == ApiaryOutput.BLOCK)
                    itemstack = new ItemStack(RegistryHandler.HONEYCOMB_BLOCK_ITEM.get(), Config.T1_APIARY_QUANTITY.get());
                else
                    itemstack = new ItemStack(RegistryHandler.RESOURCEFUL_HONEYCOMB.get(), Config.T1_APIARY_QUANTITY.get());
                break;
        }

        itemstack.getOrCreateChildTag(BeeConstants.NBT_ROOT).putString(BeeConstants.NBT_COLOR, BeeInfo.getInfo(beeType).getHoneycombColor());
        itemstack.getOrCreateChildTag(BeeConstants.NBT_ROOT).putString(BeeConstants.NBT_BEE_TYPE, BeeInfo.getInfo(beeType).getName());

        while (!itemstack.isEmpty()){
            if (slotIndex >= numberOfSlots) {
                break;
            }
            ItemStack slotStack = h.getStackInSlot(slotIndex);

            if(slotStack.isEmpty()) {
                slotStack = itemstack.copy();
                itemstack.setCount(0);
                h.setStackInSlot(slotIndex, slotStack);
                break;
            } else if (areItemsAndTagsEqual(itemstack, slotStack)) {
                int j = itemstack.getCount() + slotStack.getCount();
                if (j <= 64) {
                    itemstack.setCount(0);
                    slotStack.setCount(j);
                    h.setStackInSlot(slotIndex, slotStack);
                } else if (slotStack.getCount() < 64) {
                    itemstack.shrink(64 - slotStack.getCount());
                    slotStack.setCount(64);
                    h.setStackInSlot(slotIndex, slotStack);
                }
            }

            ++slotIndex;
        }
    }



/*
    protected boolean mergeItemStack(ItemStack stack, int startIndex, int endIndex, boolean reverseDirection) {
        boolean flag = false;
        int i = startIndex;
        if (reverseDirection) {
            i = endIndex - 1;
        }

        if (stack.isStackable()) {
            while(!stack.isEmpty()) {
                if (reverseDirection) {
                    if (i < startIndex) {
                        break;
                    }
                } else if (i >= endIndex) {
                    break;
                }

                Slot slot = this.inventorySlots.get(i);
                ItemStack itemstack = slot.getStack();
                if (!itemstack.isEmpty() && areItemsAndTagsEqual(stack, itemstack)) {
                    int j = itemstack.getCount() + stack.getCount();
                    int maxSize = Math.min(slot.getSlotStackLimit(), stack.getMaxStackSize());
                    if (j <= maxSize) {
                        stack.setCount(0);
                        itemstack.setCount(j);
                        slot.onSlotChanged();
                        flag = true;
                    } else if (itemstack.getCount() < maxSize) {
                        stack.shrink(maxSize - itemstack.getCount());
                        itemstack.setCount(maxSize);
                        slot.onSlotChanged();
                        flag = true;
                    }
                }

                if (reverseDirection) {
                    --i;
                } else {
                    ++i;
                }
            }
        }

        if (!stack.isEmpty()) {
            if (reverseDirection) {
                i = endIndex - 1;
            } else {
                i = startIndex;
            }

            while(true) {
                if (reverseDirection) {
                    if (i < startIndex) {
                        break;
                    }
                } else if (i >= endIndex) {
                    break;
                }

                Slot slot1 = this.inventorySlots.get(i);
                ItemStack itemstack1 = slot1.getStack();
                if (itemstack1.isEmpty() && slot1.isItemValid(stack)) {
                    if (stack.getCount() > slot1.getSlotStackLimit()) {
                        slot1.putStack(stack.split(slot1.getSlotStackLimit()));
                    } else {
                        slot1.putStack(stack.split(stack.getCount()));
                    }

                    slot1.onSlotChanged();
                    flag = true;
                    break;
                }

                if (reverseDirection) {
                    --i;
                } else {
                    ++i;
                }
            }
        }

        return flag;
    }


 */







    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        return cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY ? lazyOptional.cast() :
                super.getCapability(cap, side);
    }

    public AutomationSensitiveItemStackHandler.IAcceptor getAcceptor() {
        return (slot, stack, automation) -> !automation || slot == 0;
    }

    public AutomationSensitiveItemStackHandler.IRemover getRemover() {
        return (slot, automation) -> !automation || slot > 0 && slot <= 110;
    }

    protected class TileStackHandler extends AutomationSensitiveItemStackHandler {

        protected TileStackHandler(int slots) {
            super(slots);
        }

        @Override
        public AutomationSensitiveItemStackHandler.IAcceptor getAcceptor() {
            return ApiaryStorageTileEntity.this.getAcceptor();
        }

        @Override
        public AutomationSensitiveItemStackHandler.IRemover getRemover() {
            return ApiaryStorageTileEntity.this.getRemover();
        }

        @Override
        protected void onContentsChanged(int slot) {
            super.onContentsChanged(slot);
            markDirty();
        }
    }

    public static class Honeycomb {
        private final String type;
        private int count;
        private int slotIndex;

        public Honeycomb(String type, int count) {
            this.type = type;
            this.count = count;
        }

        public void addToStack(int count) {
            this.count += count;
        }

        public void takeFromStack(int count) {
            this.count -= count;
        }

        public String getType(){
            return type;
        }

        public int getCount() {
            return count;
        }

        public int getSlotIndex() {
            return slotIndex;
        }

        public void setSlotIndex(int slotIndex) {
            this.slotIndex = slotIndex;
        }
    }
}
