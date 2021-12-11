package com.teamresourceful.resourcefulbees.common.tileentity.multiblocks.apiary;

import com.teamresourceful.resourcefulbees.api.IBeeRegistry;
import com.teamresourceful.resourcefulbees.api.ICustomBee;
import com.teamresourceful.resourcefulbees.api.beedata.breeding.BeeFamily;
import com.teamresourceful.resourcefulbees.api.beedata.breeding.BreedData;
import com.teamresourceful.resourcefulbees.api.honeycombdata.OutputVariation;
import com.teamresourceful.resourcefulbees.common.inventory.AutomationSensitiveItemStackHandler;
import com.teamresourceful.resourcefulbees.common.inventory.containers.ApiaryStorageContainer;
import com.teamresourceful.resourcefulbees.common.item.BeeJar;
import com.teamresourceful.resourcefulbees.common.item.UpgradeItem;
import com.teamresourceful.resourcefulbees.common.lib.constants.NBTConstants;
import com.teamresourceful.resourcefulbees.common.lib.constants.TranslationConstants;
import com.teamresourceful.resourcefulbees.common.lib.enums.ApiaryOutputType;
import com.teamresourceful.resourcefulbees.common.lib.enums.ApiaryTab;
import com.teamresourceful.resourcefulbees.common.registry.custom.BeeRegistry;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.ModItems;
import com.teamresourceful.resourcefulbees.common.utils.BeeInfoUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ApiaryStorageTileEntity extends BlockEntity implements MenuProvider, IApiaryMultiblock {

    public static final int UPGRADE_SLOT = 0;
    private static final IBeeRegistry BEE_REGISTRY = BeeRegistry.getRegistry();
    private static final ItemStack VANILLA_HONEYCOMB = new ItemStack(Items.HONEYCOMB);
    private static final ItemStack VANILLA_HONEYCOMB_BLOCK = new ItemStack(Items.HONEYCOMB_BLOCK);

    private BlockPos apiaryPos;
    private ApiaryTileEntity apiary;

    private int numberOfSlots = 9;

    private final AutomationSensitiveItemStackHandler itemStackHandler = new ApiaryStorageTileEntity.TileStackHandler(110);
    private final LazyOptional<IItemHandler> lazyOptional = LazyOptional.of(this::getItemStackHandler);

    public ApiaryStorageTileEntity(BlockPos pos, BlockState state) {
        super(null, pos, state);
    }

    @NotNull
    @Override
    public Component getDisplayName() {
        return TranslationConstants.Guis.APIARY_STORAGE;
    }

    @Nullable
    @Override
    public ApiaryStorageContainer createMenu(int i, @NotNull Inventory playerInventory, @NotNull Player playerEntity) {
        if (level != null)
            return new ApiaryStorageContainer(i, level, worldPosition, playerInventory);
        return null;
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, ApiaryStorageTileEntity storageTileEntity) {
        storageTileEntity.validateApiaryLink();
    }

    public ApiaryTileEntity getApiary() {
        if (apiaryPos != null && level != null) {  //validate apiary first
            if (level.getBlockEntity(apiaryPos) instanceof ApiaryTileEntity apiary) { //check tile is an apiary tile
                return apiary;
            }
        }
        return null;
    }

    @SuppressWarnings("UnusedReturnValue")
    public boolean validateApiaryLink() {
        apiary = getApiary();
        if (apiary == null || apiary.getStoragePos() == null || !apiary.getStoragePos().equals(this.getBlockPos()) || !apiary.isValidApiary(false)) { //check apiary has storage location equal to this and apiary is valid
            apiaryPos = null; //if not set these to null
            return false;
        }
        return true;
    }

    private void updateNumberOfSlots() {
        int count = 9;
        if (!getItemStackHandler().getStackInSlot(0).isEmpty()) {
            ItemStack upgradeItem = getItemStackHandler().getStackInSlot(0);
            if (UpgradeItem.isUpgradeItem(upgradeItem)) {
                CompoundTag data = UpgradeItem.getUpgradeData(upgradeItem);

                if (data != null && data.getString(NBTConstants.NBT_UPGRADE_TYPE).equals(NBTConstants.NBT_STORAGE_UPGRADE)) {
                    count = (int) Mth.clamp(data.getFloat(NBTConstants.NBT_SLOT_UPGRADE), 1F, 108F);
                }
            }
        }

        setNumberOfSlots(count);
    }

    @Override
    public void load(@NotNull CompoundTag nbt) {
        super.load(nbt);
        this.loadFromNBT(nbt);
    }

    public void loadFromNBT(CompoundTag nbt) {
        CompoundTag invTag = nbt.getCompound(NBTConstants.NBT_INVENTORY);
        getItemStackHandler().deserializeNBT(invTag);
        if (nbt.contains(NBTConstants.NBT_APIARY_POS))
            apiaryPos = NbtUtils.readBlockPos(nbt.getCompound(NBTConstants.NBT_APIARY_POS));
        if (nbt.contains(NBTConstants.NBT_SLOT_COUNT))
            this.setNumberOfSlots(nbt.getInt(NBTConstants.NBT_SLOT_COUNT));
    }

    @Override
    public void handleUpdateTag(CompoundTag tag) {
        this.load(tag);
    }

    @NotNull
    @Override
    public CompoundTag save(@NotNull CompoundTag nbt) {
        super.save(nbt);
        return this.saveToNBT(nbt);
    }

    public CompoundTag saveToNBT(CompoundTag nbt) {
        CompoundTag inv = this.getItemStackHandler().serializeNBT();
        nbt.put(NBTConstants.NBT_INVENTORY, inv);
        if (apiaryPos != null)
            nbt.put(NBTConstants.NBT_APIARY_POS, NbtUtils.writeBlockPos(apiaryPos));
        if (getNumberOfSlots() != 9) {
            nbt.putInt(NBTConstants.NBT_SLOT_COUNT, getNumberOfSlots());
        }
        return nbt;
    }

    @NotNull
    @Override
    public CompoundTag getUpdateTag() {
        return save(new CompoundTag());
    }

    @Nullable
    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this, blockEntity -> {
            CompoundTag tag = new CompoundTag();
            if (apiaryPos != null) tag.put(NBTConstants.NBT_APIARY_POS, NbtUtils.writeBlockPos(apiaryPos));
            return tag;
        });
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
        CompoundTag nbt = pkt.getTag();
        if (nbt != null && nbt.contains(NBTConstants.NBT_APIARY_POS))
            apiaryPos = NbtUtils.readBlockPos(nbt.getCompound(NBTConstants.NBT_APIARY_POS));
    }

    public void deliverHoneycomb(Bee entity, int apiaryTier) {
        if (entity instanceof ICustomBee && ((ICustomBee) entity).getHoneycombData().isPresent()) {
            depositItemStack(((ICustomBee) entity).getHoneycombData().get().getApiaryOutput(apiaryTier - 1));
        } else if (!(entity instanceof ICustomBee)) {
            depositItemStack(getVanillaOutput(apiaryTier - 5));
        }
    }

    public static ItemStack getVanillaOutput(int apiaryTier) {
        ItemStack itemstack = (OutputVariation.DEFAULT_APIARY_OUTPUT_TYPES.get(apiaryTier) == ApiaryOutputType.BLOCK) ? VANILLA_HONEYCOMB_BLOCK.copy() : VANILLA_HONEYCOMB.copy();
        itemstack.setCount(OutputVariation.DEFAULT_APIARY_AMOUNTS.get(apiaryTier));
        return itemstack;
    }

    public boolean breedComplete(String p1, String p2) {
        if (inventoryHasSpace()) {
            BeeFamily beeFamily = BEE_REGISTRY.getWeightedChild(p1, p2);
            double breedChance = beeFamily.getChance();
            EntityType<?> entityType = beeFamily.getChildData().getEntityType();

            BreedData p1BreedData = BEE_REGISTRY.getBeeData(p1).getBreedData();
            BreedData p2BreedData = BEE_REGISTRY.getBeeData(p2).getBreedData();
            
            Item p1Returnable = p1BreedData.getFeedReturnItem().orElse(null);
            Item p2Returnable = p2BreedData.getFeedReturnItem().orElse(null);

            if (level != null) {
                Entity entity = entityType.create(level);
                if (entity != null) {
                    ICustomBee beeEntity = (ICustomBee) entity;
                    CompoundTag nbt = BeeInfoUtils.createJarBeeTag((Bee) beeEntity, NBTConstants.NBT_ENTITY);
                    ItemStack beeJar = new ItemStack(ModItems.BEE_JAR.get());
                    ItemStack emptyBeeJar = new ItemStack(ModItems.BEE_JAR.get());
                    beeJar.setTag(nbt);
                    BeeJar.renameJar(beeJar, (Bee) beeEntity);
                    depositItemStack(new ItemStack(p1Returnable, p1BreedData.getFeedAmount()));
                    depositItemStack(new ItemStack(p2Returnable, p2BreedData.getFeedAmount()));
                    // if failed, will deposit empty bee jar
                    float nextFloat = level.random.nextFloat();
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
        for (int i = 1; i <= getNumberOfSlots(); ++i) {
            if (getItemStackHandler().getStackInSlot(i).isEmpty()) {
                return true;
            }
        }
        return false;
    }

    public boolean depositItemStack(ItemStack itemstack) {
        int slotIndex = 1;
        while (!itemstack.isEmpty()) {
            if (slotIndex > getNumberOfSlots()) {
                break;
            }
            ItemStack slotStack = getItemStackHandler().getStackInSlot(slotIndex);

            int maxStackSize = getItemStackHandler().getSlotLimit(slotIndex);

            if (slotStack.isEmpty()) {
                int count = itemstack.getCount();
                slotStack = itemstack.copy();
                if (count > maxStackSize) {
                    slotStack.setCount(maxStackSize);
                    itemstack.setCount(count - maxStackSize);
                } else {
                    itemstack.setCount(0);
                }
                getItemStackHandler().setStackInSlot(slotIndex, slotStack);
            } else if (ItemStack.matches(itemstack, slotStack)) {
                int j = itemstack.getCount() + slotStack.getCount();
                if (j <= maxStackSize) {
                    itemstack.setCount(0);
                    slotStack.setCount(j);
                    getItemStackHandler().setStackInSlot(slotIndex, slotStack);
                } else if (slotStack.getCount() < maxStackSize) {
                    itemstack.shrink(maxStackSize - slotStack.getCount());
                    slotStack.setCount(maxStackSize);
                    getItemStackHandler().setStackInSlot(slotIndex, slotStack);
                }
            }

            ++slotIndex;
        }

        return itemstack.isEmpty();
    }

    public void rebuildOpenContainers() {
        if (level != null) {
            float f = 5.0F;
            BlockPos pos = this.worldPosition;

            for (Player playerentity : level.getEntitiesOfClass(Player.class, new AABB(pos.getX() - f, pos.getY() - f, pos.getZ() - f, (pos.getX() + 1) + f, (pos.getY() + 1) + f, (pos.getZ() + 1) + f))) {
                if (playerentity.containerMenu instanceof ApiaryStorageContainer openContainer) {
                    ApiaryStorageTileEntity apiaryStorageTileEntity1 = openContainer.getApiaryStorageTileEntity();
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

    @NotNull
    @Override
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        return cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY ? getLazyOptional().cast() :
                super.getCapability(cap, side);
    }

    public AutomationSensitiveItemStackHandler.IAcceptor getAcceptor() {
        return (slot, stack, automation) -> !automation || (slot == 0 && stack.getItem() instanceof UpgradeItem);
    }

    public AutomationSensitiveItemStackHandler.IRemover getRemover() {
        return (slot, automation) -> !automation || slot > 0 && slot <= 110;
    }

    @Override
    public void switchTab(ServerPlayer player, ApiaryTab tab) {
        if (level != null && apiaryPos != null) {
            if (tab == ApiaryTab.MAIN) {
                BlockEntity tile = level.getBlockEntity(apiaryPos);
                NetworkHooks.openGui(player, (MenuProvider) tile, apiaryPos);
            }/* else if (tab == ApiaryTab.BREED) {
                BlockEntity tile = level.getBlockEntity(apiary.getBreederPos());
                NetworkHooks.openGui(player, (MenuProvider) tile, apiary.getBreederPos());
            }*/
        }
    }

    public int getNumberOfSlots() {
        return numberOfSlots;
    }

    public void setNumberOfSlots(int numberOfSlots) {
        this.numberOfSlots = numberOfSlots;
    }

    public @NotNull AutomationSensitiveItemStackHandler getItemStackHandler() {
        return itemStackHandler;
    }

    public LazyOptional<IItemHandler> getLazyOptional() {
        return lazyOptional;
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
            setChanged();
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
