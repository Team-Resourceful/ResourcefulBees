package com.teamresourceful.resourcefulbees.api;

import com.teamresourceful.resourcefulbees.common.entity.passive.ResourcefulBee;

import java.util.function.Consumer;

public interface ITraitAbilityRegistry {

    boolean register(String name, Consumer<ResourcefulBee> consumer);
    boolean hasAbility(String name);
    Consumer<ResourcefulBee> getAbility(String name);
}
