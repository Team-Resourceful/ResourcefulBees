package com.teamresourceful.resourcefulbees.common.tileentity.multiblocks.apiary;

import com.teamresourceful.resourcefulbees.api.ICustomBee;
import com.teamresourceful.resourcefulbees.api.beedata.breeding.BreedData;
import com.teamresourceful.resourcefulbees.common.config.CommonConfig;
import com.teamresourceful.resourcefulbees.common.inventory.containers.ApiaryBreederContainer;
import com.teamresourceful.resourcefulbees.common.inventory.AutomationSensitiveItemStackHandler;
import com.teamresourceful.resourcefulbees.common.entity.passive.CustomBeeEntity;
import com.teamresourceful.resourcefulbees.common.item.BeeJar;
import com.teamresourceful.resourcefulbees.common.item.UpgradeItem;
import com.teamresourceful.resourcefulbees.common.lib.constants.NBTConstants;
import com.teamresourceful.resourcefulbees.common.registry.custom.BeeRegistry;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.ModBlockEntityTypes;
import com.teamresourceful.resourcefulbees.common.utils.BeeInfoUtils;
import com.teamresourceful.resourcefulbees.common.utils.MathUtils;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.IIntArray;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static com.teamresourceful.resourcefulbees.common.lib.constants.NBTConstants.NBT_BREEDER_COUNT;

public class ApiaryBreederTileEntity extends TileEntity implements ITickableTileEntity, INamedContainerProvider {

    private static final int[] UPGRADE_SLOTS = {0, 1, 2, 3};
    private static final int[] PARENT_1_SLOTS = {4, 9, 14, 19, 24};
    private static final int[] FEED_1_SLOTS = {5, 10, 15, 20, 25};
    private static final int[] PARENT_2_SLOTS = {6, 11, 16, 21, 26};
    private static final int[] FEED_2_SLOTS = {7, 12, 17, 22, 27};
    private static final int[] EMPTY_JAR_SLOTS = {8, 13, 18, 23, 28};

    private final ApiaryBreederTileEntity.TileStackHandler tileStackHandler = new ApiaryBreederTileEntity.TileStackHandler(29);
    private final LazyOptional<IItemHandler> lazyOptional = LazyOptional.of(this::getTileStackHandler);
    private int[] time = {0, 0, 0, 0, 0};
    private int totalTime = CommonConfig.APIARY_MAX_BREED_TIME.get();
    private int numberOfBreeders = 1;

    protected final IIntArray times = new IIntArray() {
        @Override
        public int get(int index) {
            return MathUtils.inRangeInclusive(index, 0, 4)
                    ? ApiaryBreederTileEntity.this.getTime()[index]
                    : 0;
        }

        @Override
        public void set(int index, int value) {
            if (!MathUtils.inRangeInclusive(index, 0, 4)) return;
            ApiaryBreederTileEntity.this.getTime()[index] = value;
         }

        @Override
        public int getCount() {
            return 5;
        }
    };

    public ApiaryBreederTileEntity() {
        super(ModBlockEntityTypes.APIARY_BREEDER_TILE_ENTITY.get());
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

    @Override
    public void tick() {
        if (level != null && !level.isClientSide) {
            boolean dirty = false;
            for (int i = 0; i < getNumberOfBreeders(); i++) {
                if (allBreedSlotsAreFilled(i)) {
                    if (canProcess(i)) {
                        ++this.getTime()[i];
                        if (this.getTime()[i] >= this.getTotalTime()) {
                            this.getTime()[i] = 0;
                            this.processBreed(i);
                            dirty = true;
                        }
                    }
                } else {
                    this.getTime()[i] = 0;
                }
                if (dirty) {
                    this.setChanged();
                }
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

                if (p1Entity instanceof ICustomBee && p2Entity instanceof ICustomBee) {
                    ICustomBee bee1 = (ICustomBee) p1Entity;
                    ICustomBee bee2 = (ICustomBee) p2Entity;

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
    public CompoundNBT save(@NotNull CompoundNBT nbt) {
        super.save(nbt);
        return this.saveToNBT(nbt);
    }

    public CompoundNBT saveToNBT(CompoundNBT nbt) {
        CompoundNBT inv = this.getTileStackHandler().serializeNBT();
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
    public CompoundNBT getUpdateTag() {
        CompoundNBT nbtTagCompound = new CompoundNBT();
        save(nbtTagCompound);
        return nbtTagCompound;
    }

    @Override
    public void load(@NotNull BlockState state, @NotNull CompoundNBT nbt) {
        super.load(state, nbt);
        this.loadFromNBT(nbt);
    }

    public void loadFromNBT(CompoundNBT nbt) {
        CompoundNBT invTag = nbt.getCompound(NBTConstants.NBT_INVENTORY);
        getTileStackHandler().deserializeNBT(invTag);
        setTime(nbt.getIntArray("time"));
        setTotalTime(nbt.getInt("totalTime"));
        if (nbt.contains(NBT_BREEDER_COUNT))
            this.setNumberOfBreeders(nbt.getInt(NBT_BREEDER_COUNT));
    }

    @Override
    public void handleUpdateTag(@NotNull BlockState state, CompoundNBT tag) {
        this.load(state, tag);
    }

    @Override
    public SUpdateTileEntityPacket getUpdatePacket() {
        CompoundNBT nbt = new CompoundNBT();
        return new SUpdateTileEntityPacket(worldPosition, 0, saveToNBT(nbt));
    }

    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
        CompoundNBT nbt = pkt.getTag();
        loadFromNBT(nbt);
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
    public Container createMenu(int id, @NotNull PlayerInventory playerInventory, @NotNull PlayerEntity playerEntity) {
        //noinspection ConstantConditions
        return new ApiaryBreederContainer(id, level, worldPosition, playerInventory, times);
    }

    @NotNull
    @Override
    public ITextComponent getDisplayName() {
        return new TranslationTextComponent("gui.resourcefulbees.apiary_breeder");
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
            return ApiaryBreederTileEntity.this.getAcceptor();
        }

        @Override
        public AutomationSensitiveItemStackHandler.IRemover getRemover() {
            return ApiaryBreederTileEntity.this.getRemover();
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
            switch (slot) {
                case 0:
                case 1:
                case 2:
                case 3:
                case 4:
                case 9:
                case 14:
                case 19:
                case 24:
                case 6:
                case 11:
                case 16:
                case 21:
                case 26:
                    //parent slots
                    return 1;
                default:
                    return 64;
            }
        }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            switch (slot) {
                case 0:
                case 1:
                case 2:
                case 3:
                    // upgrade slots
                    return UpgradeItem.hasUpgradeData(stack) && (UpgradeItem.getUpgradeType(stack).contains(NBTConstants.NBT_BREEDER_UPGRADE));
                //Parent 1 Bee jars
                case 4:
                case 9:
                case 14:
                case 19:
                case 24:
                //Parent 2 Bee jars
                case 6:
                case 11:
                case 16:
                case 21:
                case 26:
                    //parent slots
                    return isSlotVisible(slot) && BeeInfoUtils.isBeeInJarOurs(stack);
                //Parent 1 Feed Items
                case 5:
                case 10:
                case 15:
                case 20:
                case 25:
                //Parent 2 Feed Items
                case 7:
                case 12:
                case 17:
                case 22:
                case 27:
                    // feed slots
                    return isSlotVisible(slot) && !(stack.getItem() instanceof BeeJar);
                case 8:
                case 13:
                case 18:
                case 23:
                case 28:
                    // jar slots
                    return isSlotVisible(slot) && stack.getItem() instanceof BeeJar && !BeeJar.isFilled(stack);
                default:
                    //do nothing
                    return false;
            }
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
                        CompoundNBT data = UpgradeItem.getUpgradeData(upgradeItem);

                        if (data != null && data.getString(NBTConstants.NBT_UPGRADE_TYPE).equals(NBTConstants.NBT_BREEDER_UPGRADE)) {
                            count += (int) MathHelper.clamp(data.getFloat(NBTConstants.NBT_BREEDER_COUNT), 0F, 5);
                        }
                    }
                }
            }

            ApiaryBreederTileEntity.this.setNumberOfBreeders(count);
            tileStackHandler.setMaxSlots(3 + ApiaryBreederTileEntity.this.getNumberOfBreeders() * 5);
        }

        private void updateBreedTime(TileStackHandler tileStackHandler) {
            int newTotalTime = CommonConfig.APIARY_MAX_BREED_TIME.get();
            for (int i = 0; i < 4; i++) {
                if (!tileStackHandler.getStackInSlot(getUpgradeSlots()[i]).isEmpty()) {
                    ItemStack upgradeItem = tileStackHandler.getStackInSlot(getUpgradeSlots()[i]);
                    if (UpgradeItem.isUpgradeItem(upgradeItem)) {
                        CompoundNBT data = UpgradeItem.getUpgradeData(upgradeItem);

                        if (data != null && data.getString(NBTConstants.NBT_UPGRADE_TYPE).equals(NBTConstants.NBT_BREEDER_UPGRADE)) {
                            newTotalTime -= (int) MathHelper.clamp(data.getFloat(NBTConstants.NBT_BREED_TIME), 100, 600);
                        }
                    }
                }
            }

            ApiaryBreederTileEntity.this.setTotalTime(MathHelper.clamp(newTotalTime, 300, 4800));
        }

        private void rebuildOpenContainers() {
            if (ApiaryBreederTileEntity.this.level != null) {
                float f = 5.0F;
                BlockPos pos = ApiaryBreederTileEntity.this.worldPosition;

                ApiaryBreederTileEntity.this.level.getEntitiesOfClass(PlayerEntity.class, new AxisAlignedBB(pos.getX() - f, pos.getY() - f, pos.getZ() - f, (pos.getX() + 1) + f, (pos.getY() + 1) + f, (pos.getZ() + 1) + f))
                        .stream()
                        .filter(playerEntity -> playerEntity.containerMenu instanceof ApiaryBreederContainer)
                        .filter(this::openContainerMatches)
                        .forEach(playerEntity -> ((ApiaryBreederContainer) playerEntity.containerMenu).setupSlots(true));
            }
        }

        private boolean openContainerMatches(PlayerEntity playerEntity) {
            ApiaryBreederContainer openContainer = (ApiaryBreederContainer) playerEntity.containerMenu;
            ApiaryBreederTileEntity apiaryBreederTileEntity = openContainer.getApiaryBreederTileEntity();
            return  ApiaryBreederTileEntity.this == apiaryBreederTileEntity;
        }
    }
}
