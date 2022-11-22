package com.teamresourceful.resourcefulbees.api.data.bee.base;

import net.minecraftforge.eventbus.api.Event;

public class RegisterBeeDataEvent extends Event {

    private final DataRegisterer registry;

    public RegisterBeeDataEvent(DataRegisterer registry) {
        this.registry = registry;
    }

    public <T extends BeeData<T>> void register(BeeDataSerializer<T> serializer) {
        registry.register(serializer);
    }

    public interface DataRegisterer {
        <T extends BeeData<T>> void register(BeeDataSerializer<T> serializer);
    }
}
