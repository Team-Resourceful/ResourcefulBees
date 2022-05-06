package com.teamresourceful.resourcefulbees.common.blockentity;


import com.teamresourceful.resourcefulbees.api.IBeeCompat;
import com.teamresourceful.resourcefulbees.common.block.ApiaryBlock;
import com.teamresourceful.resourcefulbees.common.blockentity.base.GUISyncedBlockEntity;
import com.teamresourceful.resourcefulbees.common.inventory.AutomationSensitiveItemStackHandler;
import com.teamresourceful.resourcefulbees.common.inventory.menus.ApiaryMenu;
import com.teamresourceful.resourcefulbees.common.lib.constants.NBTConstants;
import com.teamresourceful.resourcefulbees.common.lib.constants.TranslationConstants;
import com.teamresourceful.resourcefulbees.common.lib.enums.ApiaryTier;
import com.teamresourceful.resourcefulbees.common.mixin.accessors.BeehiveEntityAccessor;
import com.teamresourceful.resourcefulbees.common.utils.BeeInfoUtils;
import com.teamresourceful.resourcefulbees.common.utils.ModUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BeehiveBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static com.teamresourceful.resourcefulbees.common.inventory.AutomationSensitiveItemStackHandler.ACCEPT_FALSE;
import static com.teamresourceful.resourcefulbees.common.inventory.AutomationSensitiveItemStackHandler.REMOVE_TRUE;
import static com.teamresourceful.resourcefulbees.common.lib.constants.BeeConstants.MIN_HIVE_TIME;

public class ApiaryBlockEntity extends GUISyncedBlockEntity {

    public final List<ApiaryBee> bees = new ArrayList<>();
    protected ApiaryTier tier;
    protected int ticksSinceBeesFlagged;

    private final AutomationSensitiveItemStackHandler inventory = new AutomationSensitiveItemStackHandler(27, ACCEPT_FALSE, REMOVE_TRUE);
    private final LazyOptional<IItemHandler> inventoryOptional = LazyOptional.of(() -> inventory);

    public ApiaryBlockEntity(ApiaryTier tier, BlockPos pos, BlockState state) {
        super(tier.getBlockEntityType(), pos, state);
        this.tier = tier;
    }

    public ApiaryTier getTier() {
        return tier;
    }

    public int getBeeCount() {
        return this.bees.size();
    }

    //region BEE HANDLING
    public boolean releaseBee(@NotNull BlockState state, ApiaryBee apiaryBee) {
        BlockPos blockPos = this.getBlockPos();
        Direction direction = state.getValue(BeehiveBlock.FACING);
        BlockPos blockPos1 = blockPos.relative(direction);
        CompoundTag nbt = apiaryBee.entityData;

        if (level != null && this.level.getBlockState(blockPos1).getCollisionShape(this.level, blockPos1).isEmpty()) {
            Entity entity = EntityType.loadEntityRecursive(nbt, this.level, entity1 -> entity1);
            if (entity instanceof IBeeCompat beeCompat) {
                BeeInfoUtils.setEntityLocationAndAngle(blockPos, direction, entity);
                deliverNectar(nbt, beeCompat);
                if (entity instanceof Animal animal) BeeInfoUtils.ageBee(apiaryBee.getTicksInHive(), animal);
                level.playSound(null, blockPos.getX(), blockPos.getY(), blockPos.getZ(), SoundEvents.BEEHIVE_EXIT, SoundSource.BLOCKS, 1.0F, 1.0F);
                level.addFreshEntity(entity);
            }
            return true;
        }
        return false;
    }

    private void deliverNectar(CompoundTag nbt, IBeeCompat bee) {
        if (nbt.getBoolean("HasNectar")) {
            bee.nectarDroppedOff();
            ItemStack stack = bee.getApiaryOutput(tier);
            for (int i = 0; i < inventory.getSlots() && !stack.isEmpty(); i++) { stack = ModUtils.insertItem(inventory, i, stack); }
        }
    }

    public void tryEnterHive(@NotNull Entity bee, boolean hasNectar, int ticksInHive) {
        if (this.level != null && hasSpace() && bee instanceof IBeeCompat beeCompat) {
            bee.ejectPassengers();
            CompoundTag nbt = new CompoundTag();
            bee.save(nbt);
            this.bees.add(new ApiaryBee(nbt, ticksInHive, hasNectar ? getMaxTimeInHive(beeCompat) : MIN_HIVE_TIME, bee.getName()));
            this.level.playSound(null, this.getBlockPos(), SoundEvents.BEEHIVE_ENTER, SoundSource.BLOCKS, 1.0F, 1.0F);
            bee.discard();
        }
    }

    private int getMaxTimeInHive(@NotNull IBeeCompat bee) {
        return (int) (bee.getMaxTimeInHive() * tier.getTimeModifier());
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, ApiaryBlockEntity apiaryTile) {
        ApiaryBee apiaryBee;
        Iterator<ApiaryBee> iterator = apiaryTile.bees.iterator();
        while (iterator.hasNext()) {
            apiaryBee = iterator.next();
            if (apiaryTile.canRelease(apiaryBee) && apiaryTile.releaseBee(state, apiaryBee)) iterator.remove();
            else apiaryBee.setTicksInHive(Math.min(apiaryBee.getTicksInHive() + 1, Integer.MAX_VALUE - 1));
        }

        if (!apiaryTile.bees.isEmpty() && level.getRandom().nextDouble() < 0.005D) {
            level.playSound(null, pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D, SoundEvents.BEEHIVE_WORK, SoundSource.BLOCKS, 1.0F, 1.0F);
        }

        apiaryTile.ticksSinceBeesFlagged++;
        if (apiaryTile.ticksSinceBeesFlagged == 80) {
            BeeInfoUtils.flagBeesInRange(pos, level);
            apiaryTile.ticksSinceBeesFlagged = 0;
        }
    }

    private boolean canRelease(ApiaryBee apiaryBee) {
        return !apiaryBee.isLocked() && apiaryBee.getTicksInHive() > apiaryBee.minOccupationTicks;
    }

    public boolean hasSpace() {
        return this.bees.size() < tier.getMaxBees();
    }

    public boolean isAllowedBee() {
        return getBlockState().getBlock() instanceof ApiaryBlock;
    }

    public void lockOrUnlockBee(int bee) {
        if (bee < bees.size() && bee >= 0) this.bees.get(bee).toggleLocked();
    }
    //endregion

    //region NBT HANDLING

    @NotNull
    public ListTag writeBees() {
        ListTag listTag = new ListTag();
        this.bees.forEach(apiaryBee -> {
            CompoundTag tag = new CompoundTag();
            tag.put("EntityData", apiaryBee.entityData);
            tag.putInt("TicksInHive", apiaryBee.getTicksInHive());
            tag.putInt("MinOccupationTicks", apiaryBee.minOccupationTicks);
            tag.putBoolean(NBTConstants.NBT_LOCKED, apiaryBee.isLocked());
            tag.putString(NBTConstants.NBT_BEE_NAME, Component.Serializer.toJson(apiaryBee.displayName));
            listTag.add(tag);
        });
        return listTag;
    }

    public void loadBees(CompoundTag nbt) {
        nbt.getList(NBTConstants.NBT_BEES, Tag.TAG_COMPOUND)
            .stream()
            .map(CompoundTag.class::cast)
            .forEachOrdered(data -> {
                Component displayName = data.contains(NBTConstants.NBT_BEE_NAME) ? Component.Serializer.fromJson(data.getString(NBTConstants.NBT_BEE_NAME)) : new TextComponent("Temp Bee Name");
                this.bees.add(new ApiaryBee(data.getCompound("EntityData"), data.getInt("TicksInHive"), data.getInt("MinOccupationTicks"), displayName, data.getBoolean(NBTConstants.NBT_LOCKED)));
            });
    }

    @Override
    public void load(@NotNull CompoundTag tag) {
        super.load(tag);
        inventory.deserializeNBTWithoutCheckingSize(tag.getCompound(NBTConstants.NBT_INVENTORY));
        readSyncData(tag.getCompound(NBTConstants.SYNC_DATA));
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put(NBTConstants.NBT_INVENTORY, inventory.serializeNBT());
        tag.put(NBTConstants.SYNC_DATA, getSyncData());
    }

    @Override
    public @NotNull CompoundTag getSyncData() {
        return ModUtils.nbtWithData(NBTConstants.NBT_BEES, writeBees());
    }

    @Override
    public void readSyncData(@NotNull CompoundTag tag) {
        bees.clear();
        loadBees(tag);
        bees.removeIf(bee -> BeeInfoUtils.getOptionalEntityType(bee.entityData.getString("id")).isEmpty());
    }
    //endregion

    @NotNull
    @Override
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        return cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY ? inventoryOptional.cast() : super.getCapability(cap, side);
    }

    @Override
    public @NotNull Component getDisplayName() {
        return TranslationConstants.Guis.APIARY;
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, @NotNull Inventory inventory, @NotNull Player player) {
        return new ApiaryMenu(id, inventory, this);
    }

    public AutomationSensitiveItemStackHandler getInventory() {
        return inventory;
    }

    public static class ApiaryBee {
        public final Component displayName;
        public final int minOccupationTicks;
        public final CompoundTag entityData;

        private boolean locked;
        private int ticksInHive;

        public ApiaryBee(CompoundTag entityData, int ticksInHive, int minOccupationTicks, Component displayName, boolean locked) {
            this(entityData, ticksInHive, minOccupationTicks, displayName);
            this.locked = locked;
        }

        public ApiaryBee(CompoundTag entityData, int ticksInHive, int minOccupationTicks, Component displayName) {
            BeehiveEntityAccessor.callRemoveIgnoredBeeTags(entityData);
            this.entityData = entityData;
            this.ticksInHive = ticksInHive;
            this.minOccupationTicks = minOccupationTicks;
            this.displayName = displayName;
        }

        public boolean isLocked() {
            return locked;
        }

        public void toggleLocked() {
            setLocked(!locked);
        }

        public void setLocked(boolean locked) {
            this.locked = locked;
        }

        public int getTicksInHive() {
            return ticksInHive;
        }

        public void setTicksInHive(int ticksInHive) {
            this.ticksInHive = ticksInHive;
        }
    }
}
