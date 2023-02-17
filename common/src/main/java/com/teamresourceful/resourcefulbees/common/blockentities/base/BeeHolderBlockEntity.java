package com.teamresourceful.resourcefulbees.common.blockentities.base;

import com.teamresourceful.resourcefulbees.api.compat.BeeCompat;
import com.teamresourceful.resourcefulbees.common.lib.constants.BeeConstants;
import com.teamresourceful.resourcefulbees.common.lib.constants.NBTConstants;
import com.teamresourceful.resourcefulbees.common.util.EntityUtils;
import com.teamresourceful.resourcefullib.common.utils.TagUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BeehiveBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static com.teamresourceful.resourcefulbees.common.lib.constants.BeeConstants.MIN_HIVE_TIME;

public abstract class BeeHolderBlockEntity extends GUISyncedBlockEntity {

    protected final List<BlockBee> bees = new ArrayList<>();
    protected int ticksSinceBeesFlagged;

    protected BeeHolderBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public int beeCount() {
        return bees.size();
    }

    public List<BlockBee> getBees() {
        return bees;
    }

    public boolean releaseBee(@NotNull BlockState state, BlockBee apiaryBee) {
        BlockPos blockPos = this.getBlockPos();
        Direction direction = state.getValue(BeehiveBlock.FACING);
        BlockPos blockPos1 = blockPos.relative(direction);
        CompoundTag nbt = apiaryBee.entityData;

        if (level != null && this.level.getBlockState(blockPos1).getCollisionShape(this.level, blockPos1).isEmpty()) {
            Entity entity = EntityType.loadEntityRecursive(nbt, this.level, entity1 -> entity1);
            if (entity != null) {
                EntityUtils.setEntityLocationAndAngle(blockPos, direction, entity);
                deliverNectar(nbt, entity);
                if (entity instanceof Animal animal) {
                    EntityUtils.ageBee(apiaryBee.getTicksInHive(), animal);
                }
                level.playSound(null, blockPos.getX(), blockPos.getY(), blockPos.getZ(), SoundEvents.BEEHIVE_EXIT, SoundSource.BLOCKS, 1.0F, 1.0F);
                level.addFreshEntity(entity);
            }
            return true;
        }
        return false;
    }

    protected abstract void deliverNectar(CompoundTag nbt, Entity bee);

    public void tryEnterHive(@NotNull Entity bee, boolean hasNectar, int ticksInHive) {
        if (this.level != null && hasSpace() && bee instanceof BeeCompat beeCompat) {
            bee.ejectPassengers();
            CompoundTag nbt = new CompoundTag();
            bee.save(nbt);
            String beeColor = EntityUtils.getBeeColorOrDefault(bee);
            this.bees.add(new BlockBee(nbt, ticksInHive, hasNectar ? getMaxTimeInHive(beeCompat) : MIN_HIVE_TIME, bee.getName(), beeColor));
            this.level.playSound(null, this.getBlockPos(), SoundEvents.BEEHIVE_ENTER, SoundSource.BLOCKS, 1.0F, 1.0F);
            bee.discard();
        }
    }

    protected abstract int getMaxTimeInHive(@NotNull BeeCompat bee);

    public static <T extends BeeHolderBlockEntity> void serverTick(Level level, BlockPos pos, BlockState state, T holder) {
        boolean dirty = false;
        BlockBee bee;
        Iterator<BlockBee> iterator = holder.bees.iterator();
        while (iterator.hasNext()) {
            bee = iterator.next();
            if (holder.canRelease(bee) && holder.releaseBee(state, bee)) {
                iterator.remove();
            } else {
                bee.incrementTicksInHive(1);
            }
            dirty = true;
        }

        if (dirty) {
            setChanged(level, pos, state);
        }

        if (!holder.bees.isEmpty() && level.getRandom().nextDouble() < 0.005D) {
            level.playSound(null, pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D, SoundEvents.BEEHIVE_WORK, SoundSource.BLOCKS, 1.0F, 1.0F);
        }

        holder.ticksSinceBeesFlagged++;
        if (holder.ticksSinceBeesFlagged == 80) {
            EntityUtils.flagBeesInRange(pos, level);
            holder.ticksSinceBeesFlagged = 0;
        }
    }

    protected boolean canRelease(BlockBee apiaryBee) {
        return !apiaryBee.isLocked() && apiaryBee.getTicksInHive() > apiaryBee.minOccupationTicks;
    }

    public abstract boolean hasSpace();

    public abstract boolean isAllowedBee();

    public void lockOrUnlockBee(int bee) {
        if (bee < bees.size() && bee >= 0) this.bees.get(bee).toggleLocked();
    }

    //region NBT
    @NotNull
    public CompoundTag writeBees() {
        ListTag listTag = new ListTag();
        this.bees.forEach(apiaryBee -> {
            CompoundTag tag = new CompoundTag();
            tag.put("EntityData", apiaryBee.entityData);
            tag.putInt("TicksInHive", apiaryBee.getTicksInHive());
            tag.putInt("MinOccupationTicks", apiaryBee.minOccupationTicks);
            tag.putBoolean(NBTConstants.NBT_LOCKED, apiaryBee.isLocked());
            tag.putString(NBTConstants.NBT_BEE_NAME, Component.Serializer.toJson(apiaryBee.displayName));
            tag.putString(NBTConstants.BeeJar.COLOR, apiaryBee.color);
            listTag.add(tag);
        });
        return TagUtils.tagWithData(NBTConstants.NBT_BEES, listTag);
    }

    public void loadBees(CompoundTag nbt) {
        nbt.getList(NBTConstants.NBT_BEES, Tag.TAG_COMPOUND)
            .stream()
            .map(CompoundTag.class::cast)
            .forEachOrdered(data -> {
                Component displayName = data.contains(NBTConstants.NBT_BEE_NAME) ? Component.Serializer.fromJson(data.getString(NBTConstants.NBT_BEE_NAME)) : Component.literal("Temp Bee Name");
                String beeColor = data.contains(NBTConstants.BeeJar.COLOR) ? data.getString(NBTConstants.BeeJar.COLOR) : BeeConstants.VANILLA_BEE_COLOR;
                this.bees.add(new BlockBee(data.getCompound("EntityData"), data.getInt("TicksInHive"), data.getInt("MinOccupationTicks"), displayName, beeColor, data.getBoolean(NBTConstants.NBT_LOCKED)));
            });
    }

    @Override
    public @NotNull CompoundTag getSyncData() {
        return writeBees();
    }

    @Override
    public void readSyncData(@NotNull CompoundTag tag) {
        bees.clear();
        loadBees(tag);
        bees.removeIf(bee -> EntityType.byString(bee.entityData.getString("id")).isEmpty());
    }
    //endregion

}
