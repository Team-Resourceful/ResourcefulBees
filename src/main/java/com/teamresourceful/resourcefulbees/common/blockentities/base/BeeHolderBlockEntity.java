package com.teamresourceful.resourcefulbees.common.blockentities.base;

import com.teamresourceful.resourcefulbees.api.compat.BeeCompat;
import com.teamresourceful.resourcefulbees.common.components.Bees;
import com.teamresourceful.resourcefulbees.common.menus.content.PositionContent;
import com.teamresourceful.resourcefulbees.common.registries.minecraft.ModDataComponents;
import com.teamresourceful.resourcefulbees.common.util.EntityUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BeehiveBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public abstract class BeeHolderBlockEntity extends GUISyncedBlockEntity implements ContentContainerBlock<PositionContent> {

    protected final List<BlockBee> bees = new ArrayList<>();
    protected int ticksSinceBeesFlagged;

    protected BeeHolderBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public int beeCount() {
        return bees.size();
    }

    public List<BlockBee.Occupant> getBees() {
        return this.bees.stream().map(BlockBee::toOccupant).toList();
    }

    public boolean releaseBee(@NotNull BlockState state, BlockBee.Occupant apiaryBee) {
        BlockPos blockPos = this.getBlockPos();
        Direction direction = state.getValue(BeehiveBlock.FACING);
        BlockPos blockPos1 = blockPos.relative(direction);
        if (level != null && this.level.getBlockState(blockPos1).getCollisionShape(this.level, blockPos1).isEmpty()) {
            Entity entity = apiaryBee.createEntity(level, blockPos);
            if (entity != null) {
                EntityUtils.setEntityLocationAndAngle(blockPos, direction, entity);
                deliverNectar(apiaryBee.hasNectar(), entity);
                level.playSound(null, blockPos.getX(), blockPos.getY(), blockPos.getZ(), SoundEvents.BEEHIVE_EXIT, SoundSource.BLOCKS, 1.0F, 1.0F);
                level.addFreshEntity(entity);
            }
            return true;
        }
        return false;
    }

    protected abstract void deliverNectar(boolean hasNectar, Entity bee);

    public void tryEnterHive(@NotNull Entity bee) {
        if (this.level != null && hasSpace() && bee instanceof BeeCompat beeCompat) {
            bee.ejectPassengers();
            storeBee(BlockBee.Occupant.of(bee, beeCompat, this, false));
            this.level.playSound(null, this.getBlockPos(), SoundEvents.BEEHIVE_ENTER, SoundSource.BLOCKS, 1.0F, 1.0F);
            bee.discard();
        }
    }

    public void storeBee(BlockBee.Occupant occupant) {
        this.bees.add(new BlockBee(occupant));
    }

    protected abstract int getMaxTimeInHive(@NotNull BeeCompat bee);

    public static <T extends BeeHolderBlockEntity> void serverTick(Level level, BlockPos pos, BlockState state, T holder) {
        boolean dirty = false;
        BlockBee bee;
        Iterator<BlockBee> iterator = holder.bees.iterator();
        while (iterator.hasNext()) {
            bee = iterator.next();
            if (bee.tick() && holder.releaseBee(state, bee.toOccupant())) {
                iterator.remove();
            }
            dirty = true;
        }

        if (dirty) {
            setChanged(level, pos, state);
        }

        if (!holder.bees.isEmpty() && level.getRandom().nextDouble() < 0.005D) {
            level.playSound(null, pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D, SoundEvents.BEEHIVE_WORK, SoundSource.BLOCKS, 1.0F, 1.0F);
        }

        if (holder.ticksSinceBeesFlagged++ == 80) {
            EntityUtils.flagBeesInRange(pos, level);
            holder.ticksSinceBeesFlagged = 0;
        }
    }

    public abstract boolean hasSpace();

    public abstract boolean isAllowedBee();

    public void lockOrUnlockBee(int bee) {
        if (bee < bees.size() && bee >= 0) this.bees.get(bee).toggleLocked();
    }

    @Override
    public PositionContent createContent(ServerPlayer player) {
        return new PositionContent(this.worldPosition);
    }

    //region NBT

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        this.bees.clear();
        input.read("bees", BlockBee.Occupant.LIST_CODEC).orElse(List.of()).forEach(this::storeBee);
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.store("bees", BlockBee.Occupant.LIST_CODEC, this.getBees());
    }

    @Override
    protected void applyImplicitComponents(@NonNull DataComponentGetter components) {
        super.applyImplicitComponents(components);
        this.bees.clear();
        List<BlockBee.Occupant> bees = components.getOrDefault(ModDataComponents.BEES, Bees.EMPTY).bees();
        bees.forEach(this::storeBee);
    }

    @Override
    protected void collectImplicitComponents(DataComponentMap.@NonNull Builder components) {
        super.collectImplicitComponents(components);
        components.set(ModDataComponents.BEES, new Bees(this.getBees()));
    }

    @Override
    public void removeComponentsFromTag(@NonNull ValueOutput output) {
        super.removeComponentsFromTag(output);
        output.discard("bees");
    }

    @Override
    public ListTag getSyncData(ListTag tag) {
        return (ListTag) Bees.CODEC.encodeStart(NbtOps.INSTANCE, new Bees(this.getBees())).result().orElse(new ListTag(0));
    }

    @Override
    public void readSyncData(@NotNull CompoundTag tag) {
        bees.clear();
        Bees.CODEC.parse(NbtOps.INSTANCE, tag);
        //  todo figure out how to better safeguard null entity types since getString doesn't exist
        //   getBees().removeIf(bee -> EntityType.byString(String.valueOf(bee.entityData().getString("id"))).isEmpty());
    }











    //    @Override
//    public @NotNull CompoundTag getSyncData() {
//        return writeBees();
//    }
//
//    @Override
//    public void readSyncData(@NotNull CompoundTag tag) {
//        bees.clear();
//        loadBees(tag);
//        bees.removeIf(bee -> EntityType.byString(String.valueOf(bee.entityData.getString("id"))).isEmpty());
//    }
    //endregion

}
