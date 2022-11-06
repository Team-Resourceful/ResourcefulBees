package com.teamresourceful.resourcefulbees.api.moddata;

public interface ModData<T extends ModData<T>> {

    ModDataSerializer<T> serializer();
}
