package com.teamresourceful.resourcefulbees.common.tileentity.multiblocks.apiary;


import com.teamresourceful.resourcefulbees.api.ICustomBee;
import com.teamresourceful.resourcefulbees.common.block.multiblocks.apiary.ApiaryBlock;
import com.teamresourceful.resourcefulbees.common.config.CommonConfig;
import com.teamresourceful.resourcefulbees.common.inventory.AutomationSensitiveItemStackHandler;
import com.teamresourceful.resourcefulbees.common.inventory.containers.ValidatedApiaryContainer;
import com.teamresourceful.resourcefulbees.common.item.BeeJar;
import com.teamresourceful.resourcefulbees.common.lib.constants.BeeConstants;
import com.teamresourceful.resourcefulbees.common.lib.constants.NBTConstants;
import com.teamresourceful.resourcefulbees.common.lib.constants.TranslationConstants;
import com.teamresourceful.resourcefulbees.common.network.NetPacketHandler;
import com.teamresourceful.resourcefulbees.common.network.packets.SyncGUIMessage;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.ModBlockEntityTypes;
import com.teamresourceful.resourcefulbees.common.tileentity.ISyncableGUI;
import com.teamresourceful.resourcefulbees.common.utils.BeeInfoUtils;
import io.netty.buffer.Unpooled;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerListener;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BeehiveBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static com.teamresourceful.resourcefulbees.common.lib.constants.BeeConstants.MIN_HIVE_TIME;

public class ApiaryTileEntity extends BlockEntity implements MenuProvider, ISyncableGUI {
    public static final int IMPORT = 0;
    public static final int EXPORT = 2;
    public static final int EMPTY_JAR = 1;
    public final Map<String, ApiaryBee> bees = new LinkedHashMap<>();
    protected int tier;
    private final ApiaryTileEntity.TileStackHandler tileStackHandler = new ApiaryTileEntity.TileStackHandler(3);
    private final LazyOptional<IItemHandler> lazyOptional = LazyOptional.of(this::getTileStackHandler);
    protected int ticksSinceBeesFlagged;

    public ApiaryTileEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityTypes.APIARY_TILE_ENTITY.get(), pos, state);
    }

    public int getTier() {
        return tier;
    }

    public int getMaxBees() {
        return CommonConfig.APIARY_MAX_BEES.get();
    }

    public int getBeeCount() {
        return this.bees.size();
    }

    public void setTier(int tier) {
        this.tier = tier;
    }

    //region BEE HANDLING
    public boolean releaseBee(@NotNull BlockState state, ApiaryBee apiaryBee) {
        this.bees.remove(apiaryBee.beeType);
        BlockPos blockPos = this.getBlockPos();
        Direction direction = state.getValue(BeehiveBlock.FACING);
        BlockPos blockPos1 = blockPos.relative(direction);
        CompoundTag nbt = apiaryBee.entityData;

        if (level != null && this.level.getBlockState(blockPos1).getCollisionShape(this.level, blockPos1).isEmpty()) {
            nbt.remove("Passengers");
            nbt.remove("Leash");
            nbt.remove("UUID");
            Entity entity = EntityType.loadEntityRecursive(nbt, this.level, entity1 -> entity1);
            if (entity == null) return true;
            BeeInfoUtils.setEntityLocationAndAngle(blockPos, direction, entity);

            if (entity instanceof Bee bee) {
                deliverNectar(nbt, bee);
                this.ageBee(apiaryBee.getTicksInHive(), bee);
                releaseBee(bee, this.level);
            }
            return true;
        }
        return false;
    }

    private void releaseBee(Bee vanillaBeeEntity, Level level) {
        BlockPos hivePos = this.getBlockPos();
        level.playSound(null, hivePos.getX(), hivePos.getY(), hivePos.getZ(), SoundEvents.BEEHIVE_EXIT, SoundSource.BLOCKS, 1.0F, 1.0F);
        level.addFreshEntity(vanillaBeeEntity);
    }

    private void deliverNectar(CompoundTag nbt, Bee vanillaBeeEntity) {
        if (nbt.getBoolean("HasNectar")) {
            vanillaBeeEntity.dropOffNectar();
            //TODO DEPOSIT COMB
        }
    }

    private void ageBee(int ticksInHive, Bee beeEntity) {
        BeeInfoUtils.ageBee(ticksInHive, beeEntity);
    }

    public boolean tryEnterHive(Entity bee, boolean hasNectar, boolean imported) {
        return this.tryEnterHive(bee, hasNectar, 0, imported);
    }

    public boolean tryEnterHive(@NotNull Entity bee, boolean hasNectar, int ticksInHive, boolean imported) {
        if (this.level != null && bee instanceof Bee) {
            String type = getBeeType(bee);

            if (!this.bees.containsKey(type) && this.bees.size() < getMaxBees()) {
                bee.ejectPassengers();
                CompoundTag nbt = new CompoundTag();
                bee.save(nbt);
                this.bees.put(type, new ApiaryBee(nbt, ticksInHive, hasNectar ? getMaxTimeInHive(bee) : MIN_HIVE_TIME, type, getBeeColor(bee), bee.getName(), imported));
                this.level.playSound(null, this.getBlockPos(), SoundEvents.BEEHIVE_ENTER, SoundSource.BLOCKS, 1.0F, 1.0F);
                bee.discard();
                return true;
            }
        }
        return false;
    }

    private String getBeeColor(Entity bee) {
        if (bee instanceof ICustomBee) {
            return ((ICustomBee) bee).getRenderData().getColorData().getJarColor().toString();
        }
        return BeeConstants.VANILLA_BEE_COLOR;
    }

    private String getBeeType(@NotNull Entity bee) {
        return bee instanceof ICustomBee ? ((ICustomBee) bee).getBeeType() :BeeConstants.VANILLA_BEE_TYPE;
    }

    private int getMaxTimeInHive(@NotNull Entity bee) {
        return getMaxTimeInHive(bee instanceof ICustomBee ? ((ICustomBee) bee).getCoreData().getMaxTimeInHive() : BeeConstants.MAX_TIME_IN_HIVE);
    }

    private int getMaxTimeInHive(int timeInput) {
        return this.tier >= 0 ? (int) (timeInput * (1 - (getTier() * .1)) - .1) : timeInput;
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, ApiaryTileEntity apiaryTile) {
        new HashSet<>(apiaryTile.bees.values()).stream()
                .filter(apiaryBee -> !(apiaryTile.canRelease(apiaryBee) && apiaryTile.releaseBee(state, apiaryBee)))
                .forEach(apiaryTile::tickBee);

        if (apiaryTile.bees.size() > 0 && level.getRandom().nextDouble() < 0.005D) {
            level.playSound(null, pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D, SoundEvents.BEEHIVE_WORK, SoundSource.BLOCKS, 1.0F, 1.0F);
        }

        apiaryTile.ticksSinceBeesFlagged++;
        if (apiaryTile.ticksSinceBeesFlagged == 80) {
            BeeInfoUtils.flagBeesInRange(pos, level);
            apiaryTile.ticksSinceBeesFlagged = 0;
        }
    }

    private void tickBee(ApiaryBee apiaryBee) {
        apiaryBee.setTicksInHive(Math.min(apiaryBee.getTicksInHive() + 1, Integer.MAX_VALUE - 1));
    }

    private boolean canRelease(ApiaryBee apiaryBee) {
        return !apiaryBee.isLocked() && apiaryBee.getTicksInHive() > apiaryBee.minOccupationTicks;
    }

    public boolean hasSpace() {
        return this.bees.size() < getMaxBees();
    }

    public boolean isAllowedBee() {
        return getBlockState().getBlock() instanceof ApiaryBlock;
    }

    public void lockOrUnlockBee(String beeType) {
        this.bees.get(beeType).setLocked(!this.bees.get(beeType).isLocked());
    }
    //endregion

    //region NBT HANDLING

    @NotNull
    public ListTag writeBees() {
        ListTag listTag = new ListTag();
        this.bees.values().forEach(apiaryBee -> {
            apiaryBee.entityData.remove("UUID");
            CompoundTag compoundnbt = new CompoundTag();
            compoundnbt.put("EntityData", apiaryBee.entityData);
            compoundnbt.putInt("TicksInHive", apiaryBee.getTicksInHive());
            compoundnbt.putInt("MinOccupationTicks", apiaryBee.minOccupationTicks);
            compoundnbt.putBoolean(NBTConstants.NBT_LOCKED, apiaryBee.isLocked());
            compoundnbt.putString(NBTConstants.NBT_BEE_TYPE, apiaryBee.beeType);
            compoundnbt.putString(NBTConstants.NBT_COLOR, apiaryBee.beeColor);
            compoundnbt.putString(NBTConstants.NBT_BEE_NAME, Component.Serializer.toJson(apiaryBee.displayName));
            listTag.add(compoundnbt);
        });
        return listTag;
    }

    public void loadBees(CompoundTag nbt) {
        ListTag listTag = nbt.getList(NBTConstants.NBT_BEES, 10);

        if (!listTag.isEmpty()) {
            for (int i = 0; i < listTag.size(); ++i) {
                CompoundTag data = listTag.getCompound(i);

                String beeType = data.getString(NBTConstants.NBT_BEE_TYPE);
                String beeColor = data.contains(NBTConstants.NBT_COLOR) ? data.getString(NBTConstants.NBT_COLOR) : BeeConstants.VANILLA_BEE_COLOR;
                Component displayName = data.contains(NBTConstants.NBT_BEE_NAME) ? Component.Serializer.fromJson(data.getString(NBTConstants.NBT_BEE_NAME)) : new TextComponent("Temp Bee Name");

                this.bees.computeIfAbsent(data.getString(NBTConstants.NBT_BEE_TYPE), k -> new ApiaryBee(
                        data.getCompound("EntityData"),
                        data.getInt("TicksInHive"),
                        data.getInt("MinOccupationTicks"),
                        beeType,
                        beeColor,
                        displayName));

                this.bees.get(beeType).setLocked(data.getBoolean(NBTConstants.NBT_LOCKED));
            }
        }
    }

    @Override
    public void load(@NotNull CompoundTag nbt) {
        super.load(nbt);
        loadBees(nbt);
        CompoundTag invTag = nbt.getCompound(NBTConstants.NBT_INVENTORY);
        getTileStackHandler().deserializeNBT(invTag);
        if (nbt.contains(NBTConstants.NBT_TIER)) {
            setTier(nbt.getInt(NBTConstants.NBT_TIER));
        }
        validateBees();
    }

    private void validateBees() {
        if (this.level == null) return;
        bees.forEach((s, apiaryBee) -> {
            String id = apiaryBee.entityData.getString("id");
            EntityType<?> type = BeeInfoUtils.getEntityType(id);
            if (type == EntityType.PIG) bees.remove(s);
        });
    }

    @Override
    public @NotNull CompoundTag save(@NotNull CompoundTag nbt) {
        super.save(nbt);
        nbt.put(NBTConstants.NBT_INVENTORY, this.getTileStackHandler().serializeNBT());
        nbt.put(NBTConstants.NBT_BEES, this.writeBees());
        nbt.putInt(NBTConstants.NBT_TIER, getTier());
        return nbt;
    }
    //endregion

    @NotNull
    @Override
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        return cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY ? getLazyOptional().cast() :
                super.getCapability(cap, side);
    }

    public AutomationSensitiveItemStackHandler.IAcceptor getAcceptor() {
        return (slot, stack, automation) -> !automation || slot == 0 || slot == 1;
    }

    public AutomationSensitiveItemStackHandler.IRemover getRemover() {
        return (slot, automation) -> !automation || slot == 1 || slot == 2;
    }

    public @NotNull TileStackHandler getTileStackHandler() {
        return tileStackHandler;
    }

    public LazyOptional<IItemHandler> getLazyOptional() {
        return lazyOptional;
    }

    @Override
    public @NotNull Component getDisplayName() {
        return TranslationConstants.Guis.APIARY;
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, @NotNull Inventory inventory, @NotNull Player player) {
        return level == null ? null : new ValidatedApiaryContainer(id, level, worldPosition, inventory);
    }

    public void sendData(ServerPlayer player) {
        if (!(player instanceof FakePlayer)) {
            FriendlyByteBuf buffer = new FriendlyByteBuf(Unpooled.buffer());
            CompoundTag tag = new CompoundTag();
            tag.put(NBTConstants.NBT_BEES, writeBees());
            buffer.writeNbt(tag);
            NetPacketHandler.sendToPlayer(new SyncGUIMessage(this.worldPosition, buffer), player);
        }
    }

    @Override
    public void sendGUINetworkPacket(ContainerListener player) {
    }

    @Override
    public void handleGUINetworkPacket(FriendlyByteBuf buffer) {
        CompoundTag nbt = buffer.readNbt();
        if (nbt != null) {
            bees.clear();
            loadBees(nbt);
        }
    }

    public static class ApiaryBee {
        public final CompoundTag entityData;
        public final int minOccupationTicks;
        public final String beeType;
        private int ticksInHive;
        private boolean isLocked = false;
        public final String beeColor;
        public final Component displayName;

        public ApiaryBee(CompoundTag nbt, int ticksInHive, int minOccupationTicks, String beeType, String beeColor, Component displayName) {
            nbt.remove("UUID");
            this.entityData = nbt;
            this.setTicksInHive(ticksInHive);
            this.minOccupationTicks = minOccupationTicks;
            this.beeType = beeType;
            this.beeColor = beeColor;
            this.displayName = displayName;
        }
        public ApiaryBee(CompoundTag nbt, int ticksInHive, int minOccupationTicks, String beeType, String beeColor, Component  displayName, boolean isLocked) {
            this(nbt, ticksInHive, minOccupationTicks, beeType, beeColor, displayName);
            this.isLocked = isLocked;
        }

        public int getTicksInHive() {
            return ticksInHive;
        }

        public void setTicksInHive(int ticksInHive) {
            this.ticksInHive = ticksInHive;
        }

        public boolean isLocked() {
            return isLocked;
        }

        public void setLocked(boolean locked) {
            isLocked = locked;
        }
    }

    public class TileStackHandler extends AutomationSensitiveItemStackHandler {

        protected TileStackHandler(int slots) {
            super(slots);
        }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            if (slot == IMPORT) {
                return stack.getItem() instanceof BeeJar && BeeJar.isFilled(stack);
            } else if (slot == EMPTY_JAR) {
                return stack.getItem() instanceof BeeJar && !BeeJar.isFilled(stack);
            } else {
                return false;
            }
        }

        @Override
        public int getSlotLimit(int slot) {
            return slot == IMPORT ? 1 : super.getSlotLimit(slot);
        }

        @Override
        public AutomationSensitiveItemStackHandler.IAcceptor getAcceptor() {
            return ApiaryTileEntity.this.getAcceptor();
        }

        @Override
        public AutomationSensitiveItemStackHandler.IRemover getRemover() {
            return ApiaryTileEntity.this.getRemover();
        }

        @Override
        protected void onContentsChanged(int slot) {
            super.onContentsChanged(slot);
            setChanged();
        }
    }
}
