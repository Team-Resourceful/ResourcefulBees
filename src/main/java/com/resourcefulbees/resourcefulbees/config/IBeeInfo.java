package com.resourcefulbees.resourcefulbees.config;

import com.resourcefulbees.resourcefulbees.api.CustomBee;

public interface IBeeInfo {

    /**
     * Returns the supplied bee's color value as a string.
     *
     *  @return Returns color as a string.
     */
    String getColorFromInfo(String beeType);

    Float getSizeModifierFromInfo(String beeType);

    /**
     * Returns the supplied bee's name value as a string.
     *
     *  @return Returns supplied bee's name.
     */
    String getNameFromInfo(String beeType);

    /**
     * Returns the supplied bee's information card as a BeeInfo object.
     *
     *  @return Returns supplied bee's info card.
     */
    CustomBee getBeeInfo(String beeType);
}
