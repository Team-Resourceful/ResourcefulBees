package com.teamresourceful.resourcefulbees.api.data.bee.base;

import net.minecraftforge.eventbus.api.Event;

public class RegisterBeeDataEvent extends Event {

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
