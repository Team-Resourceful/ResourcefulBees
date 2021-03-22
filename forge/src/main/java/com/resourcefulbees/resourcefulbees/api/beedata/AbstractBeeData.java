package com.resourcefulbees.resourcefulbees.api.beedata;

/**
 * Useful for making own Bee Data Objects
 */
public abstract class AbstractBeeData {

    final String id;

    AbstractBeeData(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }
}
