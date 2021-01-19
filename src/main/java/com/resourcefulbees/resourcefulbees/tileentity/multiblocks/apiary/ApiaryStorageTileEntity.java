package com.resourcefulbees.resourcefulbees.tileentity.multiblocks.apiary;

import com.resourcefulbees.resourcefulbees.api.IBeeRegistry;
import com.resourcefulbees.resourcefulbees.api.ICustomBee;
import com.resourcefulbees.resourcefulbees.api.beedata.CustomBeeData;
import com.resourcefulbees.resourcefulbees.config.Config;
import com.resourcefulbees.resourcefulbees.container.ApiaryStorageContainer;
import com.resourcefulbees.resourcefulbees.container.AutomationSensitiveItemStackHandler;
import com.resourcefulbees.resourcefulbees.item.UpgradeItem;
import com.resourcefulbees.resourcefulbees.lib.ApiaryOutput;
import com.resourcefulbees.resourcefulbees.lib.ApiaryTabs;
import com.resourcefulbees.resourcefulbees.lib.BeeConstants;
import com.resourcefulbees.resourcefulbees.lib.NBTConstants;
import com.resourcefulbees.resourcefulbees.registry.BeeRegistry;
import com.resourcefulbees.resourcefulbees.registry.ModItems;
import com.resourcefulbees.resourcefulbees.registry.ModTileEntityTypes;
import com.resourcefulbees.resourcefulbees.utils.MathUtils;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.NetworkManager;
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
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static net.minecraft.inventory.container.Container.areItemsAndTagsEqual;

public class ApiaryStorageTileEntity extends TileEntity implements INamedContainerProvider, ITickableTileEntity, IApiaryMultiblock {

    public static final int UPGRADE_SLOT = 0;
    private static final IBeeRegistry BEE_REGISTRY = BeeRegistry.getRegistry();

    private BlockPos apiaryPos;
    private ApiaryTileEntity apiary;

    public int numberOfSlots = 9;

    public AutomationSensitiveItemStackHandler h = new ApiaryStorageTileEntity.TileStackHandler(110);
    public LazyOptional<IItemHandler> lazyOptional = LazyOptional.of(() -> h);

    public ApiaryStorageTileEntity() {
        super(ModTileEntityTypes.APIARY_STORAGE_TILE_ENTITY.get());
    }

    @Nonnull
    @Override
    public TileEntityType<?> getType() {
        return ModTileEntityTypes.APIARY_STORAGE_TILE_ENTITY.get();
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
        if (world != null && !world.isRemote) {
            validateApiaryLink();
        }
    }

    public ApiaryTileEntity getApiary() {
        if (apiaryPos != null && world != null) {  //validate apiary first
            TileEntity tile = world.getTileEntity(apiaryPos); //get apiary pos
            if (tile instanceof ApiaryTileEntity) { //check tile is an apiary tile
                return (ApiaryTileEntity) tile;
            }
        }
        return null;
    }

    @SuppressWarnings("UnusedReturnValue")
    public boolean validateApiaryLink() {
        apiary = getApiary();
        if (apiary == null || apiary.storagePos == null || !apiary.storagePos.equals(this.getPos()) || !apiary.isValidApiary(false)) { //check apiary has storage location equal to this and apiary is valid
            apiaryPos = null; //if not set these to null
            return false;
        }
        return true;
    }

    private void updateNumberOfSlots() {
        int count = 9;
        if (!h.getStackInSlot(0).isEmpty()) {
            ItemStack upgradeItem = h.getStackInSlot(0);
            if (UpgradeItem.isUpgradeItem(upgradeItem)) {
                CompoundNBT data = UpgradeItem.getUpgradeData(upgradeItem);

                if (data != null && data.getString(NBTConstants.NBT_UPGRADE_TYPE).equals(NBTConstants.NBT_STORAGE_UPGRADE)) {
                    count = (int) MathUtils.clamp(data.getFloat(NBTConstants.NBT_SLOT_UPGRADE), 1F, 108F);
                }
            }
        }

        numberOfSlots = count;
    }

    @Override
    public void fromTag(@Nonnull BlockState state, @Nonnull CompoundNBT nbt) {
        super.fromTag(state, nbt);
        this.loadFromNBT(nbt);
    }

    @Nonnull
    @Override
    public CompoundNBT write(@Nonnull CompoundNBT nbt) {
        super.write(nbt);
        return this.saveToNBT(nbt);
    }

    public void loadFromNBT(CompoundNBT nbt) {
        CompoundNBT invTag = nbt.getCompound(NBTConstants.NBT_INVENTORY);
        h.deserializeNBT(invTag);
        if (nbt.contains(NBTConstants.NBT_APIARY_POS))
            apiaryPos = NBTUtil.readBlockPos(nbt.getCompound(NBTConstants.NBT_APIARY_POS));
        if (nbt.contains(NBTConstants.NBT_SLOT_COUNT))
            this.numberOfSlots = nbt.getInt(NBTConstants.NBT_SLOT_COUNT);
    }

    public CompoundNBT saveToNBT(CompoundNBT nbt) {
        CompoundNBT inv = this.h.serializeNBT();
        nbt.put(NBTConstants.NBT_INVENTORY, inv);
        if (apiaryPos != null)
            nbt.put(NBTConstants.NBT_APIARY_POS, NBTUtil.writeBlockPos(apiaryPos));
        if (numberOfSlots != 9) {
            nbt.putInt(NBTConstants.NBT_SLOT_COUNT, numberOfSlots);
        }
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
    public void handleUpdateTag(@Nonnull BlockState state, CompoundNBT tag) {
        this.fromTag(state, tag);
    }

    @Nullable
    @Override
    public SUpdateTileEntityPacket getUpdatePacket() {
        CompoundNBT nbt = new CompoundNBT();
        if (apiaryPos != null)
            nbt.put(NBTConstants.NBT_APIARY_POS, NBTUtil.writeBlockPos(apiaryPos));
        return new SUpdateTileEntityPacket(pos, 0, nbt);
    }

    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
        CompoundNBT nbt = pkt.getNbtCompound();
        if (nbt.contains(NBTConstants.NBT_APIARY_POS))
            apiaryPos = NBTUtil.readBlockPos(nbt.getCompound(NBTConstants.NBT_APIARY_POS));
    }

    public void deliverHoneycomb(BeeEntity entity, int apiaryTier) {
        String beeType;
        if (entity instanceof ICustomBee && ((ICustomBee) entity).getBeeData().hasHoneycomb()) {
            beeType = ((ICustomBee) entity).getBeeType();
        } else if (!(entity instanceof ICustomBee)) {
            beeType = BeeConstants.VANILLA_BEE_TYPE;
        } else {
            return;
        }

        ItemStack itemstack;
        ItemStack comb = beeType.equals(BeeConstants.VANILLA_BEE_TYPE) ? new ItemStack(Items.HONEYCOMB) : ((ICustomBee) entity).getBeeData().getCombStack();
        ItemStack combBlock = beeType.equals(BeeConstants.VANILLA_BEE_TYPE) ? new ItemStack(Items.HONEYCOMB_BLOCK) : ((ICustomBee) entity).getBeeData().getCombBlockItemStack();
        int[] outputAmounts = beeType.equals(BeeConstants.VANILLA_BEE_TYPE) ? null : BEE_REGISTRY.getBeeData(beeType).getApiaryOutputAmounts();

        switch (apiaryTier) {
            case 8:
                itemstack = (Config.T4_APIARY_OUTPUT.get() == ApiaryOutput.BLOCK) ? combBlock.copy() : comb.copy();
                itemstack.setCount(outputAmounts != null && outputAmounts[3] != -1 ? outputAmounts[3] : Config.T4_APIARY_QUANTITY.get());
                break;
            case 7:
                itemstack = (Config.T3_APIARY_OUTPUT.get() == ApiaryOutput.BLOCK) ? combBlock.copy() : comb.copy();
                itemstack.setCount(outputAmounts != null && outputAmounts[2] != -1 ? outputAmounts[2] : Config.T3_APIARY_QUANTITY.get());
                break;
            case 6:
                itemstack = (Config.T2_APIARY_OUTPUT.get() == ApiaryOutput.BLOCK) ? combBlock.copy() : comb.copy();
                itemstack.setCount(outputAmounts != null && outputAmounts[1] != -1 ? outputAmounts[1] : Config.T2_APIARY_QUANTITY.get());
                break;
            default:
                itemstack = (Config.T1_APIARY_OUTPUT.get() == ApiaryOutput.BLOCK) ? combBlock.copy() : comb.copy();
                itemstack.setCount(outputAmounts != null && outputAmounts[0] != -1 ? outputAmounts[0] : Config.T1_APIARY_QUANTITY.get());
                break;
        }
        depositItemStack(itemstack);
    }

    public boolean breedComplete(String p1, String p2) {
        if (inventoryHasSpace()) {
            CustomBeeData childBeeData = BEE_REGISTRY.getWeightedChild(p1, p2);
            float breedChance = BeeRegistry.getRegistry().getBreedChance(p1, p2, childBeeData);
            EntityType<?> entityType = ForgeRegistries.ENTITIES.getValue(childBeeData.getEntityTypeRegistryID());

            if (world != null && entityType != null) {
                Entity entity = entityType.create(world);
                if (entity != null) {
                    String type = EntityType.getKey(entity.getType()).toString();
                    CompoundNBT nbt = new CompoundNBT();
                    nbt.putString(NBTConstants.NBT_ENTITY, type);
                    ICustomBee beeEntity = (ICustomBee) entity;
                    nbt.putString(NBTConstants.NBT_BEE_TYPE, beeEntity.getBeeType());
                    if (beeEntity.getBeeData().getColorData().hasPrimaryColor()) {
                        nbt.putString(NBTConstants.NBT_COLOR, beeEntity.getBeeData().getColorData().getPrimaryColor());
                    } else {
                        nbt.putString(NBTConstants.NBT_COLOR, String.valueOf(BeeConstants.DEFAULT_ITEM_COLOR));
                    }
                    entity.writeWithoutTypeId(nbt);
                    ItemStack beeJar = new ItemStack(ModItems.BEE_JAR.get());
                    ItemStack emptyBeeJar = new ItemStack(ModItems.BEE_JAR.get());
                    beeJar.setTag(nbt);
                    // if failed, will deposit empty bee jar
                    float nextFloat = world.rand.nextFloat();
                    if (breedChance >= nextFloat) {
                        return depositItemStack(beeJar);
                    } else {
                        return depositItemStack(emptyBeeJar);
                    }
                }
            }
        }

        return false;
    }

    public boolean inventoryHasSpace() {
        for (int i = 1; i <= numberOfSlots; ++i) {
            if (h.getStackInSlot(i).isEmpty()) {
                return true;
            }
        }
        return false;
    }

    public boolean depositItemStack(ItemStack itemstack) {
        int slotIndex = 1;
        while (!itemstack.isEmpty()) {
            if (slotIndex > numberOfSlots) {
                break;
            }
            ItemStack slotStack = h.getStackInSlot(slotIndex);

            int maxStackSize = h.getSlotLimit(slotIndex);

            if (slotStack.isEmpty()) {
                int count = itemstack.getCount();
                slotStack = itemstack.copy();
                if (count > maxStackSize) {
                    slotStack.setCount(maxStackSize);
                    itemstack.setCount(count - maxStackSize);
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

        return itemstack.isEmpty();
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
        return (slot, stack, automation) -> !automation || (slot == 0 && stack.getItem() instanceof UpgradeItem);
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
            } else if (tab == ApiaryTabs.BREED) {
                TileEntity tile = world.getTileEntity(apiary.breederPos);
                NetworkHooks.openGui(player, (INamedContainerProvider) tile, apiary.breederPos);
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

        @Override
        public int getSlotLimit(int slot) {
            if (slot == UPGRADE_SLOT) {
                return 1;
            }
            return super.getSlotLimit(slot);
        }
    }
}
