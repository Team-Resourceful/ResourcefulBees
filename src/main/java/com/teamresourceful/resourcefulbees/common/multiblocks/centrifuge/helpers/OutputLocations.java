package com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.helpers;

import com.teamresourceful.resourcefulbees.ResourcefulBees;
import com.teamresourceful.resourcefulbees.common.lib.constants.NBTConstants;
import com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.entities.base.ICentrifugeOutput;
import com.teamresourceful.resourcefulbees.common.utils.MathUtils;
import com.teamresourceful.resourcefulbees.common.utils.WorldUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

@SuppressWarnings("all")
public class OutputLocations<T extends BlockEntity & ICentrifugeOutput<?>> {

    //TODO decide if most of the methods in this class should be moved to the inner class or not

    //TODO test for class cast exceptions then suppress warnings
    private final Output<T>[] outputs = Arrays.asList(new Output<T>(), new Output<T>(), new Output<T>()).toArray(new Output[3]);

    public void setFirst(@Nullable T tile, @Nullable BlockPos pos) {
        set(outputs[0], tile, pos);
    }

    public void setSecond(@Nullable T tile, @Nullable BlockPos pos) {
        set(outputs[1], tile, pos);
    }

    public void setThird(@Nullable T tile, @Nullable BlockPos pos) {
        set(outputs[2], tile, pos);
    }

    public void set(int i, @Nullable T tile, @Nullable BlockPos pos) {
        set(outputs[i], tile, pos);
    }

    public void set(Output<T> output, @Nullable T tile, @Nullable BlockPos pos) {
        output.tile = pos == null ? null : tile;
        output.pos = tile == null ? null : pos;
    }

    public Output<T> getFirst() {
        return outputs[0];
    }

    public Output<T> getSecond() {
        return outputs[1];
    }

    public Output<T> getThird() {
        return outputs[2];
    }

    public Output<T> get(int i) {
        if (!MathUtils.inRangeInclusive(i, 0, 2)) {
            ResourcefulBees.LOGGER.warn("Invalid Output Location Requested!");
            return outputs[0];
        }
        return outputs[i];
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean isDeposited(int i) {
        return outputs[i].deposited;
    }

    public void setDeposited(int i, boolean deposited) {
        outputs[i].deposited = deposited;
    }

    public void resetProcessData() {
        for (int i = 0; i < 3; i++) {
            outputs[i].deposited = false;
            //outputs[i].rollsLeft = 0;
        }
    }

/*    public int getRollsLeft(int i) {
        return outputs[i].rollsLeft;
    }

    public void setRollsLeft(int i, int rollsLeft) {
        outputs[i].rollsLeft = rollsLeft;
    }

    public void decreaseRollsLeft(int i) {
        outputs[i].rollsLeft--;
    }*/

    // this is going to need to stay for the time being
    public void onLoad(Class<T> clazz, @Nullable Level level) {
        for (int i = 0; i < 3; i++) {
            BlockPos pos = outputs[i].pos;
            if (pos != null) set(i, WorldUtils.getTileEntity(clazz, level, pos), pos);
        }
    }

    public void deserialize(CompoundTag tag) { //, Class<T> clazz, @Nullable Level level) {
        if (tag.contains(NBTConstants.NBT_LOCATIONS)) {
            ListTag listTag = tag.getList(NBTConstants.NBT_LOCATIONS, Tag.TAG_COMPOUND);
            for (int i = 0; i < listTag.size(); i++) {
                CompoundTag location = listTag.getCompound(i);
                if (location.getInt("id") != i) continue;
                BlockPos pos = NbtUtils.readBlockPos(location.getCompound("pos"));
                outputs[i].pos = pos;
                //this.set(i, WorldUtils.getTileEntity(clazz, level, pos), pos);
                // due to LevelChunk#promotePendingBlockEntity I can't safely use this method
                // here bc the level isn't set until AFTER the nbt data is read.
            }
        }
    }

    public CompoundTag serialize() {
        ListTag nbtTagList = new ListTag();
        for (int i = 0; i < 3; i++) {
            Output<T> output = outputs[i];
            //if (output.tile == null || output.pos == null) continue; retaining in case I have to revert this.
            if (output.pos == null) continue;
            CompoundTag nbt = new CompoundTag();
            nbt.putInt("id", i);
            nbt.put("pos", NbtUtils.writeBlockPos(output.pos));
            nbt.putBoolean("resultDeposited", output.deposited);
            nbtTagList.add(nbt);
        }
        CompoundTag nbt = new CompoundTag();
        nbt.put(NBTConstants.NBT_LOCATIONS, nbtTagList);
        return nbt;
    }

    public static class Output<T extends ICentrifugeOutput<?>> {
        //TODO look into switching this to a memoized supplier
        private @Nullable T tile = null;
        private @Nullable BlockPos pos = null;
        private boolean deposited = false;
        //private int rollsLeft = 0;

        public @Nullable T getTile() {
            return tile;
        }

        public @Nullable BlockPos getPos() {
            return pos;
        }
    }
}
