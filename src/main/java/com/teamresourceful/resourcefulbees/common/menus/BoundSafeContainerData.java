package com.teamresourceful.resourcefulbees.common.menus;

import com.teamresourceful.resourcefulbees.common.lib.util.MathUtils;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.common.util.ValueIOSerializable;

import java.util.Arrays;

public class BoundSafeContainerData implements ContainerData, ValueIOSerializable {

    private static final String VALUES_KEY = "values";

    private final int[] ints;
    private final int defaultVal;

    public BoundSafeContainerData(int size, int defaultVal) {
        this.ints = new int[size];
        this.defaultVal = defaultVal;
        Arrays.fill(this.ints, defaultVal);
    }

    @Override
    public int get(int i) {
        return inBounds(i) ? ints[i] : defaultVal;
    }

    @Override
    public void set(int i, int val) {
        if (!inBounds(i)) return;
        ints[i] = val;
    }

    public void increment(int i) {
        increment(i, 1);
    }

    public void increment(int i, int val) {
        if (!inBounds(i)) return;
        ints[i]+=val;
    }

    public void decrement(int i) {
        decrement(i, 1);
    }

    public void decrement(int i, int val) {
        if (!inBounds(i)) return;
        ints[i]-=val;
    }

    private boolean inBounds(int i) {
        return MathUtils.inRangeInclusive(i, 0, getCount() - 1);
    }

    @Override
    public int getCount() {
        return ints.length;
    }

    public void serialize(ValueOutput output) {
        output.putIntArray(VALUES_KEY, ints);
    }

    public void deserialize(ValueInput input) {
        input.getIntArray(VALUES_KEY).ifPresent(values -> {
            int copyLength = Math.min(values.length, ints.length);

            System.arraycopy(values, 0, ints, 0, copyLength);

            // Reset any entries not present in the serialized array.
            if (copyLength < ints.length) {
                Arrays.fill(ints, copyLength, ints.length, defaultVal);
            }
        });
    }
}
