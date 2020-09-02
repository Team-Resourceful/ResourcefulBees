package com.resourcefulbees.resourcefulbees.tileentity;

import com.resourcefulbees.resourcefulbees.config.BeeRegistry;
import com.resourcefulbees.resourcefulbees.config.Config;
import com.resourcefulbees.resourcefulbees.container.ApiaryBreederContainer;
import com.resourcefulbees.resourcefulbees.container.AutomationSensitiveItemStackHandler;
import com.resourcefulbees.resourcefulbees.entity.passive.CustomBeeEntity;
import com.resourcefulbees.resourcefulbees.item.BeeJar;
import com.resourcefulbees.resourcefulbees.item.UpgradeItem;
import com.resourcefulbees.resourcefulbees.lib.ApiaryTabs;
import com.resourcefulbees.resourcefulbees.lib.BeeConstants;
import com.resourcefulbees.resourcefulbees.lib.NBTConstants;
import com.resourcefulbees.resourcefulbees.recipe.CentrifugeRecipe;
import com.resourcefulbees.resourcefulbees.registry.RegistryHandler;
import com.resourcefulbees.resourcefulbees.utils.BeeInfoUtils;
import com.resourcefulbees.resourcefulbees.utils.BeeValidator;
import com.resourcefulbees.resourcefulbees.utils.MathUtils;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tags.ITag;
import net.minecraft.tags.ItemTags;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.IIntArray;
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

import static com.resourcefulbees.resourcefulbees.lib.NBTConstants.NBT_BREEDER_COUNT;

public class ApiaryBreederTileEntity extends TileEntity implements ITickableTileEntity, INamedContainerProvider, IApiaryMultiblock {

    public static final int[] UPGRADE_SLOTS = {0,1,2,3};
    public static final int[] PARENT_1_SLOTS = {4,9,14,19,24};
    public static final int[] FEED_1_SLOTS = {5,10,15,20,25};
    public static final int[] PARENT_2_SLOTS = {6,11,16,21,26};
    public static final int[] FEED_2_SLOTS = {7,12,17,22,27};
    public static final int[] EMPTY_JAR_SLOTS = {8,13,18,23,28};

    public AutomationSensitiveItemStackHandler h = new ApiaryBreederTileEntity.TileStackHandler(29);
    private final LazyOptional<IItemHandler> lazyOptional = LazyOptional.of(() -> h);
    public int[] time = {0,0,0,0,0};
    public CentrifugeRecipe recipe;
    public int totalTime = Config.APIARY_MAX_BREED_TIME.get();
    public int numberOfBreeders = 1;

    private BlockPos apiaryPos;
    private ApiaryTileEntity apiary;

    protected final IIntArray times = new IIntArray() {
        @Override
        public int get(int index) {
            switch (index) {
                case 0:
                    return ApiaryBreederTileEntity.this.time[0];
                case 1:
                    return ApiaryBreederTileEntity.this.time[1];
                case 2:
                    return ApiaryBreederTileEntity.this.time[2];
                case 3:
                    return ApiaryBreederTileEntity.this.time[3];
                case 4:
                    return ApiaryBreederTileEntity.this.time[4];
                default:
                    return 0;
            }
        }

        @Override
        public void set(int index, int value) {
            switch (index) {
                case 0:
                    ApiaryBreederTileEntity.this.time[0] = value;
                    break;
                case 1:
                    ApiaryBreederTileEntity.this.time[1] = value;
                    break;
                case 2:
                    ApiaryBreederTileEntity.this.time[2] = value;
                    break;
                case 3:
                    ApiaryBreederTileEntity.this.time[3] = value;
                    break;
                case 4:
                    ApiaryBreederTileEntity.this.time[4] = value;
            }
        }

        @Override
        public int size() {
            return 5;
        }
    };

    public ApiaryBreederTileEntity() {
        super(RegistryHandler.APIARY_BREEDER_TILE_ENTITY.get());
    }

    public BlockPos getApiaryPos() {
        return apiaryPos;
    }

    public void setApiaryPos(BlockPos apiaryPos) {
        this.apiaryPos = apiaryPos;
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

    public boolean validateApiaryLink() {
        apiary = getApiary();
        if (apiary == null || apiary.breederPos == null || !apiary.breederPos.equals(this.getPos()) || !apiary.isValidApiary()) { //check apiary has storage location equal to this and apiary is valid
            apiaryPos = null; //if not set these to null
            return false;
        }
        return true;
    }

    @Override
    public void tick() {
        if (world != null && !world.isRemote) {
            validateApiaryLink();
            boolean dirty = false;
            for (int i = 0; i < numberOfBreeders; i++){
                if(!h.getStackInSlot(PARENT_1_SLOTS[i]).isEmpty() && !h.getStackInSlot(FEED_1_SLOTS[i]).isEmpty()
                && !h.getStackInSlot(PARENT_2_SLOTS[i]).isEmpty() && !h.getStackInSlot(FEED_2_SLOTS[i]).isEmpty()
                && !h.getStackInSlot(EMPTY_JAR_SLOTS[i]).isEmpty()) {
                    if(canProcess(i)){
                        ++this.time[i];
                        if (this.time[i] >= this.totalTime) {
                            this.time[i] = 0;
                            this.processBreed(i);
                            dirty = true;
                        }
                    }
                } else {
                    this.time[i] = 0;
                }
                if (dirty){
                    this.markDirty();
                }
            }
        }
    }

    protected boolean canProcess(int slot) {
        ItemStack p1Stack = h.getStackInSlot(PARENT_1_SLOTS[slot]);
        ItemStack p2Stack = h.getStackInSlot(PARENT_2_SLOTS[slot]);
        if(p1Stack.getItem() instanceof BeeJar && p2Stack.getItem() instanceof BeeJar){
            BeeJar p1Jar = (BeeJar) p1Stack.getItem();
            BeeJar p2Jar = (BeeJar) p2Stack.getItem();

            Entity p1Entity = p1Jar.getEntityFromStack(p1Stack, world, true);
            Entity p2Entity = p2Jar.getEntityFromStack(p2Stack, world, true);

            if (p1Entity instanceof CustomBeeEntity && p2Entity instanceof CustomBeeEntity){
                String p1Type = ((CustomBeeEntity) p1Entity).getBeeInfo().getName();
                String p2Type = ((CustomBeeEntity) p2Entity).getBeeInfo().getName();

                boolean canBreed = BeeRegistry.canParentsBreed(p1Type, p2Type);

                ItemStack f1Stack = h.getStackInSlot(FEED_1_SLOTS[slot]);
                ItemStack f2Stack = h.getStackInSlot(FEED_2_SLOTS[slot]);

                String p1FeedItem = ((CustomBeeEntity) p1Entity).getBeeInfo().BreedData.getFeedItem();
                String p2FeedItem = ((CustomBeeEntity) p2Entity).getBeeInfo().BreedData.getFeedItem();

                int f1StackCount = h.getStackInSlot(FEED_1_SLOTS[slot]).getCount();
                int f2StackCount = h.getStackInSlot(FEED_2_SLOTS[slot]).getCount();

                int p1FeedAmount = ((CustomBeeEntity) p1Entity).getBeeInfo().BreedData.getFeedAmount();
                int p2FeedAmount = ((CustomBeeEntity) p2Entity).getBeeInfo().BreedData.getFeedAmount();

                return (canBreed && stackIsValidBreedItem(f1Stack, p1FeedItem) && stackIsValidBreedItem(f2Stack, p2FeedItem)
                && f1StackCount >= p1FeedAmount && f2StackCount >= p2FeedAmount && !h.getStackInSlot(EMPTY_JAR_SLOTS[slot]).isEmpty());
            }
        }
        return false;
    }

    private boolean stackIsValidBreedItem(ItemStack stack, String validBreedItem){
        if (BeeValidator.TAG_RESOURCE_PATTERN.matcher(validBreedItem).matches()) {
            ITag<Item> itemTag = BeeInfoUtils.getItemTag(validBreedItem.replace(BeeConstants.TAG_PREFIX, ""));
            return itemTag != null && stack.getItem().isIn(itemTag);
        } else {
            switch (validBreedItem) {
                case BeeConstants.FLOWER_TAG_ALL:
                    return stack.getItem().isIn(ItemTags.FLOWERS);
                case BeeConstants.FLOWER_TAG_SMALL:
                    return stack.getItem().isIn(ItemTags.SMALL_FLOWERS);
                case BeeConstants.FLOWER_TAG_TALL:
                    return stack.getItem().isIn(ItemTags.TALL_FLOWERS);
                default:
                    return stack.getItem().equals(BeeInfoUtils.getItem(validBreedItem));
            }
        }
    }

    private void processBreed(int slot) {
        if (canProcess(slot)) {
            ItemStack p1Stack = h.getStackInSlot(PARENT_1_SLOTS[slot]);
            ItemStack p2Stack = h.getStackInSlot(PARENT_2_SLOTS[slot]);
            if (p1Stack.getItem() instanceof BeeJar && p2Stack.getItem() instanceof BeeJar) {
                BeeJar p1Jar = (BeeJar) p1Stack.getItem();
                BeeJar p2Jar = (BeeJar) p2Stack.getItem();

                Entity p1Entity = p1Jar.getEntityFromStack(p1Stack, world, true);
                Entity p2Entity = p2Jar.getEntityFromStack(p2Stack, world, true);

                if (p1Entity instanceof CustomBeeEntity && p2Entity instanceof CustomBeeEntity) {
                    CustomBeeEntity bee1 = (CustomBeeEntity) p1Entity;
                    CustomBeeEntity bee2 = (CustomBeeEntity) p2Entity;

                    String p1Type = bee1.getBeeInfo().getName();
                    String p2Type = bee2.getBeeInfo().getName();

                    if (world != null && validateApiaryLink()){
                        TileEntity tile = world.getTileEntity(apiary.storagePos);
                        if (tile instanceof ApiaryStorageTileEntity){
                            ApiaryStorageTileEntity apiaryStorage = (ApiaryStorageTileEntity) tile;
                            if (apiaryStorage.breedComplete(p1Type, p2Type)){
                                h.getStackInSlot(EMPTY_JAR_SLOTS[slot]).shrink(1);
                                h.getStackInSlot(FEED_1_SLOTS[slot]).shrink(bee1.getBeeInfo().BreedData.getFeedAmount());
                                h.getStackInSlot(FEED_2_SLOTS[slot]).shrink(bee2.getBeeInfo().BreedData.getFeedAmount());
                            }
                        }
                    }
                }
            }
        }
        this.time[slot] = 0;
    }

    private void rebuildOpenContainers() {
        if (world != null) {
            float f = 5.0F;
            BlockPos pos = this.pos;

            for (PlayerEntity playerentity : world.getEntitiesWithinAABB(PlayerEntity.class, new AxisAlignedBB(pos.getX() - f, pos.getY() - f, pos.getZ() - f, (pos.getX() + 1) + f, (pos.getY() + 1) + f, (pos.getZ() + 1) + f))) {
                if (playerentity.openContainer instanceof ApiaryBreederContainer) {
                    ApiaryBreederContainer openContainer = (ApiaryBreederContainer) playerentity.openContainer;
                    ApiaryBreederTileEntity apiaryBreederTileEntity = openContainer.apiaryBreederTileEntity;
                    if (apiaryBreederTileEntity == this) {
                        openContainer.setupSlots(true);
                    }
                }
            }
        }
    }

    private void updateNumberOfBreeders() {
        int count = 1;
        for (int i = 0; i < 4; i++) {
            if (!h.getStackInSlot(UPGRADE_SLOTS[i]).isEmpty()) {
                ItemStack upgradeItem = h.getStackInSlot(UPGRADE_SLOTS[i]);
                if (UpgradeItem.isUpgradeItem(upgradeItem)) {
                    CompoundNBT data = UpgradeItem.getUpgradeData(upgradeItem);

                    if (data != null && data.getString(NBTConstants.NBT_UPGRADE_TYPE).equals(NBTConstants.NBT_BREEDER_UPGRADE)) {
                        count += (int) MathUtils.clamp(data.getFloat(NBTConstants.NBT_BREEDER_COUNT), 0F, 5);
                    }
                }
            }
        }

        numberOfBreeders = count;
    }

    private void updateBreedTime() {
        int totalTime = Config.APIARY_MAX_BREED_TIME.get();
        for (int i = 0; i < 4; i++) {
            if (!h.getStackInSlot(UPGRADE_SLOTS[i]).isEmpty()) {
                ItemStack upgradeItem = h.getStackInSlot(UPGRADE_SLOTS[i]);
                if (UpgradeItem.isUpgradeItem(upgradeItem)) {
                    CompoundNBT data = UpgradeItem.getUpgradeData(upgradeItem);

                    if (data != null && data.getString(NBTConstants.NBT_UPGRADE_TYPE).equals(NBTConstants.NBT_BREEDER_UPGRADE)) {
                        totalTime -= (int) MathUtils.clamp(data.getFloat(NBTConstants.NBT_BREED_TIME), 100, 600);
                    }
                }
            }
        }

        this.totalTime = MathUtils.clamp(totalTime, 300, 4800);
    }



    @Override
    public void deserializeNBT(@Nonnull BlockState state, @Nonnull CompoundNBT nbt) {
        super.deserializeNBT(state, nbt);
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
        time = nbt.getIntArray("time");
        totalTime = nbt.getInt("totalTime");
        if (nbt.contains(NBTConstants.NBT_APIARY_POS))
            apiaryPos = NBTUtil.readBlockPos(nbt.getCompound(NBTConstants.NBT_APIARY_POS));
        if (nbt.contains(NBT_BREEDER_COUNT))
            this.numberOfBreeders = nbt.getInt(NBT_BREEDER_COUNT);
    }

    public CompoundNBT saveToNBT(CompoundNBT nbt) {
        CompoundNBT inv = this.h.serializeNBT();
        nbt.put(NBTConstants.NBT_INVENTORY, inv);
        nbt.putIntArray("time", time);
        nbt.putInt("totalTime", totalTime);
        if (apiaryPos != null)
            nbt.put(NBTConstants.NBT_APIARY_POS, NBTUtil.writeBlockPos(apiaryPos));
        if (numberOfBreeders != 1) {
            nbt.putInt(NBT_BREEDER_COUNT, numberOfBreeders);
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
        this.deserializeNBT(state, tag);
    }

    @Nullable
    @Override
    public SUpdateTileEntityPacket getUpdatePacket() {
        CompoundNBT nbt = new CompoundNBT();
/*        nbt.putIntArray("time", time);
        nbt.putInt("totalTime", totalTime);
        if (apiaryPos != null)
            nbt.put(NBTConstants.NBT_APIARY_POS, NBTUtil.writeBlockPos(apiaryPos));*/
        return new SUpdateTileEntityPacket(pos,0, saveToNBT(nbt));
    }

    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
        CompoundNBT nbt = pkt.getNbtCompound();
        loadFromNBT(nbt);
        /*if (nbt.contains(NBTConstants.NBT_APIARY_POS))
            apiaryPos = NBTUtil.readBlockPos(nbt.getCompound(NBTConstants.NBT_APIARY_POS));*/
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (cap.equals(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)) return lazyOptional.cast();
        return super.getCapability(cap, side);
    }

    public AutomationSensitiveItemStackHandler.IAcceptor getAcceptor() {
        return (slot, stack, automation) -> !automation || (slot == EMPTY_JAR_SLOTS[0] || slot == EMPTY_JAR_SLOTS[1] || slot == EMPTY_JAR_SLOTS[2] || slot == EMPTY_JAR_SLOTS[3] && stack.getItem().equals(RegistryHandler.BEE_JAR.get()));
    }

    public AutomationSensitiveItemStackHandler.IRemover getRemover() {
        return (slot, automation) -> !automation || (slot == EMPTY_JAR_SLOTS[0] || slot == EMPTY_JAR_SLOTS[1] || slot == EMPTY_JAR_SLOTS[2] || slot == EMPTY_JAR_SLOTS[3]);
    }

    @Nullable
    @Override
    public Container createMenu(int id, @Nonnull PlayerInventory playerInventory, @Nonnull PlayerEntity playerEntity) {
        //noinspection ConstantConditions
        return new ApiaryBreederContainer(id, world, pos, playerInventory, times);
    }

    @Nonnull
    @Override
    public ITextComponent getDisplayName() {
        return new TranslationTextComponent("gui.resourcefulbees.apiary_breeder");
    }

    @Override
    public void switchTab(ServerPlayerEntity player, ApiaryTabs tab) {
        if (world != null && apiaryPos != null) {
            if (tab == ApiaryTabs.MAIN) {
                TileEntity tile = world.getTileEntity(apiaryPos);
                NetworkHooks.openGui(player, (INamedContainerProvider) tile, apiaryPos);
            }
            if (tab == ApiaryTabs.STORAGE) {
                TileEntity tile = world.getTileEntity(apiary.storagePos);
                NetworkHooks.openGui(player, (INamedContainerProvider) tile, apiary.storagePos);
            }
        }
    }

    protected class TileStackHandler extends AutomationSensitiveItemStackHandler {
        protected TileStackHandler(int slots) {
            super(slots);
        }

        @Override
        public AutomationSensitiveItemStackHandler.IAcceptor getAcceptor() {
            return ApiaryBreederTileEntity.this.getAcceptor();
        }

        @Override
        public AutomationSensitiveItemStackHandler.IRemover getRemover() {
            return ApiaryBreederTileEntity.this.getRemover();
        }

        @Override
        protected void onContentsChanged(int slot) {
            super.onContentsChanged(slot);
            markDirty();

            for (int i=0; i<4; i++){
                if (slot == UPGRADE_SLOTS[i]) {
                    updateNumberOfBreeders();
                    rebuildOpenContainers();
                    updateBreedTime();
                    break;
                }
            }
        }

        @Override
        public int getSlotLimit(int slot) {
            for (int i = 0; i < 4; i++) {
                if (slot == UPGRADE_SLOTS[i]) {
                    return 1;
                }
            }
            return super.getSlotLimit(slot);
        }
    }
}
