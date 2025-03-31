package com.teamresourceful.resourcefulbees.api.data.bee.base;

public class RegisterBeeDataEvent {

    private final DataRegistrar registry;

    public RegisterBeeDataEvent(DataRegistrar registry) {
        this.registry = registry;
    }

    public <T extends BeeData<T>> void register(BeeDataSerializer<T> serializer) {
        registry.register(serializer);
    }

    public interface DataRegistrar {
        <T extends BeeData<T>> void register(BeeDataSerializer<T> serializer);
    }
}
