package com.dungeonderps.resourcefulbees.config;

public interface IBeeInfo {
    String getColorFromInfo(String beeType);
    String getNameFromInfo(String beeType);
    BeeInfo getBeeInfo(String beeType);
}
