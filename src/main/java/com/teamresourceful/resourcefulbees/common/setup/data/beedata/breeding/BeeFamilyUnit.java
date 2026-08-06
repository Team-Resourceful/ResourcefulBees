package com.teamresourceful.resourcefulbees.common.setup.data.beedata.breeding;

import com.google.common.base.Suppliers;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamresourceful.resourcefulbees.api.data.bee.CustomBeeData;
import com.teamresourceful.resourcefulbees.api.data.bee.breeding.FamilyUnit;
import com.teamresourceful.resourcefulbees.api.data.bee.breeding.Parents;
import com.teamresourceful.resourcefulbees.api.registry.BeeRegistry;
import com.teamresourceful.resourcefulbees.common.lib.constants.BeeConstants;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.ApiStatus;

import java.util.function.Supplier;

public record BeeFamilyUnit(
        double weight, double chance,
        Parents parents,
        Identifier child,
        Supplier<CustomBeeData> childData
) implements FamilyUnit {

    public static BeeFamilyUnit of(double weight, double chance, Identifier parent1, Identifier parent2, Identifier child) {
        return new BeeFamilyUnit(weight, chance, BeeParents.of(parent1, parent2), child, Suppliers.memoize(() -> BeeRegistry.get().getBeeData(child)));
    }

    @ApiStatus.Internal
    public static Codec<FamilyUnit> codec(Identifier name) {
        return RecordCodecBuilder.create(instance -> instance.group(
                Codec.doubleRange(0.0d, Double.MAX_VALUE).optionalFieldOf("weight", BeeConstants.DEFAULT_BREED_WEIGHT).forGetter(FamilyUnit::weight),
                Codec.doubleRange(0.0d, 1.0d).optionalFieldOf("chance", BeeConstants.DEFAULT_BREED_CHANCE).forGetter(FamilyUnit::chance),
                Identifier.CODEC.fieldOf("parent1").forGetter(unit -> unit.getParents().getParent1()),
                Identifier.CODEC.fieldOf("parent2").forGetter(unit -> unit.getParents().getParent2()),
                RecordCodecBuilder.point(name)
        ).apply(instance, BeeFamilyUnit::of));
    }

    @Override
    public Parents getParents() {
        return parents;
    }

    @Override
    public Identifier getChild() {
        return child;
    }

    @Override
    public CustomBeeData getChildData() {
        return childData.get();
    }

}
