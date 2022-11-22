package com.teamresourceful.resourcefulbees.api.data.bee.breeding;

import com.teamresourceful.resourcefulbees.api.data.bee.CustomBeeData;
import com.teamresourceful.resourcefulbees.common.registry.custom.BeeRegistry;

public interface FamilyUnit {

    double weight();

    double chance();

    Parents getParents();

    String getChild();

    CustomBeeData getChildData();

    default boolean validUnit() {
        String parent1 = getParents().getParent1();
        String parent2 = getParents().getParent2();
        return !parent1.isEmpty() && !parent2.isEmpty() && BeeRegistry.containsBeeType(parent1) && BeeRegistry.containsBeeType(parent2);
    }
}
