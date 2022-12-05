package com.teamresourceful.resourcefulbees.common.inventory;

import com.teamresourceful.resourcefulbees.common.util.MathUtils;
import net.minecraft.world.inventory.ContainerData;

public class BoundSafeContainerData implements ContainerData {

    private final int[] ints;
    private final int defaultVal;

    public BoundSafeContainerData(int size, int defaultVal) {
        this.ints = new int[size];
        this.defaultVal = defaultVal;
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
        if (!inBounds(i)) return;
        ints[i]++;
    }

    public void increment(int i, int val) {
        if (!inBounds(i)) return;
        ints[i]+=val;
    }

    public void decrement(int i) {
        if (!inBounds(i)) return;
        ints[i]--;
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
}
