package com.teamresourceful.resourcefulbees.api.data.bee.breeding;

import com.teamresourceful.resourcefulbees.api.ResourcefulBeesAPI;
import com.teamresourceful.resourcefulbees.api.data.bee.CustomBeeData;

public interface FamilyUnit {

    double weight();

    double chance();

    Parents getParents();

    String getChild();

    CustomBeeData getChildData();

    default boolean validUnit() {
        String parent1 = getParents().getParent1();
        String parent2 = getParents().getParent2();
        var bees = ResourcefulBeesAPI.getRegistry().getBeeRegistry().getBees();
        return !parent1.isEmpty() && !parent2.isEmpty() && bees.containsKey(parent1) && bees.containsKey(parent2);
    }
}
