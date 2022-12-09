package com.teamresourceful.resourcefulbees.api.data.conditions;

public interface LoadCondition<T extends LoadCondition<T>> {

    boolean canLoad();

    LoadConditionSerializer<T> serializer();
}
