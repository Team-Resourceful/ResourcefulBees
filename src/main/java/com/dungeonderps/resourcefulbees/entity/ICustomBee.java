package com.dungeonderps.resourcefulbees.entity;

import com.dungeonderps.resourcefulbees.config.IBeeInfo;
import com.dungeonderps.resourcefulbees.data.BeeData;

public interface ICustomBee extends IBeeInfo {

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
    BeeData getBeeInfo();

    /**
     * Gets "this" bee's current number of times fed.
     *
     * @return "This" bee's feed count.
     */
    int getFeedCount();

    /**
     * Resets "this" bee's current number of times fed.
     */
    void resetFeedCount();

    /**
     * Increments "this" bee's current number of times fed by 1.
     */
    void addFeedCount();
}
