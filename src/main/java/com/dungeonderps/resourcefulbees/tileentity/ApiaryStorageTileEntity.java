package com.dungeonderps.resourcefulbees.tileentity;

import com.dungeonderps.resourcefulbees.config.BeeInfo;
import com.dungeonderps.resourcefulbees.config.Config;
import com.dungeonderps.resourcefulbees.container.ApiaryStorageContainer;
import com.dungeonderps.resourcefulbees.container.AutomationSensitiveItemStackHandler;
import com.dungeonderps.resourcefulbees.item.UpgradeItem;
import com.dungeonderps.resourcefulbees.lib.ApiaryOutput;
import com.dungeonderps.resourcefulbees.lib.ApiaryTabs;
import com.dungeonderps.resourcefulbees.registry.RegistryHandler;
import com.dungeonderps.resourcefulbees.utils.MathUtils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fml.network.NetworkHooks;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static com.dungeonderps.resourcefulbees.lib.BeeConstants.*;
import static net.minecraft.inventory.container.Container.areItemsAndTagsEqual;

public class ApiaryStorageTileEntity extends TileEntity implements INamedContainerProvider, ITickableTileEntity, IApiaryMultiblock {

    public static final int UPGRADE_SLOT = 0;

    private BlockPos apiaryPos;

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
        if (apiaryPos != null && world != null) {
            TileEntity tile = world.getTileEntity(apiaryPos);
            if (tile instanceof ApiaryTileEntity) {
                ApiaryTileEntity apiary = (ApiaryTileEntity) tile;
                if (apiary.apiaryStoragePos == null || !apiary.apiaryStoragePos.equals(this.getPos()) || !apiary.isValidApiary()) {
                    apiaryPos = null;
                }
            } else {
                apiaryPos = null;
            }
        }
    }

    private void updateNumberOfSlots() {
        int count = 9;
        if (!h.getStackInSlot(0).isEmpty()) {
            ItemStack upgradeItem = h.getStackInSlot(0);
            if (UpgradeItem.isUpgradeItem(upgradeItem)) {
                CompoundNBT data = UpgradeItem.getUpgradeData(upgradeItem);

                if (data != null && data.getString(NBT_UPGRADE_TYPE).equals(NBT_STORAGE_UPGRADE)) {
                    count = (int) MathUtils.clamp(data.getFloat(NBT_SLOT_UPGRADE), 1F, 108F);
                }
            }
        }

        numberOfSlots = count;
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
        CompoundNBT invTag = nbt.getCompound(NBT_INVENTORY);
        h.deserializeNBT(invTag);
        if (nbt.contains(NBT_APIARY_POS))
            apiaryPos = NBTUtil.readBlockPos(nbt.getCompound(NBT_APIARY_POS));
    }

    public CompoundNBT saveToNBT(CompoundNBT nbt) {
        CompoundNBT inv = this.h.serializeNBT();
        nbt.put(NBT_INVENTORY, inv);
        if (apiaryPos != null)
            nbt.put(NBT_APIARY_POS, NBTUtil.writeBlockPos(apiaryPos));
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

    @Nullable
    @Override
    public SUpdateTileEntityPacket getUpdatePacket() {
        return super.getUpdatePacket();
    }

    public void deliverHoneycomb(String beeType, int apiaryTier) {
        boolean flag = false;
        ItemStack itemstack;
        int slotIndex = 1;

        switch (apiaryTier) {
            case 8:
                if (Config.T4_APIARY_OUTPUT.get() == ApiaryOutput.BLOCK)
                    itemstack = new ItemStack(RegistryHandler.HONEYCOMB_BLOCK_ITEM.get(), Config.T4_APIARY_QUANTITY.get());
                else
                    itemstack = new ItemStack(RegistryHandler.RESOURCEFUL_HONEYCOMB.get(), Config.T4_APIARY_QUANTITY.get());
                break;
            case 7:
                if (Config.T3_APIARY_OUTPUT.get() == ApiaryOutput.BLOCK)
                    itemstack = new ItemStack(RegistryHandler.HONEYCOMB_BLOCK_ITEM.get(), Config.T3_APIARY_QUANTITY.get());
                else
                    itemstack = new ItemStack(RegistryHandler.RESOURCEFUL_HONEYCOMB.get(), Config.T3_APIARY_QUANTITY.get());
                break;
            case 6:
                if (Config.T2_APIARY_OUTPUT.get() == ApiaryOutput.BLOCK)
                    itemstack = new ItemStack(RegistryHandler.HONEYCOMB_BLOCK_ITEM.get(), Config.T2_APIARY_QUANTITY.get());
                else
                    itemstack = new ItemStack(RegistryHandler.RESOURCEFUL_HONEYCOMB.get(), Config.T2_APIARY_QUANTITY.get());
                break;
            default:
                if (Config.T1_APIARY_OUTPUT.get() == ApiaryOutput.BLOCK)
                    itemstack = new ItemStack(RegistryHandler.HONEYCOMB_BLOCK_ITEM.get(), Config.T1_APIARY_QUANTITY.get());
                else
                    itemstack = new ItemStack(RegistryHandler.RESOURCEFUL_HONEYCOMB.get(), Config.T1_APIARY_QUANTITY.get());
                break;
        }

        itemstack.getOrCreateChildTag(NBT_ROOT).putString(NBT_COLOR, BeeInfo.getInfo(beeType).getHoneycombColor());
        itemstack.getOrCreateChildTag(NBT_ROOT).putString(NBT_BEE_TYPE, BeeInfo.getInfo(beeType).getName());

        while (!itemstack.isEmpty()){
            if (slotIndex > numberOfSlots) {
                break;
            }
            ItemStack slotStack = h.getStackInSlot(slotIndex);

            int maxStackSize = h.getSlotLimit(slotIndex);

            if(slotStack.isEmpty()) {
                int count = itemstack.getCount();
                slotStack = itemstack.copy();
                if (count > maxStackSize) {
                    slotStack.setCount(maxStackSize);
                    itemstack.setCount(count-maxStackSize);
                } else {
                    itemstack.setCount(0);
                }
                h.setStackInSlot(slotIndex, slotStack);
            } else if (areItemsAndTagsEqual(itemstack, slotStack)) {
                int j = itemstack.getCount() + slotStack.getCount();
                if (j <= maxStackSize) {
                    itemstack.setCount(0);
                    slotStack.setCount(j);
                    h.setStackInSlot(slotIndex, slotStack);
                } else if (slotStack.getCount() < maxStackSize) {
                    itemstack.shrink(maxStackSize - slotStack.getCount());
                    slotStack.setCount(maxStackSize);
                    h.setStackInSlot(slotIndex, slotStack);
                }
            }

            ++slotIndex;
        }
    }





    public void rebuildOpenContainers() {
        if (world != null) {
            float f = 5.0F;
            BlockPos pos = this.pos;

            for (PlayerEntity playerentity : world.getEntitiesWithinAABB(PlayerEntity.class, new AxisAlignedBB(pos.getX() - f, pos.getY() - f, pos.getZ() - f, (pos.getX() + 1) + f, (pos.getY() + 1) + f, (pos.getZ() + 1) + f))) {
                if (playerentity.openContainer instanceof ApiaryStorageContainer) {
                    ApiaryStorageContainer openContainer = (ApiaryStorageContainer) playerentity.openContainer;
                    ApiaryStorageTileEntity apiaryStorageTileEntity1 = openContainer.apiaryStorageTileEntity;
                    if (apiaryStorageTileEntity1 == this) {
                        openContainer.setupSlots(true);
                    }
                }
            }
        }
    }


    @Override
    public void onLoad() {
        super.onLoad();
        updateNumberOfSlots();
    }

    public BlockPos getApiaryPos() {
        return apiaryPos;
    }

    public void setApiaryPos(BlockPos apiaryPos) {
        this.apiaryPos = apiaryPos;
    }

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

    @Override
    public void switchTab(ServerPlayerEntity player, ApiaryTabs tab) {
        if (world != null && apiaryPos != null) {
            if (tab == ApiaryTabs.MAIN) {
                TileEntity tile = world.getTileEntity(apiaryPos);
                NetworkHooks.openGui(player, (INamedContainerProvider) tile, apiaryPos);
            }
        }
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
            if (slot == 0) {
                updateNumberOfSlots();
                rebuildOpenContainers();
            }
        }
    }
}
