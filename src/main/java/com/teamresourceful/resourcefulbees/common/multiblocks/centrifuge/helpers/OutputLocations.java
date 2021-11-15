package com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.helpers;

import com.teamresourceful.resourcefulbees.common.lib.constants.NBTConstants;
import com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.entities.base.ICentrifugeOutput;
import com.teamresourceful.resourcefulbees.common.utils.WorldUtils;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

public class OutputLocations<T extends TileEntity & ICentrifugeOutput<?>> {

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

    // merge this back into #deserialize in 1.18
    public void onLoad(Class<T> clazz, @Nullable World level) {
        for (int i = 0; i < 3; i++) {
            BlockPos pos = outputs[i].pos;
            if (pos != null) set(i, WorldUtils.getTileEntity(clazz, level, pos), pos);
        }
    }

    public void deserialize(CompoundNBT tag) {
        if (tag.contains(NBTConstants.NBT_LOCATIONS)) {
            ListNBT listTag = tag.getList(NBTConstants.NBT_LOCATIONS, Constants.NBT.TAG_COMPOUND);
            for (int i = 0; i < listTag.size(); i++) {
                CompoundNBT location = listTag.getCompound(i);
                if (location.getInt("id") != i) continue;
                BlockPos pos = NBTUtil.readBlockPos(location.getCompound("pos"));
                outputs[i].pos = pos;
                //set(i, WorldUtils.getTileEntity(clazz, level, pos), pos); //TODO reimplement this in 1.18
            }
        }
    }

    public CompoundNBT serialize() {
        ListNBT nbtTagList = new ListNBT();
        for (int i = 0; i < 3; i++) {
            Output<T> output = outputs[i];
            if (output.tile == null || output.pos == null) continue;
            CompoundNBT nbt = new CompoundNBT();
            nbt.putInt("id", i);
            nbt.put("pos", NBTUtil.writeBlockPos(output.pos));
            nbt.putBoolean("resultDeposited", output.deposited);
            nbtTagList.add(nbt);
        }
        CompoundNBT nbt = new CompoundNBT();
        nbt.put(NBTConstants.NBT_LOCATIONS, nbtTagList);
        return nbt;
    }

    public static class Output<T extends ICentrifugeOutput<?>> {
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
