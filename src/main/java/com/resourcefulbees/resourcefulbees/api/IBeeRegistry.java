package com.resourcefulbees.resourcefulbees.api;

import com.resourcefulbees.resourcefulbees.api.beedata.CustomBeeData;

import java.util.Map;

public interface IBeeRegistry {

    CustomBeeData getBeeData(String bee);

    boolean canParentsBreed(String parent1, String parent2);

    CustomBeeData getWeightedChild(String parent1, String parent2);

    double getAdjustedWeightForChild(CustomBeeData child, boolean sameType);

    boolean registerBee(String beeType, CustomBeeData customBeeData);

    Map<String, CustomBeeData> getBees();
}
