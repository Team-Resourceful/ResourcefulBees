package com.teamresourceful.resourcefulbees.api.beedata.breeding;

import com.google.common.base.Suppliers;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Decoder;
import com.mojang.serialization.Encoder;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamresourceful.resourcefulbees.api.beedata.CustomBeeData;
import com.teamresourceful.resourcefulbees.common.lib.constants.BeeConstants;
import com.teamresourceful.resourcefulbees.common.registry.custom.BeeRegistry;
import com.teamresourceful.resourcefulbees.common.utils.BeeInfoUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Locale;
import java.util.function.Supplier;

public record BeeFamily(
        double weight, double chance, Pair<String, String> parents, String child,
        Supplier<CustomBeeData> parent1Data,
        Supplier<CustomBeeData> parent2Data,
        Supplier<CustomBeeData> childData
) {

    public static Codec<BeeFamily> codec(String name) {
        return RecordCodecBuilder.create(instance -> instance.group(
                Codec.doubleRange(0.0d, Double.MAX_VALUE).fieldOf("weight").orElse(BeeConstants.DEFAULT_BREED_WEIGHT).forGetter(BeeFamily::weight),
                Codec.doubleRange(0.0d, 1.0d).fieldOf("chance").orElse(BeeConstants.DEFAULT_BREED_CHANCE).forGetter(BeeFamily::chance),
                Codec.STRING.fieldOf("parent1").orElse("").forGetter(BeeFamily::getParent1),
                Codec.STRING.fieldOf("parent2").orElse("").forGetter(BeeFamily::getParent2),
                MapCodec.of(Encoder.empty(), Decoder.unit(() -> name)).forGetter(BeeFamily::child)
        ).apply(instance, BeeFamily::of));
    }

    public static BeeFamily of(double weight, double chance, String parent1, String parent2, String childName) {
        Pair<String, String> parents = BeeInfoUtils.sortParents(parent1, parent2);
        String child = childName.toLowerCase(Locale.ENGLISH).replace(" ", "_");
        return new BeeFamily(weight, chance, parents, child, createData(parents.getLeft()), createData(parents.getRight()), createData(child));
    }

    private static Supplier<CustomBeeData> createData(String id) {
        return Suppliers.memoize(() -> BeeRegistry.getRegistry().getBeeData(id));
    }

    public String getParent1() {
        return parents.getLeft();
    }

    public String getParent2() {
        return parents.getRight();
    }

    public CustomBeeData getParent1Data() {
        return this.parent1Data.get();
    }

    public CustomBeeData getParent2Data() {
        return this.parent2Data.get();
    }

    public CustomBeeData getChildData() {
        return this.childData.get();
    }

    public boolean hasValidParents() {
        return !getParent1().isEmpty() && !getParent2().isEmpty() && BeeRegistry.containsBeeType(getParent1()) && BeeRegistry.containsBeeType(getParent2());
    }
}
