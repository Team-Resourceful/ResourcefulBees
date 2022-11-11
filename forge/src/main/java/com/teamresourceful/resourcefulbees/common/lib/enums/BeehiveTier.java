package com.teamresourceful.resourcefulbees.common.lib.enums;

import com.google.common.base.Suppliers;
import com.mojang.serialization.Codec;
import com.teamresourceful.resourcefulbees.common.registry.api.RegistryEntry;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.ModItems;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.IExtensibleEnum;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public enum BeehiveTier implements IExtensibleEnum, StringRepresentable {
    T1_NEST("t1", 2, 4, 2.0, () -> ModItems.T1_NEST_ITEMS.getEntries().stream().map(RegistryEntry::get).toList()),
    T2_NEST("t2", 4, 8, 1.6, () -> ModItems.T2_NEST_ITEMS.getEntries().stream().map(RegistryEntry::get).toList()),
    T3_NEST("t3", 6, 16, 1.3, () -> ModItems.T3_NEST_ITEMS.getEntries().stream().map(RegistryEntry::get).toList()),
    T4_NEST("t4", 8, 32, 1.0, () -> ModItems.T4_NEST_ITEMS.getEntries().stream().map(RegistryEntry::get).toList());

    public static final Codec<BeehiveTier> CODEC = IExtensibleEnum.createCodecForExtensibleEnum(BeehiveTier::values, BeehiveTier::byName);
    private static final Map<String, BeehiveTier> BY_NAME = Arrays.stream(values()).collect(Collectors.toMap(BeehiveTier::getName, tier -> tier));
    private final String name;
    private final int maxBees;
    private final int maxCombs;
    private final double timeModifier;
    private final Supplier<Collection<Item>> displayItems;

    BeehiveTier(String name, int maxBees, int maxCombs, double timeModifier, Supplier<Collection<Item>> displayItems) {
        this.name = name;
        this.maxBees = maxBees;
        this.maxCombs = maxCombs;
        this.timeModifier = timeModifier;
        this.displayItems = Suppliers.memoize(displayItems::get);
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

    public Supplier<Collection<Item>> getDisplayItems() {
        return displayItems;
    }

    public static BeehiveTier byName(String s) {
        return BY_NAME.get(s);
    }

    @SuppressWarnings("unused")
    public static BeehiveTier create(String name, String id, int maxBees, int maxCombs, double timeModifier, Supplier<Collection<Item>> displayItems) {
        throw new IllegalStateException("Enum not extended");
    }

    @Override
    public @NotNull String getSerializedName() {
        return this.name;
    }
}
