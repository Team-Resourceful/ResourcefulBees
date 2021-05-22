package com.teamresourceful.resourcefulbees.tileentity.multiblocks.apiary;

import com.teamresourceful.resourcefulbees.api.ICustomBee;
import com.teamresourceful.resourcefulbees.api.beedata.BreedData;
import com.teamresourceful.resourcefulbees.config.Config;
import com.teamresourceful.resourcefulbees.container.ApiaryBreederContainer;
import com.teamresourceful.resourcefulbees.container.AutomationSensitiveItemStackHandler;
import com.teamresourceful.resourcefulbees.entity.passive.CustomBeeEntity;
import com.teamresourceful.resourcefulbees.item.BeeJar;
import com.teamresourceful.resourcefulbees.item.UpgradeItem;
import com.teamresourceful.resourcefulbees.lib.ApiaryTabs;
import com.teamresourceful.resourcefulbees.lib.NBTConstants;
import com.teamresourceful.resourcefulbees.registry.BeeRegistry;
import com.teamresourceful.resourcefulbees.registry.ModBlockEntityTypes;
import com.teamresourceful.resourcefulbees.utils.BeeInfoUtils;
import com.teamresourceful.resourcefulbees.utils.MathUtils;
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
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.TickableBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fml.network.NetworkHooks;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static com.teamresourceful.resourcefulbees.lib.NBTConstants.NBT_BREEDER_COUNT;

public class ApiaryBreederTileEntity extends BlockEntity implements TickableBlockEntity, MenuProvider, IApiaryMultiblock {

    private static final int[] UPGRADE_SLOTS = {0, 1, 2, 3};
    private static final int[] PARENT_1_SLOTS = {4, 9, 14, 19, 24};
    private static final int[] FEED_1_SLOTS = {5, 10, 15, 20, 25};
    private static final int[] PARENT_2_SLOTS = {6, 11, 16, 21, 26};
    private static final int[] FEED_2_SLOTS = {7, 12, 17, 22, 27};
    private static final int[] EMPTY_JAR_SLOTS = {8, 13, 18, 23, 28};

    private final ApiaryBreederTileEntity.TileStackHandler tileStackHandler = new ApiaryBreederTileEntity.TileStackHandler(29);
    private final LazyOptional<IItemHandler> lazyOptional = LazyOptional.of(this::getTileStackHandler);
    private int[] time = {0, 0, 0, 0, 0};
    private int totalTime = Config.APIARY_MAX_BREED_TIME.get();
    private int numberOfBreeders = 1;

    private BlockPos apiaryPos;
    private ApiaryTileEntity apiary;

    protected final ContainerData times = new ContainerData() {
        @Override
        public int get(int index) {
            return MathUtils.inRangeExclusive(index, -1, 5)
                    ? ApiaryBreederTileEntity.this.getTime()[index]
                    : 0;
        }

        @Override
        public void set(int index, int value) {
            if (!MathUtils.inRangeExclusive(index, -1, 5)) return;
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

    public BlockPos getApiaryPos() {
        return apiaryPos;
    }

    public void setApiaryPos(BlockPos apiaryPos) {
        this.apiaryPos = apiaryPos;
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

    public boolean validateApiaryLink() {
        apiary = getApiary();
        if (apiary == null || apiary.getBreederPos() == null || !apiary.getBreederPos().equals(this.getBlockPos()) || !apiary.isValidApiary(false)) { //check apiary has storage location equal to this and apiary is valid
            apiaryPos = null; //if not set these to null
            return false;
        }
        return true;
    }

    @Override
    public void tick() {
        if (level != null && !level.isClientSide) {
            validateApiaryLink();
            boolean dirty = false;
            for (int i = 0; i < getNumberOfBreeders(); i++) {
                if (!getTileStackHandler().getStackInSlot(getParent1Slots()[i]).isEmpty() && !getTileStackHandler().getStackInSlot(getFeed1Slots()[i]).isEmpty()
                        && !getTileStackHandler().getStackInSlot(getParent2Slots()[i]).isEmpty() && !getTileStackHandler().getStackInSlot(getFeed2Slots()[i]).isEmpty()
                        && !getTileStackHandler().getStackInSlot(getEmptyJarSlots()[i]).isEmpty()) {
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

    protected boolean canProcess(int slot) {
        ItemStack p1Stack = getTileStackHandler().getStackInSlot(getParent1Slots()[slot]);
        ItemStack p2Stack = getTileStackHandler().getStackInSlot(getParent2Slots()[slot]);
        if (p1Stack.getItem() instanceof BeeJar && p2Stack.getItem() instanceof BeeJar) {
            BeeJar p1Jar = (BeeJar) p1Stack.getItem();
            BeeJar p2Jar = (BeeJar) p2Stack.getItem();

            Entity p1Entity = p1Jar.getEntityFromStack(p1Stack, level, true);
            Entity p2Entity = p2Jar.getEntityFromStack(p2Stack, level, true);

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
                BeeJar p1Jar = (BeeJar) p1Stack.getItem();
                BeeJar p2Jar = (BeeJar) p2Stack.getItem();

                Entity p1Entity = p1Jar.getEntityFromStack(p1Stack, level, true);
                Entity p2Entity = p2Jar.getEntityFromStack(p2Stack, level, true);

                if (p1Entity instanceof ICustomBee && p2Entity instanceof ICustomBee) {
                    ICustomBee bee1 = (ICustomBee) p1Entity;
                    ICustomBee bee2 = (ICustomBee) p2Entity;

                    String p1Type = bee1.getBeeType();
                    String p2Type = bee2.getBeeType();

                    if (level != null && validateApiaryLink()) {
                        BlockEntity tile = level.getBlockEntity(apiary.getStoragePos());
                        if (tile instanceof ApiaryStorageTileEntity) {
                            ApiaryStorageTileEntity apiaryStorage = (ApiaryStorageTileEntity) tile;
                            if (apiaryStorage.breedComplete(p1Type, p2Type)) {
                                getTileStackHandler().getStackInSlot(getEmptyJarSlots()[slot]).shrink(1);
                                getTileStackHandler().getStackInSlot(getFeed1Slots()[slot]).shrink(bee1.getBreedData().getFeedAmount());
                                getTileStackHandler().getStackInSlot(getFeed2Slots()[slot]).shrink(bee2.getBreedData().getFeedAmount());
                            }
                        }
                    }
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
        if (apiaryPos != null)
            nbt.put(NBTConstants.NBT_APIARY_POS, NbtUtils.writeBlockPos(apiaryPos));
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
    public void load(@NotNull BlockState state, @NotNull CompoundTag nbt) {
        super.load(state, nbt);
        this.loadFromNBT(nbt);
    }

    public void loadFromNBT(CompoundTag nbt) {
        CompoundTag invTag = nbt.getCompound(NBTConstants.NBT_INVENTORY);
        getTileStackHandler().deserializeNBT(invTag);
        setTime(nbt.getIntArray("time"));
        setTotalTime(nbt.getInt("totalTime"));
        if (nbt.contains(NBTConstants.NBT_APIARY_POS))
            apiaryPos = NbtUtils.readBlockPos(nbt.getCompound(NBTConstants.NBT_APIARY_POS));
        if (nbt.contains(NBT_BREEDER_COUNT))
            this.setNumberOfBreeders(nbt.getInt(NBT_BREEDER_COUNT));
    }

    @Override
    public void handleUpdateTag(@NotNull BlockState state, CompoundTag tag) {
        this.load(state, tag);
    }

    @Nullable
    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        CompoundTag nbt = new CompoundTag();
        return new ClientboundBlockEntityDataPacket(worldPosition, 0, saveToNBT(nbt));
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
        CompoundTag nbt = pkt.getTag();
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

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, @NotNull Inventory playerInventory, @NotNull Player playerEntity) {
        //noinspection ConstantConditions
        return new ApiaryBreederContainer(id, level, worldPosition, playerInventory, times);
    }

    @NotNull
    @Override
    public Component getDisplayName() {
        return new TranslatableComponent("gui.resourcefulbees.apiary_breeder");
    }

    @Override
    public void switchTab(ServerPlayer player, ApiaryTabs tab) {
        if (level != null && apiaryPos != null) {
            if (tab == ApiaryTabs.MAIN) {
                BlockEntity tile = level.getBlockEntity(apiaryPos);
                NetworkHooks.openGui(player, (MenuProvider) tile, apiaryPos);
            }
            if (tab == ApiaryTabs.STORAGE) {
                BlockEntity tile = level.getBlockEntity(apiary.getStoragePos());
                NetworkHooks.openGui(player, (MenuProvider) tile, apiary.getStoragePos());
            }
        }
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
                        CompoundTag data = UpgradeItem.getUpgradeData(upgradeItem);

                        if (data != null && data.getString(NBTConstants.NBT_UPGRADE_TYPE).equals(NBTConstants.NBT_BREEDER_UPGRADE)) {
                            count += (int) MathUtils.clamp(data.getFloat(NBTConstants.NBT_BREEDER_COUNT), 0F, 5);
                        }
                    }
                }
            }

            ApiaryBreederTileEntity.this.setNumberOfBreeders(count);
            tileStackHandler.setMaxSlots(3 + ApiaryBreederTileEntity.this.getNumberOfBreeders() * 5);
        }

        private void updateBreedTime(TileStackHandler tileStackHandler) {
            int newTotalTime = Config.APIARY_MAX_BREED_TIME.get();
            for (int i = 0; i < 4; i++) {
                if (!tileStackHandler.getStackInSlot(getUpgradeSlots()[i]).isEmpty()) {
                    ItemStack upgradeItem = tileStackHandler.getStackInSlot(getUpgradeSlots()[i]);
                    if (UpgradeItem.isUpgradeItem(upgradeItem)) {
                        CompoundTag data = UpgradeItem.getUpgradeData(upgradeItem);

                        if (data != null && data.getString(NBTConstants.NBT_UPGRADE_TYPE).equals(NBTConstants.NBT_BREEDER_UPGRADE)) {
                            newTotalTime -= (int) MathUtils.clamp(data.getFloat(NBTConstants.NBT_BREED_TIME), 100, 600);
                        }
                    }
                }
            }

            ApiaryBreederTileEntity.this.setTotalTime(MathUtils.clamp(newTotalTime, 300, 4800));
        }

        private void rebuildOpenContainers() {
            if (ApiaryBreederTileEntity.this.level != null) {
                float f = 5.0F;
                BlockPos pos = ApiaryBreederTileEntity.this.worldPosition;

                ApiaryBreederTileEntity.this.level.getEntitiesOfClass(Player.class, new AABB(pos.getX() - f, pos.getY() - f, pos.getZ() - f, (pos.getX() + 1) + f, (pos.getY() + 1) + f, (pos.getZ() + 1) + f))
                        .stream()
                        .filter(playerEntity -> playerEntity.containerMenu instanceof ApiaryBreederContainer)
                        .filter(this::openContainerMatches)
                        .forEach(playerEntity -> ((ApiaryBreederContainer) playerEntity.containerMenu).setupSlots(true));
            }
        }

        private boolean openContainerMatches(Player playerEntity) {
            ApiaryBreederContainer openContainer = (ApiaryBreederContainer) playerEntity.containerMenu;
            ApiaryBreederTileEntity apiaryBreederTileEntity = openContainer.getApiaryBreederTileEntity();
            return  ApiaryBreederTileEntity.this == apiaryBreederTileEntity;
        }
    }
}
