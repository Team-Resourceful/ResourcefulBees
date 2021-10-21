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

public class OutputLocations<T extends TileEntity & ICentrifugeOutput<?>> {

    private final Output<T> first = new Output<>();
    private final Output<T> second = new Output<>();
    private final Output<T> third = new Output<>();

    public void setFirst(@Nullable T tile, @Nullable BlockPos pos) {
        set(first, tile, pos);
    }

    public void setSecond(@Nullable T tile, @Nullable BlockPos pos) {
        set(second, tile, pos);
    }

    public void setThird(@Nullable T tile, @Nullable BlockPos pos) {
        set(third, tile, pos);
    }

    public Output<T> getFirst() {
        return first;
    }

    public Output<T> getSecond() {
        return second;
    }

    public Output<T> getThird() {
        return third;
    }

    public Output<T> get(int i) {
        switch (i) {
            case 2: return third;
            case 1: return second;
            default: return first;
        }
    }

    public void set(int i, @Nullable T tile, @Nullable BlockPos pos) {
        switch (i) {
            case 2: setThird(tile, pos); break;
            case 1: setSecond(tile, pos); break;
            default: setFirst(tile, pos);
        }
    }

    public void set(Output<T> output, @Nullable T tile, @Nullable BlockPos pos) {
        output.tile = pos == null ? null : tile;
        output.pos = tile == null ? null : pos;
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean isDeposited(int i) {
        switch (i) {
            case 2: return third.deposited;
            case 1: return second.deposited;
            default: return first.deposited;
        }
    }

    public void setDeposited(int i, boolean deposited) {
        switch (i) {
            case 2: third.deposited = deposited; break;
            case 1: second.deposited = deposited; break;
            default: first.deposited = deposited;
        }
    }

    public void resetDeposited() {
        first.deposited = false;
        second.deposited =false;
        third.deposited = false;
    }

    public void deserialize(CompoundNBT tag, Class<T> clazz, @Nullable World level) {
        if (tag.contains(NBTConstants.NBT_LOCATIONS)) {
            ListNBT listTag = tag.getList(NBTConstants.NBT_LOCATIONS, Constants.NBT.TAG_COMPOUND);
            for (int i = 0; i < listTag.size(); i++) {
                CompoundNBT location = listTag.getCompound(i);
                if (location.getInt("id") != i) continue;
                BlockPos pos = NBTUtil.readBlockPos(location.getCompound("pos"));
                set(i, WorldUtils.getTileEntity(clazz, level, pos), pos);
            }
        }
    }

    public CompoundNBT serialize() {
        ListNBT nbtTagList = new ListNBT();
        for (int i = 0; i < 3; i++) {
            Output<T> output = get(i);
            if (output.tile == null || output.pos == null) continue;
            CompoundNBT location = new CompoundNBT();
            location.putInt("id", i);
            location.put("pos", NBTUtil.writeBlockPos(output.pos));
            nbtTagList.add(location);
        }
        CompoundNBT nbt = new CompoundNBT();
        nbt.put(NBTConstants.NBT_LOCATIONS, nbtTagList);
        return nbt;
    }

    public static class Output<T extends ICentrifugeOutput<?>> {
        private @Nullable T tile = null;
        private @Nullable BlockPos pos = null;
        private boolean deposited = false;

        public @Nullable T getTile() {
            return tile;
        }

        public @Nullable BlockPos getPos() {
            return pos;
        }
    }
}
