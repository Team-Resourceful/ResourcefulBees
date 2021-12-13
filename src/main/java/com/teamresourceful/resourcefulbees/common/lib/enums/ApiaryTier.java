package com.teamresourceful.resourcefulbees.common.lib.enums;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringRepresentable;
import net.minecraftforge.common.IExtensibleEnum;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public enum ApiaryTier implements IExtensibleEnum, StringRepresentable {
    //TODO Balance these numbers against the apiary
    //TODO Also determine a better naming scheme
    ERROR("error", 0, 0), //WTF
    T1_APIARY("t1_apiary", 8, 0.8),
    T2_APIARY("t2_apiary", 12, 0.7),
    T3_APIARY("t3_apiary", 16, 0.6),
    T4_APIARY("t4_apiary", 20, 0.5);

    public static final Codec<ApiaryTier> CODEC = IExtensibleEnum.createCodecForExtensibleEnum(ApiaryTier::values, ApiaryTier::byName);
    private static final Map<String, ApiaryTier> BY_NAME = Arrays.stream(values()).collect(Collectors.toMap(ApiaryTier::getName, tier -> tier));
    private final String name;
    private final int maxBees;
    //private final int maxCombs; //Not sure if we'll keep this
    private final double timeModifier;

    ApiaryTier(String name, int maxBees, /*int maxCombs,*/ double timeModifier) {
        this.name = name;
        this.maxBees = maxBees;
        //this.maxCombs = maxCombs;
        this.timeModifier = timeModifier;
    }

    public String getName() {
        return name;
    }

    public int getMaxBees() {
        return maxBees;
    }

    /*public int getMaxCombs() {
        return maxCombs;
    }*/

    public double getTimeModifier() {
        return timeModifier;
    }

    public static ApiaryTier byName(String s) {
        return BY_NAME.get(s);
    }

    @SuppressWarnings("unused")
    public static ApiaryTier create(String name, String id, int maxBees, int maxCombs, double timeModifier) {
        throw new IllegalStateException("Enum not extended");
    }

    @Override
    public @NotNull String getSerializedName() {
        return this.name;
    }
}
