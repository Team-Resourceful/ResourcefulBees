package com.resourcefulbees.resourcefulbees.tileentity.multiblocks.apiary;

import com.resourcefulbees.resourcefulbees.api.IBeeRegistry;
import com.resourcefulbees.resourcefulbees.api.ICustomBee;
import com.resourcefulbees.resourcefulbees.api.beedata.BreedData;
import com.resourcefulbees.resourcefulbees.api.beedata.CustomBeeData;
import com.resourcefulbees.resourcefulbees.config.Config;
import com.resourcefulbees.resourcefulbees.container.ApiaryStorageContainer;
import com.resourcefulbees.resourcefulbees.container.AutomationSensitiveItemStackHandler;
import com.resourcefulbees.resourcefulbees.item.BeeJar;
import com.resourcefulbees.resourcefulbees.item.UpgradeItem;
import com.resourcefulbees.resourcefulbees.lib.*;
import com.resourcefulbees.resourcefulbees.registry.BeeRegistry;
import com.resourcefulbees.resourcefulbees.registry.ModBlockEntityTypes;
import com.resourcefulbees.resourcefulbees.registry.ModItems;
import com.resourcefulbees.resourcefulbees.utils.BeeInfoUtils;
import com.resourcefulbees.resourcefulbees.utils.MathUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.TickableBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fml.network.NetworkHooks;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

import java.util.Arrays;
import java.util.List;

import static net.minecraft.world.inventory.AbstractContainerMenu.consideredTheSameItem;

public class ApiaryStorageTileEntity extends BlockEntity implements MenuProvider, TickableBlockEntity, IApiaryMultiblock {

    public static final int UPGRADE_SLOT = 0;
    private static final IBeeRegistry BEE_REGISTRY = BeeRegistry.getRegistry();
    private static final List<Integer> OUTPUT_AMOUNTS = Arrays.asList(Config.T1_APIARY_QUANTITY.get(), Config.T2_APIARY_QUANTITY.get(), Config.T3_APIARY_QUANTITY.get(), Config.T4_APIARY_QUANTITY.get());
    private static final List<ApiaryOutputs> OUTPUT_TYPES = Arrays.asList(Config.T1_APIARY_OUTPUT.get(), Config.T2_APIARY_OUTPUT.get(), Config.T3_APIARY_OUTPUT.get(), Config.T4_APIARY_OUTPUT.get());

    private BlockPos apiaryPos;
    private ApiaryTileEntity apiary;

    private int numberOfSlots = 9;

    private final AutomationSensitiveItemStackHandler itemStackHandler = new ApiaryStorageTileEntity.TileStackHandler(110);
    private final LazyOptional<IItemHandler> lazyOptional = LazyOptional.of(this::getItemStackHandler);

    public ApiaryStorageTileEntity() {
        super(ModBlockEntityTypes.APIARY_STORAGE_TILE_ENTITY.get());
    }

    @NotNull
    @Override
    public BlockEntityType<?> getType() {
        return ModBlockEntityTypes.APIARY_STORAGE_TILE_ENTITY.get();
    }

    @NotNull
    @Override
    public Component getDisplayName() {
        return new TranslatableComponent("gui.resourcefulbees.apiary_storage");
    }

    @Nullable
    @Override
    public ApiaryStorageContainer createMenu(int i, @NotNull Inventory playerInventory, @NotNull Player playerEntity) {
        if (level != null)
            return new ApiaryStorageContainer(i, level, worldPosition, playerInventory);
        return null;
    }

    @Override
    public void tick() {
        if (level != null && !level.isClientSide) {
            validateApiaryLink();
        }
    }

    public ApiaryTileEntity getApiary() {
        if (apiaryPos != null && level != null) {  //validate apiary first
            BlockEntity tile = level.getBlockEntity(apiaryPos); //get apiary pos
            if (tile instanceof ApiaryTileEntity) { //check tile is an apiary tile
                return (ApiaryTileEntity) tile;
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
                    count = (int) MathUtils.clamp(data.getFloat(NBTConstants.NBT_SLOT_UPGRADE), 1F, 108F);
                }
            }
        }

        setNumberOfSlots(count);
    }

    @Override
    public void load(@NotNull BlockState state, @NotNull CompoundTag nbt) {
        super.load(state, nbt);
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
    public void handleUpdateTag(@NotNull BlockState state, CompoundTag tag) {
        this.load(state, tag);
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
        CompoundTag nbtTagCompound = new CompoundTag();
        save(nbtTagCompound);
        return nbtTagCompound;
    }

    @Nullable
    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        CompoundTag nbt = new CompoundTag();
        if (apiaryPos != null)
            nbt.put(NBTConstants.NBT_APIARY_POS, NbtUtils.writeBlockPos(apiaryPos));
        return new ClientboundBlockEntityDataPacket(worldPosition, 0, nbt);
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
        CompoundTag nbt = pkt.getTag();
        if (nbt.contains(NBTConstants.NBT_APIARY_POS))
            apiaryPos = NbtUtils.readBlockPos(nbt.getCompound(NBTConstants.NBT_APIARY_POS));
    }

    public void deliverHoneycomb(Bee entity, int apiaryTier) {
        String beeType;
        if (entity instanceof ICustomBee && !((ICustomBee) entity).getHoneycombData().getHoneycombType().equals(HoneycombTypes.NONE)) {
            beeType = ((ICustomBee) entity).getBeeType();
        } else if (!(entity instanceof ICustomBee)) {
            beeType = BeeConstants.VANILLA_BEE_TYPE;
        } else {
            return;
        }


        ItemStack comb = beeType.equals(BeeConstants.VANILLA_BEE_TYPE) ? new ItemStack(Items.HONEYCOMB) : ((ICustomBee) entity).getHoneycombData().getHoneycomb().getDefaultInstance();
        ItemStack combBlock = beeType.equals(BeeConstants.VANILLA_BEE_TYPE) ? new ItemStack(Items.HONEYCOMB_BLOCK) : ((ICustomBee) entity).getHoneycombData().getHoneycombBlock().getDefaultInstance();
        List<Integer> outputAmounts = beeType.equals(BeeConstants.VANILLA_BEE_TYPE) ? null : ((ICustomBee) entity).getHoneycombData().getApiaryOutputAmounts();
        List<ApiaryOutputs> outputTypes = !beeType.equals(BeeConstants.VANILLA_BEE_TYPE) ? ((ICustomBee) entity).getHoneycombData().getApiaryOutputTypes() : OUTPUT_TYPES;

        depositItemStack(getApiaryOutput(apiaryTier, comb, combBlock, outputAmounts, outputTypes));
    }

    private ItemStack getApiaryOutput(int apiaryTier, ItemStack comb, ItemStack combBlock, List<Integer> outputAmounts, List<ApiaryOutputs> outputTypes) {
        ItemStack itemstack;
        int index = apiaryTier - 5;
        itemstack = (outputTypes.get(index) == ApiaryOutputs.BLOCK) ? combBlock : comb;
        itemstack.setCount(outputAmounts != null && outputAmounts.get(index) != -1 ? outputAmounts.get(index) : OUTPUT_AMOUNTS.get(index));
        return itemstack;
    }



    public boolean breedComplete(String p1, String p2) {
        if (inventoryHasSpace()) {
            CustomBeeData childBeeData = BEE_REGISTRY.getWeightedChild(p1, p2);
            float breedChance = BeeRegistry.getRegistry().getBreedChance(p1, p2, childBeeData.getBreedData());
            EntityType<?> entityType = BeeInfoUtils.getEntityType(childBeeData.getCoreData().getName());

            BreedData p1BreedData = BEE_REGISTRY.getBeeData(p1).getBreedData();
            BreedData p2BreedData = BEE_REGISTRY.getBeeData(p2).getBreedData();
            
            Item p1Returnable = p1BreedData.getFeedReturnItem().orElse(null);
            Item p2Returnable = p2BreedData.getFeedReturnItem().orElse(null);

            if (level != null && entityType != null) {
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
            } else if (consideredTheSameItem(itemstack, slotStack)) {
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
                if (playerentity.containerMenu instanceof ApiaryStorageContainer) {
                    ApiaryStorageContainer openContainer = (ApiaryStorageContainer) playerentity.containerMenu;
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
    public void switchTab(ServerPlayer player, ApiaryTabs tab) {
        if (level != null && apiaryPos != null) {
            if (tab == ApiaryTabs.MAIN) {
                BlockEntity tile = level.getBlockEntity(apiaryPos);
                NetworkHooks.openGui(player, (MenuProvider) tile, apiaryPos);
            } else if (tab == ApiaryTabs.BREED) {
                BlockEntity tile = level.getBlockEntity(apiary.getBreederPos());
                NetworkHooks.openGui(player, (MenuProvider) tile, apiary.getBreederPos());
            }
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
