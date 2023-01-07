package com.teamresourceful.resourcefulbees.centrifuge.common.helpers;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringRepresentable;
import net.minecraftforge.common.IExtensibleEnum;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public enum CentrifugeTier implements IExtensibleEnum, StringRepresentable {
    ERROR("error", 0, 0, 0, 0),
    BASIC("basic", 1, 4000, 50000, 512),
    ADVANCED("advanced", 4, 16000, 500000, 8192),
    ELITE("elite", 8, 64000, 5000000, 65536),
    ULTIMATE("ultimate", 16, 256000, 50000000, 262144);

    public static final Codec<CentrifugeTier> CODEC = IExtensibleEnum.createCodecForExtensibleEnum(CentrifugeTier::values, CentrifugeTier::byName);
    private static final Map<String, CentrifugeTier> BY_NAME = Arrays.stream(values()).collect(Collectors.toMap(CentrifugeTier::getName, tier -> tier));
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

    public int getContainerRows() {
        return this == BASIC ? 1 : slots / 4;
    }

    public int getContainerColumns() {
        return this == BASIC ? 1 : 4;
    }

    public static CentrifugeTier byName(String s) {
        return BY_NAME.get(s);
    }

    @SuppressWarnings("unused")
    public static CentrifugeTier create(String name, String id, int slots, int tankCapacity, int energyCapacity, int energyReceiveRate) {
        throw new IllegalStateException("Enum not extended");
    }

    @Override
    public @NotNull String getSerializedName() {
        return this.name;
    }
}
