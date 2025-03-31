package com.teamresourceful.resourcefulbees.api.data.bee.breeding;

import com.teamresourceful.resourcefulbees.api.data.bee.CustomBeeData;

public interface Parents {

    String getParent1();

    String getParent2();

    CustomBeeData getParent1Data();

    CustomBeeData getParent2Data();
}
