package com.teamresourceful.resourcefulbees.common.data.beedata.breeding;

import com.google.common.base.Suppliers;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Decoder;
import com.mojang.serialization.Encoder;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamresourceful.resourcefulbees.api.data.bee.CustomBeeData;
import com.teamresourceful.resourcefulbees.api.data.bee.breeding.FamilyUnit;
import com.teamresourceful.resourcefulbees.api.data.bee.breeding.Parents;
import com.teamresourceful.resourcefulbees.api.registry.BeeRegistry;
import com.teamresourceful.resourcefulbees.common.lib.constants.BeeConstants;
import org.jetbrains.annotations.ApiStatus;

import java.util.Locale;
import java.util.function.Supplier;

public record BeeFamilyUnit(
        double weight, double chance,
        Parents parents,
        String child,
        Supplier<CustomBeeData> childData
) implements FamilyUnit {

    public static BeeFamilyUnit of(double weight, double chance, String parent1, String parent2, String childName) {
        String child = childName.toLowerCase(Locale.ROOT).replace(" ", "_");
        return new BeeFamilyUnit(weight, chance, BeeParents.of(parent1, parent2), child, Suppliers.memoize(() -> BeeRegistry.get().getBeeData(child)));
    }

    @ApiStatus.Internal
    public static Codec<FamilyUnit> codec(String name) {
        return RecordCodecBuilder.create(instance -> instance.group(
                Codec.doubleRange(0.0d, Double.MAX_VALUE).fieldOf("weight").orElse(BeeConstants.DEFAULT_BREED_WEIGHT).forGetter(FamilyUnit::weight),
                Codec.doubleRange(0.0d, 1.0d).fieldOf("chance").orElse(BeeConstants.DEFAULT_BREED_CHANCE).forGetter(FamilyUnit::chance),
                Codec.STRING.fieldOf("parent1").orElse("").forGetter(unit -> unit.getParents().getParent1()),
                Codec.STRING.fieldOf("parent2").orElse("").forGetter(unit -> unit.getParents().getParent2()),
                MapCodec.of(Encoder.empty(), Decoder.unit(() -> name)).forGetter(FamilyUnit::getChild)
        ).apply(instance, BeeFamilyUnit::of));
    }

    @Override
    public Parents getParents() {
        return parents;
    }

    @Override
    public String getChild() {
        return child;
    }

    @Override
    public CustomBeeData getChildData() {
        return childData.get();
    }

}
