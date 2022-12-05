package com.teamresourceful.resourcefulbees.api.data.honey.base;

public interface HoneyData<T extends HoneyData<T>> {

    HoneyDataSerializer<T> serializer();
}
