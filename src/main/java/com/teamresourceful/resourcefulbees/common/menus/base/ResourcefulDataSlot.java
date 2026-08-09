package com.teamresourceful.resourcefulbees.common.menus.base;

import net.minecraft.world.inventory.DataSlot;

public class ResourcefulDataSlot extends DataSlot {

    private int value;

    @Override
    public int get() {
        return this.value;
    }

    @Override
    public void set(int value) {
        this.value = value;
    }

    public int increment() {
        return this.value++;
    }

    public int increment(int value) {
        return this.value += value;
    }

    public int decrement() {
        return this.value--;
    }

    public int decrement(int value) {
        return this.value -= value;
    }
}
