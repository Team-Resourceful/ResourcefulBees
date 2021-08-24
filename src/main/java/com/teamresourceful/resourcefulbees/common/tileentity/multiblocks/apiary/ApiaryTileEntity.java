package com.teamresourceful.resourcefulbees.common.tileentity.multiblocks.apiary;


import com.teamresourceful.resourcefulbees.api.ICustomBee;
import com.teamresourceful.resourcefulbees.common.block.multiblocks.apiary.ApiaryBlock;
import com.teamresourceful.resourcefulbees.common.config.Config;
import com.teamresourceful.resourcefulbees.common.container.AutomationSensitiveItemStackHandler;
import com.teamresourceful.resourcefulbees.common.item.BeeJar;
import com.teamresourceful.resourcefulbees.common.lib.constants.BeeConstants;
import com.teamresourceful.resourcefulbees.common.lib.constants.NBTConstants;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.ModBlockEntityTypes;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.ModItems;
import com.teamresourceful.resourcefulbees.common.utils.BeeInfoUtils;
import net.minecraft.block.BeehiveBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;

import static com.teamresourceful.resourcefulbees.common.lib.constants.BeeConstants.MIN_HIVE_TIME;

public class ApiaryTileEntity extends ApiaryController {
    private static final TranslationTextComponent IMPORT_FAILED = new TranslationTextComponent("gui.resourcefulbees.apiary.import.false");
    private static final TranslationTextComponent IMPORT_SUCCESS = new TranslationTextComponent("gui.resourcefulbees.apiary.import.true");
    private static final TranslationTextComponent EXPORT_FAILED = new TranslationTextComponent("gui.resourcefulbees.apiary.export.false");
    private static final TranslationTextComponent EXPORT_SUCCESS = new TranslationTextComponent("gui.resourcefulbees.apiary.export.true");
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

    public ApiaryTileEntity(TileEntityType<?> tileEntityType) {
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
    public boolean releaseBee(@NotNull BlockState state, ApiaryBee apiaryBee, boolean isExporting) {
        this.bees.remove(apiaryBee.beeType);
        BlockPos blockPos = this.getBlockPos();
        Direction direction = state.getValue(BeehiveBlock.FACING);
        BlockPos blockPos1 = blockPos.relative(direction);
        CompoundNBT nbt = apiaryBee.entityData;

        if (level != null && this.level.getBlockState(blockPos1).getCollisionShape(this.level, blockPos1).isEmpty()) {
            nbt.remove("Passengers");
            nbt.remove("Leash");
            nbt.remove("UUID");
            Entity entity = EntityType.loadEntityRecursive(nbt, this.level, entity1 -> entity1);
            if (entity == null) return true;
            BeeInfoUtils.setEntityLocationAndAngle(blockPos, direction, entity);

            if (entity instanceof BeeEntity) {
                BeeEntity vanillaBeeEntity = (BeeEntity) entity;
                deliverNectar(isExporting, nbt, vanillaBeeEntity);
                this.ageBee(apiaryBee.getTicksInHive(), vanillaBeeEntity);
                releaseBee(isExporting, vanillaBeeEntity, this.level);
            }
            return true;
        }
        return false;
    }

    private void releaseBee(boolean isExporting, BeeEntity vanillaBeeEntity, World level) {
        if (isExporting) {
            exportBee(vanillaBeeEntity);
        } else {
            BlockPos hivePos = this.getBlockPos();
            level.playSound(null, hivePos.getX(), hivePos.getY(), hivePos.getZ(), SoundEvents.BEEHIVE_EXIT, SoundCategory.BLOCKS, 1.0F, 1.0F);
            level.addFreshEntity(vanillaBeeEntity);
        }

        if (!level.isClientSide) syncApiaryToPlayersUsing();
    }

    private void deliverNectar(boolean isExporting, CompoundNBT nbt, BeeEntity vanillaBeeEntity) {
        if (nbt.getBoolean("HasNectar")) {
            vanillaBeeEntity.dropOffNectar();
            if (!isExporting && isValidApiary(true)) {
                getApiaryStorage().deliverHoneycomb(vanillaBeeEntity, getTier());
            }
        }
    }

    private void ageBee(int ticksInHive, BeeEntity beeEntity) {
        BeeInfoUtils.ageBee(ticksInHive, beeEntity);
    }

    public boolean tryEnterHive(Entity bee, boolean hasNectar, boolean imported) {
        return isValidApiary(true) && this.tryEnterHive(bee, hasNectar, 0, imported);
    }

    public boolean tryEnterHive(@NotNull Entity bee, boolean hasNectar, int ticksInHive, boolean imported) {
        if (this.level != null && bee instanceof BeeEntity) {
            String type = getBeeType(bee);

            if (!this.bees.containsKey(type) && this.bees.size() < getMaxBees()) {
                bee.ejectPassengers();
                CompoundNBT nbt = new CompoundNBT();
                bee.save(nbt);
                this.bees.put(type, new ApiaryBee(nbt, ticksInHive, hasNectar ? getMaxTimeInHive(bee) : MIN_HIVE_TIME, type, getBeeColor(bee), bee.getName(), imported));
                this.level.playSound(null, this.getBlockPos(), SoundEvents.BEEHIVE_ENTER, SoundCategory.BLOCKS, 1.0F, 1.0F);
                syncApiaryToPlayersUsing();
                bee.remove();
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
        new HashSet<>(this.bees.values()).stream()
                .filter(apiaryBee -> !(canRelease(apiaryBee) && releaseBee(this.getBlockState(), apiaryBee, false)))
                .forEach(this::tickBee);

        if (level != null && !level.isClientSide && isValidApiary) {
            if (this.bees.size() > 0 && this.level.getRandom().nextDouble() < 0.005D) {
                BlockPos blockpos = this.getBlockPos();
                double d0 = blockpos.getX() + 0.5D;
                double d1 = blockpos.getY();
                double d2 = blockpos.getZ() + 0.5D;
                this.level.playSound(null, d0, d1, d2, SoundEvents.BEEHIVE_WORK, SoundCategory.BLOCKS, 1.0F, 1.0F);
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
        syncApiaryToPlayersUsing();
    }
    //endregion

    //region NBT HANDLING

    @Nonnull
    public ListNBT writeBees() {
        ListNBT listTag = new ListNBT();
        this.bees.values().forEach(apiaryBee -> {
            apiaryBee.entityData.remove("UUID");
            CompoundNBT compoundnbt = new CompoundNBT();
            compoundnbt.put("EntityData", apiaryBee.entityData);
            compoundnbt.putInt("TicksInHive", apiaryBee.getTicksInHive());
            compoundnbt.putInt("MinOccupationTicks", apiaryBee.minOccupationTicks);
            compoundnbt.putBoolean(NBTConstants.NBT_LOCKED, apiaryBee.isLocked());
            compoundnbt.putString(NBTConstants.NBT_BEE_TYPE, apiaryBee.beeType);
            compoundnbt.putString(NBTConstants.NBT_COLOR, apiaryBee.beeColor);
            compoundnbt.putString(NBTConstants.NBT_BEE_NAME, ITextComponent.Serializer.toJson(apiaryBee.displayName));
            listTag.add(compoundnbt);
        });
        return listTag;
    }

    public void loadBees(CompoundNBT nbt) {
        ListNBT listTag = nbt.getList(NBTConstants.NBT_BEES, 10);

        if (!listTag.isEmpty()) {
            for (int i = 0; i < listTag.size(); ++i) {
                CompoundNBT data = listTag.getCompound(i);

                String beeType = data.getString(NBTConstants.NBT_BEE_TYPE);
                String beeColor = data.contains(NBTConstants.NBT_COLOR) ? data.getString(NBTConstants.NBT_COLOR) : BeeConstants.VANILLA_BEE_COLOR;
                ITextComponent displayName = data.contains(NBTConstants.NBT_BEE_NAME) ? ITextComponent.Serializer.fromJson(data.getString(NBTConstants.NBT_BEE_NAME)) : new StringTextComponent("Temp Bee Name");

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
    public void loadFromNBT(CompoundNBT nbt) {
        super.loadFromNBT(nbt);
        loadBees(nbt);
        CompoundNBT invTag = nbt.getCompound(NBTConstants.NBT_INVENTORY);
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
    public CompoundNBT saveToNBT(CompoundNBT nbt) {
        super.saveToNBT(nbt);
        CompoundNBT inv = this.getTileStackHandler().serializeNBT();
        nbt.put(NBTConstants.NBT_INVENTORY, inv);
        nbt.put(NBTConstants.NBT_BEES, this.writeBees());
        nbt.putInt(NBTConstants.NBT_TIER, getTier());
        return nbt;
    }
    //endregion

    //region IMPORT/EXPORT
    public void importBee(ServerPlayerEntity player) {
        if (canImport()) {
            ItemStack filledJar = this.getTileStackHandler().getStackInSlot(IMPORT);
            BeeJar jarItem = (BeeJar) filledJar.getItem(); //TODO Fix this -oreo
            BeeEntity beeEntity = (BeeEntity) BeeJar.getEntityFromStack(filledJar, this.level, true);

            if (beeEntity != null && tryEnterHive(beeEntity, beeEntity.hasNectar(), true)) {
                player.displayClientMessage(IMPORT_SUCCESS, true);
                ItemStack emptyJar = this.getTileStackHandler().getStackInSlot(EMPTY_JAR);
                filledJar.shrink(1);
                if (emptyJar.isEmpty()) {
                    this.getTileStackHandler().setStackInSlot(EMPTY_JAR, new ItemStack(jarItem));
                } else {
                    emptyJar.grow(1);
                }
                return;
            }
        }
        player.displayClientMessage(IMPORT_FAILED, true);
    }

    private boolean canImport() {
        return !this.getTileStackHandler().getStackInSlot(IMPORT).isEmpty() && this.getTileStackHandler().getStackInSlot(EMPTY_JAR).getCount() < 16;
    }

    public void exportBee(ServerPlayerEntity player, String beeType) {
        boolean exported = false;
        ApiaryBee bee = this.bees.get(beeType);

        if (bee.isLocked() && getTileStackHandler().getStackInSlot(EXPORT).isEmpty() && !getTileStackHandler().getStackInSlot(EMPTY_JAR).isEmpty()) {
            exported = releaseBee(this.getBlockState(), bee, true);
        }
        if (exported) {
            this.bees.remove(beeType);
            this.getTileStackHandler().getStackInSlot(EMPTY_JAR).shrink(1);
            if (this.level != null && !this.level.isClientSide)
                syncApiaryToPlayersUsing();
            player.displayClientMessage(EXPORT_SUCCESS, true);
            return;
        }

        player.displayClientMessage(EXPORT_FAILED, true);
    }

    public void exportBee(BeeEntity beeEntity) {
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

    public static class ApiaryBee {
        public final CompoundNBT entityData;
        public final int minOccupationTicks;
        public final String beeType;
        private int ticksInHive;
        private boolean isLocked = false;
        public final String beeColor;
        public final ITextComponent displayName;

        public ApiaryBee(CompoundNBT nbt, int ticksInHive, int minOccupationTicks, String beeType, String beeColor, ITextComponent displayName) {
            nbt.remove("UUID");
            this.entityData = nbt;
            this.setTicksInHive(ticksInHive);
            this.minOccupationTicks = minOccupationTicks;
            this.beeType = beeType;
            this.beeColor = beeColor;
            this.displayName = displayName;
        }
        public ApiaryBee(CompoundNBT nbt, int ticksInHive, int minOccupationTicks, String beeType, String beeColor, ITextComponent  displayName, boolean isLocked) {
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
