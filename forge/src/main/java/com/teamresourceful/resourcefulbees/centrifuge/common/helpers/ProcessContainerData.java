package com.teamresourceful.resourcefulbees.centrifuge.common.helpers;

import com.teamresourceful.resourcefulbees.common.util.MathUtils;
import net.minecraft.world.inventory.ContainerData;

public class ProcessContainerData implements ContainerData {

    private static final int TIME = 0; //ticks left to complete processed recipe
    private static final int ENERGY = 1; //energy needed per tick for processed recipe

    private final int[] data = new int[2];

    public int getTime() {
        return data[TIME];
    }

    public int getEnergy() {
        return data[ENERGY];
    }

    public void decreaseTime() {
        data[TIME]--;
    }

    public void setTime(int time) {
        data[TIME] = time;
    }

    public void setEnergy(int energy) {
        data[ENERGY] = energy;
    }

    public void reset() {
        data[TIME] = 0;
        data[ENERGY] = 0;
    }

    @Override
    public int get(int index) {
        return inBounds(index) ? data[index] : Integer.MIN_VALUE;
    }

    @Override
    public void set(int index, int value) {
        if (!inBounds(index)) return;
        data[index] = value;
    }

    @Override
    public int getCount() {
        return 2;
    }

    private boolean inBounds(int index) {
        return MathUtils.inRangeInclusive(index, 0, getCount()-1);
    }
}
