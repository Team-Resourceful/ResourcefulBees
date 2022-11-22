package com.teamresourceful.resourcefulbees.api.data.bee.base;

public interface BeeData<T extends BeeData<T>> {

    BeeDataSerializer<T> serializer();
}
