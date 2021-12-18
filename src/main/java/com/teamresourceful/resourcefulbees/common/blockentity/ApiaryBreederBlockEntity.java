package com.teamresourceful.resourcefulbees.common.blockentity;

import com.teamresourceful.resourcefulbees.api.ICustomBee;
import com.teamresourceful.resourcefulbees.api.beedata.breeding.BreedData;
import com.teamresourceful.resourcefulbees.common.config.CommonConfig;
import com.teamresourceful.resourcefulbees.common.entity.passive.CustomBeeEntity;
import com.teamresourceful.resourcefulbees.common.inventory.AutomationSensitiveItemStackHandler;
import com.teamresourceful.resourcefulbees.common.inventory.menus.ApiaryBreederContainer;
import com.teamresourceful.resourcefulbees.common.item.BeeJar;
import com.teamresourceful.resourcefulbees.common.item.UpgradeItem;
import com.teamresourceful.resourcefulbees.common.lib.constants.NBTConstants;
import com.teamresourceful.resourcefulbees.common.lib.constants.TranslationConstants;
import com.teamresourceful.resourcefulbees.common.registry.custom.BeeRegistry;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.ModBlockEntityTypes;
import com.teamresourceful.resourcefulbees.common.utils.BeeInfoUtils;
import com.teamresourceful.resourcefulbees.common.utils.MathUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static com.teamresourceful.resourcefulbees.common.lib.constants.NBTConstants.NBT_BREEDER_COUNT;

public class ApiaryBreederBlockEntity extends GUISyncedBlockEntity {

    private static final int[] UPGRADE_SLOTS = {0, 1, 2, 3};
    private static final int[] PARENT_1_SLOTS = {4, 9, 14, 19, 24};
    private static final int[] FEED_1_SLOTS = {5, 10, 15, 20, 25};
    private static final int[] PARENT_2_SLOTS = {6, 11, 16, 21, 26};
    private static final int[] FEED_2_SLOTS = {7, 12, 17, 22, 27};
    private static final int[] EMPTY_JAR_SLOTS = {8, 13, 18, 23, 28};

    private final ApiaryBreederBlockEntity.TileStackHandler tileStackHandler = new ApiaryBreederBlockEntity.TileStackHandler(29);
    private final LazyOptional<IItemHandler> lazyOptional = LazyOptional.of(this::getTileStackHandler);
    private int[] time = {0, 0, 0, 0, 0};
    private int totalTime = CommonConfig.APIARY_MAX_BREED_TIME.get();
    private int numberOfBreeders = 1;

    protected final ContainerData times = new ContainerData() {
        @Override
        public int get(int index) {
            return MathUtils.inRangeInclusive(index, 0, 4)
                    ? ApiaryBreederBlockEntity.this.getTime()[index]
                    : 0;
        }

        @Override
        public void set(int index, int value) {
            if (!MathUtils.inRangeInclusive(index, 0, 4)) return;
            ApiaryBreederBlockEntity.this.getTime()[index] = value;
         }

        @Override
        public int getCount() {
            return 5;
        }
    };

    public ApiaryBreederBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityTypes.APIARY_BREEDER_TILE_ENTITY.get(), pos, state);
    }

    public static int[] getUpgradeSlots() {
        return UPGRADE_SLOTS;
    }

    public static int[] getParent1Slots() {
        return PARENT_1_SLOTS;
    }

    public static int[] getFeed1Slots() {
        return FEED_1_SLOTS;
    }

    public static int[] getParent2Slots() {
        return PARENT_2_SLOTS;
    }

    public static int[] getFeed2Slots() {
        return FEED_2_SLOTS;
    }

    public static int[] getEmptyJarSlots() {
        return EMPTY_JAR_SLOTS;
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, ApiaryBreederBlockEntity entity) {
        if (true) return;
        boolean dirty = false;
        for (int i = 0; i < entity.getNumberOfBreeders(); i++) {
            if (entity.allBreedSlotsAreFilled(i)) {
                if (entity.canProcess(i)) {
                    ++entity.getTime()[i];
                    if (entity.getTime()[i] >= entity.getTotalTime()) {
                        entity.getTime()[i] = 0;
                        entity.processBreed(i);
                        dirty = true;
                    }
                }
            } else {
                entity.getTime()[i] = 0;
            }
            if (dirty) {
                entity.setChanged();
            }
        }
    }

    private boolean allBreedSlotsAreFilled(int i) {
        return slotIsFilled(getParent1Slots()[i]) && slotIsFilled(getFeed1Slots()[i]) && slotIsFilled(getParent2Slots()[i])
                && slotIsFilled(getFeed2Slots()[i]) && slotIsFilled(getEmptyJarSlots()[i]);
    }

    private boolean slotIsFilled(int slot) {
        return !getTileStackHandler().getStackInSlot(slot).isEmpty();
    }

    protected boolean canProcess(int slot) {
        ItemStack p1Stack = getTileStackHandler().getStackInSlot(getParent1Slots()[slot]);
        ItemStack p2Stack = getTileStackHandler().getStackInSlot(getParent2Slots()[slot]);
        if (p1Stack.getItem() instanceof BeeJar && p2Stack.getItem() instanceof BeeJar) {
            Entity p1Entity = BeeJar.getEntityFromStack(p1Stack, level, true);
            Entity p2Entity = BeeJar.getEntityFromStack(p2Stack, level, true);

            if (p1Entity instanceof CustomBeeEntity && p2Entity instanceof CustomBeeEntity) {
                String p1Type = ((CustomBeeEntity) p1Entity).getBeeType();
                String p2Type = ((CustomBeeEntity) p2Entity).getBeeType();

                boolean canBreed = BeeRegistry.getRegistry().canParentsBreed(p1Type, p2Type);

                ItemStack f1Stack = getTileStackHandler().getStackInSlot(getFeed1Slots()[slot]);
                ItemStack f2Stack = getTileStackHandler().getStackInSlot(getFeed2Slots()[slot]);

                BreedData p1BreedData = ((CustomBeeEntity) p1Entity).getBreedData();
                BreedData p2BreedData = ((CustomBeeEntity) p2Entity).getBreedData();

                int f1StackCount = getTileStackHandler().getStackInSlot(getFeed1Slots()[slot]).getCount();
                int f2StackCount = getTileStackHandler().getStackInSlot(getFeed2Slots()[slot]).getCount();

                int p1FeedAmount = ((CustomBeeEntity) p1Entity).getBreedData().getFeedAmount();
                int p2FeedAmount = ((CustomBeeEntity) p2Entity).getBreedData().getFeedAmount();

                return (canBreed && p1BreedData.getFeedItems().contains(f1Stack.getItem()) && p2BreedData.getFeedItems().contains(f2Stack.getItem())
                        && f1StackCount >= p1FeedAmount && f2StackCount >= p2FeedAmount && !getTileStackHandler().getStackInSlot(getEmptyJarSlots()[slot]).isEmpty());
            }
        }
        return false;
    }

    private void processBreed(int slot) {
        if (canProcess(slot)) {
            ItemStack p1Stack = getTileStackHandler().getStackInSlot(getParent1Slots()[slot]);
            ItemStack p2Stack = getTileStackHandler().getStackInSlot(getParent2Slots()[slot]);
            if (p1Stack.getItem() instanceof BeeJar && p2Stack.getItem() instanceof BeeJar) {
                Entity p1Entity = BeeJar.getEntityFromStack(p1Stack, level, true);
                Entity p2Entity = BeeJar.getEntityFromStack(p2Stack, level, true);

                if (p1Entity instanceof ICustomBee bee1 && p2Entity instanceof ICustomBee bee2) {

                    String p1Type = bee1.getBeeType();
                    String p2Type = bee2.getBeeType();

                    //TODO Finish splitting breeder from apiary - oreo
/*                    if (level != null && validateApiaryLink()) {
                        TileEntity tile = level.getBlockEntity(apiary.getStoragePos());
                        if (tile instanceof ApiaryStorageTileEntity) {
                            ApiaryStorageTileEntity apiaryStorage = (ApiaryStorageTileEntity) tile;
                            if (apiaryStorage.breedComplete(p1Type, p2Type)) {
                                getTileStackHandler().getStackInSlot(getEmptyJarSlots()[slot]).shrink(1);
                                getTileStackHandler().getStackInSlot(getFeed1Slots()[slot]).shrink(bee1.getBreedData().getFeedAmount());
                                getTileStackHandler().getStackInSlot(getFeed2Slots()[slot]).shrink(bee2.getBreedData().getFeedAmount());
                            }
                        }
                    }*/
                }
            }
        }
        this.getTime()[slot] = 0;
    }

    @NotNull
    @Override
    public CompoundTag save(@NotNull CompoundTag nbt) {
        super.save(nbt);
        return this.saveToNBT(nbt);
    }

    public CompoundTag saveToNBT(CompoundTag nbt) {
        CompoundTag inv = this.getTileStackHandler().serializeNBT();
        nbt.put(NBTConstants.NBT_INVENTORY, inv);
        nbt.putIntArray("time", getTime());
        nbt.putInt("totalTime", getTotalTime());
        if (getNumberOfBreeders() != 1) {
            nbt.putInt(NBT_BREEDER_COUNT, getNumberOfBreeders());
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

    @Override
    public void load(@NotNull CompoundTag nbt) {
        super.load(nbt);
        this.loadFromNBT(nbt);
    }

    public void loadFromNBT(CompoundTag nbt) {
        CompoundTag invTag = nbt.getCompound(NBTConstants.NBT_INVENTORY);
        getTileStackHandler().deserializeNBT(invTag);
        setTime(nbt.getIntArray("time"));
        setTotalTime(nbt.getInt("totalTime"));
        if (nbt.contains(NBT_BREEDER_COUNT))
            this.setNumberOfBreeders(nbt.getInt(NBT_BREEDER_COUNT));
    }

    @Override
    public void handleUpdateTag(CompoundTag tag) {
        this.load(tag);
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
        CompoundTag nbt = pkt.getTag();
        if (nbt != null) loadFromNBT(nbt);
    }

    @NotNull
    @Override
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap.equals(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)) return lazyOptional.cast();
        return super.getCapability(cap, side);
    }

    public AutomationSensitiveItemStackHandler.IAcceptor getAcceptor() {
        return (slot, stack, automation) -> !automation || slot > 3;
    }

    public AutomationSensitiveItemStackHandler.IRemover getRemover() {
        return (slot, automation) -> !automation || slot > 3;
    }

    @Override
    public AbstractContainerMenu createMenu(int id, @NotNull Inventory playerInventory, @NotNull Player playerEntity) {
        return new ApiaryBreederContainer(id, playerInventory, this, times);
    }

    @NotNull
    @Override
    public Component getDisplayName() {
        return TranslationConstants.Guis.APIARY_BREEDER;
    }

    public int[] getTime() {
        return time;
    }

    public void setTime(int[] time) {
        this.time = time;
    }

    public int getTotalTime() {
        return totalTime;
    }

    public void setTotalTime(int totalTime) {
        this.totalTime = totalTime;
    }

    public int getNumberOfBreeders() {
        return numberOfBreeders;
    }

    public void setNumberOfBreeders(int numberOfBreeders) {
        this.numberOfBreeders = numberOfBreeders;
    }

    public @NotNull TileStackHandler getTileStackHandler() {
        return tileStackHandler;
    }

    @Override
    public CompoundTag getSyncData() {
        return null;
    }

    @Override
    public void readSyncData(@NotNull CompoundTag tag) {

    }

    public class TileStackHandler extends AutomationSensitiveItemStackHandler {

        private int maxSlots = 8;

        public void setMaxSlots(int maxSlots) {
            this.maxSlots = maxSlots;
        }

        protected TileStackHandler(int slots) {
            super(slots);
        }

        @Override
        public AutomationSensitiveItemStackHandler.IAcceptor getAcceptor() {
            return ApiaryBreederBlockEntity.this.getAcceptor();
        }

        @Override
        public AutomationSensitiveItemStackHandler.IRemover getRemover() {
            return ApiaryBreederBlockEntity.this.getRemover();
        }

        @Override
        protected void onContentsChanged(int slot) {
            super.onContentsChanged(slot);
            setChanged();

            for (int i = 0; i < 4; i++) {
                if (slot == getUpgradeSlots()[i]) {
                    tileStackHandler.updateNumberOfBreeders(this);
                    tileStackHandler.rebuildOpenContainers();
                    tileStackHandler.updateBreedTime(this);
                    break;
                }
            }
        }

        @Override
        public int getSlotLimit(int slot) {
            return switch (slot) {
                case 0, 1, 2, 3, 4, 9, 14, 19, 24, 6, 11, 16, 21, 26 -> /*parent slots*/ 1;
                default -> 64;
            };
        }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            return switch (slot) {
                case 0, 1, 2, 3 -> /* upgrade slots */
                        UpgradeItem.hasUpgradeData(stack) && (UpgradeItem.getUpgradeType(stack).contains(NBTConstants.NBT_BREEDER_UPGRADE));
                //Parent 1 Bee jars
                //Parent 2 Bee jars
                case 4, 9, 14, 19, 24, 6, 11, 16, 21, 26 ->
                        //parent slots
                        isSlotVisible(slot) && BeeInfoUtils.isBeeInJarOurs(stack);
                //Parent 1 Feed Items
                //Parent 2 Feed Items
                case 5, 10, 15, 20, 25, 7, 12, 17, 22, 27 ->
                        // feed slots
                        isSlotVisible(slot) && !(stack.getItem() instanceof BeeJar);
                case 8, 13, 18, 23, 28 ->
                        // jar slots
                        isSlotVisible(slot) && stack.getItem() instanceof BeeJar && !BeeJar.isFilled(stack);
                default -> false;
            };
        }

        private boolean isSlotVisible(int slot) {
            return slot <= maxSlots;
        }

        private void updateNumberOfBreeders(TileStackHandler tileStackHandler) {
            int count = 1;
            for (int i = 0; i < 4; i++) {
                if (!tileStackHandler.getStackInSlot(getUpgradeSlots()[i]).isEmpty()) {
                    ItemStack upgradeItem = tileStackHandler.getStackInSlot(getUpgradeSlots()[i]);
                    if (UpgradeItem.isUpgradeItem(upgradeItem)) {
                        CompoundTag data = UpgradeItem.getUpgradeData(upgradeItem);

                        if (data != null && data.getString(NBTConstants.NBT_UPGRADE_TYPE).equals(NBTConstants.NBT_BREEDER_UPGRADE)) {
                            count += (int) Mth.clamp(data.getFloat(NBTConstants.NBT_BREEDER_COUNT), 0F, 5);
                        }
                    }
                }
            }

            ApiaryBreederBlockEntity.this.setNumberOfBreeders(count);
            tileStackHandler.setMaxSlots(3 + ApiaryBreederBlockEntity.this.getNumberOfBreeders() * 5);
        }

        private void updateBreedTime(TileStackHandler tileStackHandler) {
            int newTotalTime = CommonConfig.APIARY_MAX_BREED_TIME.get();
            for (int i = 0; i < 4; i++) {
                if (!tileStackHandler.getStackInSlot(getUpgradeSlots()[i]).isEmpty()) {
                    ItemStack upgradeItem = tileStackHandler.getStackInSlot(getUpgradeSlots()[i]);
                    if (UpgradeItem.isUpgradeItem(upgradeItem)) {
                        CompoundTag data = UpgradeItem.getUpgradeData(upgradeItem);

                        if (data != null && data.getString(NBTConstants.NBT_UPGRADE_TYPE).equals(NBTConstants.NBT_BREEDER_UPGRADE)) {
                            newTotalTime -= (int) Mth.clamp(data.getFloat(NBTConstants.NBT_BREED_TIME), 100, 600);
                        }
                    }
                }
            }

            ApiaryBreederBlockEntity.this.setTotalTime(Mth.clamp(newTotalTime, 300, 4800));
        }

        private void rebuildOpenContainers() {
            if (ApiaryBreederBlockEntity.this.level != null) {
                float f = 5.0F;
                BlockPos pos = ApiaryBreederBlockEntity.this.worldPosition;

                ApiaryBreederBlockEntity.this.level.getEntitiesOfClass(Player.class, new AABB(pos.getX() - f, pos.getY() - f, pos.getZ() - f, (pos.getX() + 1) + f, (pos.getY() + 1) + f, (pos.getZ() + 1) + f))
                        .stream()
                        .filter(playerEntity -> playerEntity.containerMenu instanceof ApiaryBreederContainer)
                        .filter(this::openContainerMatches)
                        .forEach(playerEntity -> ((ApiaryBreederContainer) playerEntity.containerMenu).setupSlots(true));
            }
        }

        private boolean openContainerMatches(Player playerEntity) {
            ApiaryBreederContainer openContainer = (ApiaryBreederContainer) playerEntity.containerMenu;
            ApiaryBreederBlockEntity apiaryBreederBlockEntity = openContainer.getEntity();
            return  ApiaryBreederBlockEntity.this == apiaryBreederBlockEntity;
        }
    }
}
