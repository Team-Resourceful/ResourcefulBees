package com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.helpers;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.teamresourceful.resourcefulbees.ResourcefulBees;
import com.teamresourceful.resourcefulbees.common.lib.constants.NBTConstants;
import com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.entities.base.ICentrifugeOutput;
import com.teamresourceful.resourcefulbees.common.utils.MathUtils;
import com.teamresourceful.resourcefulbees.common.utils.WorldUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("all")
public class OutputLocationGroup<T extends BlockEntity & ICentrifugeOutput<?>> {

    //TODO decide if most of the methods in this class should be moved to the inner class or not

    public OutputLocationGroup() {
        setFirst(null, null);
        setSecond(null, null);
        setThird(null, null);
    }

    //TODO test for class cast exceptions then suppress warnings
    private final Output<T>[] outputs = new Output[] { new Output<T>(), new Output<T>(), new Output<T>() };

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
        /*output.tile = pos == null ? null : tile;
        output.pos = tile == null ? null : pos;*/

        if (tile == null || pos == null ) {
            setAsSupplier(output, () -> null, null);
        } else {
            setAsSupplier(output, () -> tile, pos);
        }
    }

    //Might need to rework the contract/scope for this method, ideally this would only be used when we know for a fact
    // whatever data is being supplied would return both the pos and the tile.
    protected void setAsSupplier(Output<T> output, Supplier<@Nullable T> supplier, @Nullable BlockPos pos) {
        output.tileSupplier = supplier;
        output.pos = pos;
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
    /*public void onLoad(Class<T> clazz, @Nullable Level level) {
        for (int i = 0; i < 3; i++) {
            BlockPos pos = outputs[i].pos;
            if (pos != null) set(i, WorldUtils.getTileEntity(clazz, level, pos), pos);
        }
    }*/

    public void deserialize(CompoundTag tag, Class<T> clazz, Supplier<@Nullable Level> levelSupplier) {
        if (tag.contains(NBTConstants.NBT_LOCATIONS)) {
            ListTag listTag = tag.getList(NBTConstants.NBT_LOCATIONS, Tag.TAG_COMPOUND);
            for (int i = 0; i < listTag.size(); i++) {
                CompoundTag location = listTag.getCompound(i);
                int index = location.getInt("id");
                BlockPos pos = NbtUtils.readBlockPos(location.getCompound("pos"));
                outputs[index].pos = pos;
                this.setAsSupplier(outputs[index], Suppliers.memoize(() -> WorldUtils.getTileEntity(clazz, levelSupplier, pos)), pos);

                //look into a way to defer the deserialization of this nbt if possible. passing "level" in is just passing a null value
                //that isn't an updatable reference so when the WorldUtils.getTileEntity is called level is null
                //and thus it fails to get the tile

                // keeping this comment just in case *do not delete*
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
        //private @Nullable T tile = null;
        private Supplier<T> tileSupplier;
        private @Nullable BlockPos pos = null;
        private boolean deposited = false;
        //private int rollsLeft = 0;

        /*public @Nullable T getTile() {
            return tile;
        }*/

        public @Nullable T getTile() {
            return tileSupplier.get();
        }

        public @Nullable BlockPos getPos() {
            return pos;
        }
    }
}
