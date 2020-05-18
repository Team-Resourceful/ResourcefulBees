package com.dungeonderps.resourcefulbees.config;

public interface IBeeInfo {

    /**
     * Returns the supplied bee's color value as a float.
     *
     *  @return Returns color as a float.
     */
    String getColorFromInfo(String beeType);

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
    BeeInfo getBeeInfo(String beeType);
}
