package com.teamresourceful.resourcefulbees.api;

import com.teamresourceful.resourcefulbees.api.beedata.CustomBeeData;
import net.minecraftforge.eventbus.api.Event;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Objects;

@SuppressWarnings("unused")
public class RegisterBeeEvent extends Event {

    private final Map<String, CustomBeeData> beeData;// = new ConcurrentHashMap<>();

    //TODO verify if this event runs in parallel to avoid concurrency exceptions.
    public RegisterBeeEvent(Map<String, CustomBeeData> beeData) {
        this.beeData = beeData;
    }

    //Automatically calls toImmutable on data objects
    public boolean registerBee(String beeType, @NotNull CustomBeeData customBeeData) {
        return !beeData.containsKey(beeType) && beeData.put(beeType, Objects.requireNonNull(customBeeData.toImmutable(), "Cannot register 'null' bee data!")) == null;
    }

    @Override
    public boolean isCancelable() {
        return true;
    }
}
