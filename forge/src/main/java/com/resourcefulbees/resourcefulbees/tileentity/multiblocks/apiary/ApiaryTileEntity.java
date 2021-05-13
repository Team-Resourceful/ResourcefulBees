package com.resourcefulbees.resourcefulbees.tileentity.multiblocks.apiary;


import com.resourcefulbees.resourcefulbees.api.ICustomBee;
import com.resourcefulbees.resourcefulbees.block.multiblocks.apiary.ApiaryBlock;
import com.resourcefulbees.resourcefulbees.config.Config;
import com.resourcefulbees.resourcefulbees.container.AutomationSensitiveItemStackHandler;
import com.resourcefulbees.resourcefulbees.item.BeeJar;
import com.resourcefulbees.resourcefulbees.lib.BeeConstants;
import com.resourcefulbees.resourcefulbees.lib.NBTConstants;
import com.resourcefulbees.resourcefulbees.registry.ModBlockEntityTypes;
import com.resourcefulbees.resourcefulbees.registry.ModItems;
import com.resourcefulbees.resourcefulbees.utils.BeeInfoUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BeehiveBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.LinkedHashMap;
import java.util.Map;

import static com.resourcefulbees.resourcefulbees.lib.BeeConstants.MIN_HIVE_TIME;

public class ApiaryTileEntity extends ApiaryController implements MenuProvider, IApiaryMultiblock {
    public static final int IMPORT = 0;
    public static final int EXPORT = 2;
    public static final int EMPTY_JAR = 1;
    public final Map<String, ApiaryBee> bees = new LinkedHashMap<>();
    protected int tier;
    private final ApiaryTileEntity.TileStackHandler tileStackHandler = new ApiaryTileEntity.TileStackHandler(3);
    private final LazyOptional<IItemHandler> lazyOptional = LazyOptional.of(this::getTileStackHandler);
    protected int ticksSinceBeesFlagged;


    public ApiaryTileEntity() {
        super(ModBlockEntityTypes.APIARY_TILE_ENTITY.get());
    }

    public ApiaryTileEntity(BlockEntityType<?> tileEntityType) {
        super(tileEntityType);
    }

    public int getTier() {
        return tier;
    }

    public int getMaxBees() {
        return Config.APIARY_MAX_BEES.get();
    }

    public int getBeeCount() {
        return this.bees.size();
    }

    public void setTier(int tier) {
        this.tier = tier;
    }

    //region BEE HANDLING
    public boolean releaseBee(@NotNull BlockState state, ApiaryBee apiaryBee, boolean exportBee) {
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

            if (entity instanceof Bee) {
                Bee vanillaBeeEntity = (Bee) entity;
                handleNectar(exportBee, nbt, vanillaBeeEntity);
                this.ageBee(apiaryBee.getTicksInHive(), vanillaBeeEntity);
                releaseBee(exportBee, vanillaBeeEntity, this.level);
            }
            return true;
        }
        return false;
    }

    private void releaseBee(boolean exportBee, Bee vanillaBeeEntity, Level level) {
        if (exportBee) {
            exportBee(vanillaBeeEntity);
        } else {
            BlockPos hivePos = this.getBlockPos();
            level.playSound(null, hivePos.getX(), hivePos.getY(), hivePos.getZ(), SoundEvents.BEEHIVE_EXIT, SoundSource.BLOCKS, 1.0F, 1.0F);
            level.addFreshEntity(vanillaBeeEntity);
        }

        if (!level.isClientSide) syncApiaryToPlayersUsing(this.level, this.getBlockPos(), this.saveToNBT(new CompoundTag()));
    }

    private void handleNectar(boolean exportBee, CompoundTag nbt, Bee vanillaBeeEntity) {
        if (nbt.getBoolean("HasNectar")) {
            vanillaBeeEntity.dropOffNectar();

            if (!exportBee && isValidApiary(true)) {
                getApiaryStorage().deliverHoneycomb(vanillaBeeEntity, getTier());
            }
        }
    }

    private void ageBee(int ticksInHive, Bee beeEntity) {
        BeeInfoUtils.ageBee(ticksInHive, beeEntity);
    }

    public boolean tryEnterHive(Entity bee, boolean hasNectar, boolean imported) {
        return isValidApiary(true) && this.tryEnterHive(bee, hasNectar, 0, imported);
    }

    public boolean tryEnterHive(@NotNull Entity bee, boolean hasNectar, int ticksInHive, boolean imported) {
        if (this.level != null && bee instanceof Bee) {
            String type = getBeeType(bee);

            if (!this.bees.containsKey(type) && this.bees.size() < getMaxBees()) {
                bee.ejectPassengers();
                CompoundTag nbt = new CompoundTag();
                bee.save(nbt);

                this.bees.computeIfAbsent(type, k -> new ApiaryBee(nbt, ticksInHive, hasNectar ? getMaxTimeInHive(bee) : MIN_HIVE_TIME, type, getBeeColor(bee), bee.getName()));
                this.level.playSound(null, this.getBlockPos(), SoundEvents.BEEHIVE_ENTER, SoundSource.BLOCKS, 1.0F, 1.0F);
                this.bees.get(type).setLocked(imported);

                if (this.getNumPlayersUsing() > 0) syncApiaryToPlayersUsing(this.level, this.getBlockPos(), this.saveToNBT(new CompoundTag()));

                bee.remove();
                return true;
            }
        }
        return false;
    }

    private String getBeeColor(Entity bee) {
        if (bee instanceof ICustomBee) {
            ICustomBee iCustomBee = (ICustomBee) bee;
            return iCustomBee.getRenderData().getColorData().getJarColor().toString();
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
        if (this.tier != 1) {
            if (this.tier == 0) {
                return (int) (timeInput * 1.05);
            } else {
                return (int) (timeInput * (1 - getTier() * .05));
            }
        }
        return timeInput;
    }

    @Override
    public void tick() {
        super.tick();
        this.bees.values().stream()
                .filter(apiaryBee -> !(canRelease(apiaryBee) && releaseBee(this.getBlockState(), apiaryBee, false)))
                .forEach(this::tickBee);

        if (level != null && !level.isClientSide && isValidApiary) {
            if (this.bees.size() > 0 && this.level.getRandom().nextDouble() < 0.005D) {
                BlockPos blockpos = this.getBlockPos();
                double d0 = blockpos.getX() + 0.5D;
                double d1 = blockpos.getY();
                double d2 = blockpos.getZ() + 0.5D;
                this.level.playSound(null, d0, d1, d2, SoundEvents.BEEHIVE_WORK, SoundSource.BLOCKS, 1.0F, 1.0F);
            }

            ticksSinceBeesFlagged++;
            if (ticksSinceBeesFlagged == 80) {
                BeeInfoUtils.flagBeesInRange(worldPosition, level);
                ticksSinceBeesFlagged = 0;
            }
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
        Block hive = getBlockState().getBlock();
        return isValidApiary(false) && hive instanceof ApiaryBlock;
    }

    public void lockOrUnlockBee(String beeType) {
        this.bees.get(beeType).setLocked(!this.bees.get(beeType).isLocked());
        syncApiaryToPlayersUsing(this.level, this.getBlockPos(), this.saveToNBT(new CompoundTag()));
    }
    //endregion

    //region NBT HANDLING

    @Nonnull
    public ListTag writeBees() {
        ListTag listTag = new ListTag();

        this.bees.forEach((key, apiaryBee) -> {
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
    public void loadFromNBT(CompoundTag nbt) {
        super.loadFromNBT(nbt);
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
    public CompoundTag saveToNBT(CompoundTag nbt) {
        super.saveToNBT(nbt);
        CompoundTag inv = this.getTileStackHandler().serializeNBT();
        nbt.put(NBTConstants.NBT_INVENTORY, inv);
        nbt.put(NBTConstants.NBT_BEES, this.writeBees());
        nbt.putInt(NBTConstants.NBT_TIER, getTier());
        return nbt;
    }
    //endregion

    //region IMPORT/EXPORT
    public void importBee(ServerPlayer player) {
        boolean isSuccessful = false;

        if (canImport()) {
            ItemStack filledJar = this.getTileStackHandler().getStackInSlot(IMPORT);
            ItemStack emptyJar = this.getTileStackHandler().getStackInSlot(EMPTY_JAR);

            if (filledJar.getItem() instanceof BeeJar) {
                BeeJar jarItem = (BeeJar) filledJar.getItem();
                Entity entity = jarItem.getEntityFromStack(filledJar, this.level, true);

                if (entity instanceof Bee) {
                    Bee beeEntity = (Bee) entity;
                    isSuccessful = tryEnterHive(beeEntity, beeEntity.hasNectar(), true);
                    handleSuccess(isSuccessful, filledJar, emptyJar, jarItem);
                }
            }
        }

        player.displayClientMessage(new TranslatableComponent("gui.resourcefulbees.apiary.import." + isSuccessful), true);
    }

    private void handleSuccess(boolean isSuccessful, ItemStack filledJar, ItemStack emptyJar, BeeJar jarItem) {
        if (isSuccessful) {
            filledJar.shrink(1);
            if (emptyJar.isEmpty()) {
                this.getTileStackHandler().setStackInSlot(EMPTY_JAR, new ItemStack(jarItem));
            } else {
                emptyJar.grow(1);
            }
        }
    }

    private boolean canImport() {
        return !this.getTileStackHandler().getStackInSlot(IMPORT).isEmpty() && this.getTileStackHandler().getStackInSlot(EMPTY_JAR).getCount() < 16;
    }

    public void exportBee(ServerPlayer player, String beeType) {
        boolean exported = false;
        ApiaryBee bee = this.bees.get(beeType);

        if (bee.isLocked() && getTileStackHandler().getStackInSlot(EXPORT).isEmpty() && !getTileStackHandler().getStackInSlot(EMPTY_JAR).isEmpty()) {
            exported = releaseBee(this.getBlockState(), bee, true);
        }
        if (exported) {
            this.bees.remove(beeType);
            this.getTileStackHandler().getStackInSlot(EMPTY_JAR).shrink(1);
            if (this.getNumPlayersUsing() > 0 && this.level != null && !this.level.isClientSide)
                syncApiaryToPlayersUsing(this.level, this.getBlockPos(), this.saveToNBT(new CompoundTag()));
        }

        player.displayClientMessage(new TranslatableComponent("gui.resourcefulbees.apiary.export." + exported), true);
    }

    public void exportBee(Bee beeEntity) {
        ItemStack beeJar = new ItemStack(ModItems.BEE_JAR.get());
        beeJar.setTag(BeeInfoUtils.createJarBeeTag(beeEntity, NBTConstants.NBT_ENTITY));
        BeeJar.renameJar(beeJar, beeEntity);
        this.getTileStackHandler().setStackInSlot(EXPORT, beeJar);
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

    public enum State {
        HONEY_DELIVERED,
        BEE_RELEASED,
        EMERGENCY
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
            if (slot == IMPORT) {
                return 1;
            }
            return super.getSlotLimit(slot);
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
