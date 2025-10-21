package com.teamresourceful.resourcefulbees.api.data.honey.base;

public class RegisterHoneyDataEvent {

    private final DataRegistrar registry;

    public RegisterHoneyDataEvent(DataRegistrar registry) {
        this.registry = registry;
    }

    public <T extends HoneyData<T>> void register(HoneyDataSerializer<T> serializer) {
        registry.register(serializer);
    }

    public interface DataRegistrar {
        <T extends HoneyData<T>> void register(HoneyDataSerializer<T> serializer);
    }
}
