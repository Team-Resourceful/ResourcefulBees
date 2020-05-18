package com.dungeonderps.resourcefulbees.entity;

import com.dungeonderps.resourcefulbees.config.BeeInfo;
import com.dungeonderps.resourcefulbees.config.IBeeInfo;

public interface ICustomBee extends IBeeInfo {

    /**
     * Returns "this" bee's color value.
     *
     *  @return Returns "this" bee's color.
     */
    String getBeeColor();

    /**
     * Selects a random bee from the list of biome specific spawnable bees,
     * and sets "this" bee's type to the selected bee.
     *
     *  @param fromBiome  Should this bee based on biome?
     */
    void setBeeType(boolean fromBiome);

    /**
     * Sets "this" bee's type to the given type.
     *
     *  @param beeType  "This" bee's new type.
     */
    void setBeeType(String beeType);

    /**
     * Gets "this" bee's type.
     *
     *  @return "This" bee's type.
     */
    String getBeeType();

    /**
     * Gets "this" bee's information card from the BEE_INFO hashmap.
     *
     *  @return "This" bee's info card.
     */
    BeeInfo getBeeInfo();
}
