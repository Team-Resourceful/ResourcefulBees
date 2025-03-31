package com.teamresourceful.resourcefulbees.api;

import com.teamresourceful.resourcefulbees.api.data.bee.base.RegisterBeeDataEvent;
import com.teamresourceful.resourcefulbees.api.data.conditions.RegisterConditionEvent;
import com.teamresourceful.resourcefulbees.api.data.honey.base.RegisterHoneyDataEvent;
import org.jetbrains.annotations.ApiStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@ApiStatus.NonExtendable
public final class ResourcefulBeesEvents {

    private final List<Consumer<RegisterBeeDataEvent>> beeDataEvents = new ArrayList<>();
    private final List<Consumer<RegisterHoneyDataEvent>> honeyDataEvents = new ArrayList<>();
    private final List<Consumer<RegisterConditionEvent>> loadConditionEvents = new ArrayList<>();

    public synchronized void registerBeeData(Consumer<RegisterBeeDataEvent> consumer) {
        beeDataEvents.add(consumer);
    }

    public synchronized void registerHoneyData(Consumer<RegisterHoneyDataEvent> consumer) {
        honeyDataEvents.add(consumer);
    }

    public synchronized void registerCondition(Consumer<RegisterConditionEvent> consumer) {
        loadConditionEvents.add(consumer);
    }


    @ApiStatus.Internal
    public synchronized void onRegisterBeeData(RegisterBeeDataEvent event) {
        beeDataEvents.forEach(consumer -> consumer.accept(event));
    }

    @ApiStatus.Internal
    public synchronized void onRegisterHoneyData(RegisterHoneyDataEvent event) {
        honeyDataEvents.forEach(consumer -> consumer.accept(event));
    }

    @ApiStatus.Internal
    public synchronized void onRegisterLoadCondition(RegisterConditionEvent event) {
        loadConditionEvents.forEach(consumer -> consumer.accept(event));
    }
}
