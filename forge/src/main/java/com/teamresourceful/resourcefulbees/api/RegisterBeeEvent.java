package com.teamresourceful.resourcefulbees.api;

import com.teamresourceful.resourcefulbees.api.beedata.CustomBeeData;
import me.shedaniel.architectury.ForgeEvent;
import me.shedaniel.architectury.event.Actor;
import me.shedaniel.architectury.event.Event;
import me.shedaniel.architectury.event.EventFactory;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Objects;

@SuppressWarnings("unused")
@ForgeEvent
public class RegisterBeeEvent {

    public static final Event<Actor<RegisterBeeEvent>> EVENT = EventFactory.createActorLoop();
    private final Map<String, CustomBeeData> beeData;// = new ConcurrentHashMap<>();

    //TODO verify if this event runs in parallel to avoid concurrency exceptions.
    public RegisterBeeEvent(Map<String, CustomBeeData> beeData) {
        this.beeData = beeData;
    }

    //Automatically calls toImmutable on data objects
    public boolean registerBee(String beeType, @NotNull CustomBeeData customBeeData) {
        return !beeData.containsKey(beeType) && beeData.put(beeType, Objects.requireNonNull(customBeeData.toImmutable(), "Cannot register 'null' bee data!")) == null;
    }

    public boolean isCancelable() {
        return false;
    }

    public boolean hasResult() {
        return false;
    }
}
