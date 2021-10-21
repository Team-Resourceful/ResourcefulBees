package com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.helpers;

public enum CentrifugeTier {
    ERROR("error", 0, 0, 0, 0),
    BASIC("basic", 1, 4000, 50000, 512),
    ADVANCED("advanced", 4, 16000, 500000, 8192),
    ELITE("elite", 8, 64000, 5000000, 65536),
    ULTIMATE("ultimate", 16, 256000, 50000000, 262144);

    private final String name;
    private final int slots;
    private final int tankCapacity;
    private final int energyCapacity;
    private final int energyReceiveRate;

    CentrifugeTier(String name, int slots, int tankCapacity, int energyCapacity, int energyReceiveRate) {
        this.name = name;
        this.slots = slots;
        this.tankCapacity = tankCapacity;
        this.energyCapacity = energyCapacity;
        this.energyReceiveRate = energyReceiveRate;
    }

    public String getName() {
        return name;
    }

    public int getSlots() {
        return slots;
    }

    public int getTankCapacity() {
        return tankCapacity;
    }

    public int getEnergyCapacity() {
        return energyCapacity;
    }

    public int getEnergyReceiveRate() {
        return energyReceiveRate;
    }
}
