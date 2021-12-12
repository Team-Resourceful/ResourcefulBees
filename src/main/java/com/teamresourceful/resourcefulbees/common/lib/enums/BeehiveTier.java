package com.teamresourceful.resourcefulbees.common.lib.enums;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringRepresentable;
import net.minecraftforge.common.IExtensibleEnum;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public enum BeehiveTier implements IExtensibleEnum, StringRepresentable {
    //TODO Balance these numbers against the apiary
    //TODO Also determine a better naming scheme
    ERROR("error", 0, 0, 0), //WTF
    NEST("nest", 2, 4, 1.1), //nest
    T1_HIVE("t1_hive", 4, 8, 1), //normal
    T2_HIVE("t2_hive", 6, 16, 0.9), //T2
    T3_HIVE("t3_hive", 8, 32, 0.8); //T3

    public static final Codec<BeehiveTier> CODEC = IExtensibleEnum.createCodecForExtensibleEnum(BeehiveTier::values, BeehiveTier::byName);
    private static final Map<String, BeehiveTier> BY_NAME = Arrays.stream(values()).collect(Collectors.toMap(BeehiveTier::getName, tier -> tier));
    private final String name;
    private final int maxBees;
    private final int maxCombs;
    private final double timeModifier;

    BeehiveTier(String name, int maxBees, int maxCombs, double timeModifier) {
        this.name = name;
        this.maxBees = maxBees;
        this.maxCombs = maxCombs;
        this.timeModifier = timeModifier;
    }

    public String getName() {
        return name;
    }

    public int getMaxBees() {
        return maxBees;
    }

    public int getMaxCombs() {
        return maxCombs;
    }

    public double getTimeModifier() {
        return timeModifier;
    }

    public static BeehiveTier byName(String s) {
        return BY_NAME.get(s);
    }

    @SuppressWarnings("unused")
    public static BeehiveTier create(String name, String id, int maxBees, int maxCombs, double timeModifier) {
        throw new IllegalStateException("Enum not extended");
    }

    @Override
    public @NotNull String getSerializedName() {
        return this.name;
    }
}
