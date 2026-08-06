package com.teamresourceful.resourcefulbees.api.data.bee.breeding;

import com.teamresourceful.resourcefulbees.api.data.bee.CustomBeeData;
import com.teamresourceful.resourcefulbees.api.registry.BeeRegistry;
import net.minecraft.resources.Identifier;

public interface FamilyUnit {

    double weight();

    double chance();

    Parents getParents();

    Identifier getChild();

    CustomBeeData getChildData();

    default boolean validUnit() {
        Identifier parent1 = getParents().getParent1();
        Identifier parent2 = getParents().getParent2();
        return BeeRegistry.get().containsBeeType(parent1) && BeeRegistry.get().containsBeeType(parent2);
    }
}
