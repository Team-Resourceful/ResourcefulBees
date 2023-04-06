package com.teamresourceful.resourcefulbees.centrifuge.common.helpers;

import com.google.common.base.Suppliers;
import com.teamresourceful.resourcefulbees.centrifuge.common.entities.base.AbstractCentrifugeOutputEntity;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefulbees.common.lib.constants.NBTConstants;
import com.teamresourceful.resourcefulbees.common.recipe.recipes.centrifuge.outputs.AbstractOutput;
import com.teamresourceful.resourcefulbees.common.util.WorldUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

//@SuppressWarnings("all")
public class OutputLocationGroup<T extends AbstractCentrifugeOutputEntity<E, V>, E extends AbstractOutput<V>, V> {

    //TODO decide if most of the methods in this class should be moved to the inner class or not

    private final Output<T,E,V> location1 = new Output<>();
    private final Output<T,E,V> location2 = new Output<>();
    private final Output<T,E,V> location3 = new Output<>();

    public OutputLocationGroup() {
        setFirst(null, null);
        setSecond(null, null);
        setThird(null, null);
    }


    public void setFirst(@Nullable T tile, @Nullable BlockPos pos) {
        set(location1, tile, pos);
    }

    public void setSecond(@Nullable T tile, @Nullable BlockPos pos) {
        set(location2, tile, pos);
    }

    public void setThird(@Nullable T tile, @Nullable BlockPos pos) {
        set(location3, tile, pos);
    }

    public void set(int i, @Nullable T tile, @Nullable BlockPos pos) {
        switch (i) {
            case 0 -> set(location1, tile, pos);
            case 1 -> set(location2, tile, pos);
            case 2 -> set(location3, tile, pos);
            default -> ModConstants.LOGGER.warn("Invalid Output Location Given!");
        }
    }

    public void set(Output<T,E,V> output, @Nullable T tile, @Nullable BlockPos pos) {
        if (tile == null || pos == null ) {
            setAsSupplier(output, () -> null, null);
        } else {
            setAsSupplier(output, () -> tile, pos);
        }
    }

    //Might need to rework the contract/scope for this method, ideally this would only be used when we know for a fact
    // whatever data is being supplied would return both the pos and the tile.
    protected void setAsSupplier(Output<T,E,V> output, Supplier<@Nullable T> supplier, @Nullable BlockPos pos) {
        output.tileSupplier = supplier;
        output.pos = pos;
    }

    public Output<T,E,V> getFirst() {
        return location1;
    }

    public Output<T,E,V> getSecond() {
        return location2;
    }

    public Output<T,E,V> getThird() {
        return location3;
    }

    /**
     * Returns the specified output location.
     * This method will default to location 1 if any value other than 1 or 2 is passed.
     *
     * @param i Location index
     * @return The output location
     */
    public Output<T,E,V> get(int i) {
        return switch (i) {
            case 1 -> location2;
            case 2 -> location3;
            default -> location1;
        };
    }

    /**
     * Returns the True if the output was deposited for the specific output location.
     * This method will default to location 1 if any value other than 1 or 2 is passed.
     *
     * @param i Location index
     * @return whether the result was deposited or not
     */
    public boolean depositResult(int i, E result, int processQuantity) {
        return switch (i) {
            case 1 -> location2.depositResult(result, processQuantity);
            case 2 -> location3.depositResult(result, processQuantity);
            default -> location1.depositResult(result, processQuantity);
        };
    }

    public void resetProcessData() {
        location1.deposited = false;
        location2.deposited = false;
        location3.deposited = false;
    }

    public boolean allLinked() {
        return location1.isLinked() && location2.isLinked() && location3.isLinked();
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
                get(index).pos = pos;
                this.setAsSupplier(get(index), Suppliers.memoize(() -> WorldUtils.getTileEntity(clazz, levelSupplier, pos)), pos);

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
            Output<T,E,V> output = get(i);
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

    public static class Output<T extends AbstractCentrifugeOutputEntity<E, V>, E extends AbstractOutput<V>, V> {
        private Supplier<T> tileSupplier;
        private @Nullable BlockPos pos = null;
        private boolean deposited = false;
        //private int rollsLeft = 0;

        public @Nullable T getTile() {
            return tileSupplier.get();
        }

        public @Nullable BlockPos getPos() {
            return pos;
        }

        public boolean isLinked() {
            return pos != null && tileSupplier.get() != null;
        }

        public boolean depositResult(E result, int processQuantity) {
            if (getTile() == null) return false;
            if (!deposited) {
                deposited = getTile().depositResult(result, processQuantity);
            }
            return deposited;
        }
    }
}
