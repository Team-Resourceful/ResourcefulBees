package com.teamresourceful.resourcefulbees.api.data.bee.breeding;

import com.teamresourceful.resourcefulbees.api.data.bee.CustomBeeData;
import com.teamresourceful.resourcefulbees.api.registry.BeeRegistry;

public interface FamilyUnit {

    double weight();

    double chance();

    Parents getParents();

    String getChild();

    CustomBeeData getChildData();

    default boolean validUnit() {
        String parent1 = getParents().getParent1();
        String parent2 = getParents().getParent2();
        return !parent1.isEmpty() && !parent2.isEmpty() && BeeRegistry.get().containsBeeType(parent1) && BeeRegistry.get().containsBeeType(parent2);
    }
}
