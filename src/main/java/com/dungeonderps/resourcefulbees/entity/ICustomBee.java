package com.dungeonderps.resourcefulbees.entity;

import com.dungeonderps.resourcefulbees.config.BeeInfo;
import com.dungeonderps.resourcefulbees.config.IBeeInfo;

public interface ICustomBee extends IBeeInfo {
    //Gets "This" bee's color
    String getBeeColor();

    //Sets "This" bee's type based on current biome
    void setBeeType(boolean fromBiome);

    //Sets "This" bee's type based to given type
    void setBeeType(String beeType);

    //Returns
    String getBeeType();
    BeeInfo getBeeInfo();
}
