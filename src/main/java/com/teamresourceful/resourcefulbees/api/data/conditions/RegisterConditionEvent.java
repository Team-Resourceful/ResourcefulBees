package com.teamresourceful.resourcefulbees.api.data.conditions;

public class RegisterConditionEvent {

    private final DataRegistrar registry;

    public RegisterConditionEvent(DataRegistrar registry) {
        this.registry = registry;
    }

    public <T extends LoadCondition<T>> void register(LoadConditionSerializer<T> serializer) {
        registry.register(serializer);
    }

    public interface DataRegistrar {
        <T extends LoadCondition<T>> void register(LoadConditionSerializer<T> serializer);
    }
}
