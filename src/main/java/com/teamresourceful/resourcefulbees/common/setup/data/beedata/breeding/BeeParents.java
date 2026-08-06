package com.teamresourceful.resourcefulbees.common.setup.data.beedata.breeding;

import com.google.common.base.Suppliers;
import com.teamresourceful.resourcefulbees.api.data.bee.CustomBeeData;
import com.teamresourceful.resourcefulbees.api.data.bee.breeding.Parents;
import com.teamresourceful.resourcefulbees.api.registry.BeeRegistry;
import net.minecraft.resources.Identifier;

import java.util.Objects;
import java.util.function.Supplier;

public record BeeParents(Identifier parent1, Identifier parent2, Supplier<CustomBeeData> parent1Data, Supplier<CustomBeeData> parent2Data) implements Parents {

    public static BeeParents of(Identifier parent1, Identifier parent2) {
        Supplier<CustomBeeData> parent1Data = Suppliers.memoize(() -> BeeRegistry.get().getBeeData(parent1));
        Supplier<CustomBeeData> parent2Data = Suppliers.memoize(() -> BeeRegistry.get().getBeeData(parent2));
        if (parent1.compareTo(parent2) > 0) {
            return new BeeParents(parent1, parent2, parent1Data, parent2Data);
        } else {
            return new BeeParents(parent2, parent1, parent2Data, parent1Data);
        }
    }

    @Override
    public Identifier getParent1() {
        return parent1;
    }

    @Override
    public Identifier getParent2() {
        return parent2;
    }

    @Override
    public CustomBeeData getParent1Data() {
        return parent1Data.get();
    }

    @Override
    public CustomBeeData getParent2Data() {
        return parent2Data.get();
    }

    @Override
    public int hashCode() {
        return Objects.hash(parent1, parent2);
    }
}
