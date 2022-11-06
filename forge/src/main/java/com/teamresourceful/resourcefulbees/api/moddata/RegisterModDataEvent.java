package com.teamresourceful.resourcefulbees.api.moddata;

import com.teamresourceful.resourcefulbees.common.registry.custom.CustomDataRegistry;
import net.minecraftforge.eventbus.api.Event;

public class RegisterModDataEvent extends Event {

    private final CustomDataRegistry registry;

    public RegisterModDataEvent(CustomDataRegistry registry) {
        this.registry = registry;
    }

    public void register(ModDataSerializer<?> serializer) {
        registry.register(serializer);
    }
}
