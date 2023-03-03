package com.teamresourceful.resourcefulbees.centrifuge.common.helpers;

import com.teamresourceful.resourcefullib.common.inventory.IntContainerData;

public class ProcessContainerData extends IntContainerData {

    private static final int TIME = 0; //ticks left to complete processed recipe
    private static final int ENERGY = 1; //energy needed per tick for processed recipe

    public ProcessContainerData() {
        super(2);
    }

    public int getTime() {
        return getInt(TIME);
    }

    public int getEnergy() {
        return getInt(ENERGY);
    }

    public void decreaseTime() {
        setInt(TIME, getInt(TIME) - 1);
    }

    public void setTime(int time) {
        setInt(TIME, time);
    }

    public void setEnergy(int energy) {
        setInt(ENERGY, energy);
    }

    public void reset() {
        setInt(TIME, 0);
        setInt(ENERGY, 0);
    }
}
