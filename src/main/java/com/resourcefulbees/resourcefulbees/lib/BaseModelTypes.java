package com.resourcefulbees.resourcefulbees.lib;

import com.resourcefulbees.resourcefulbees.ResourcefulBees;
import net.minecraft.util.ResourceLocation;

public enum BaseModelTypes {
    DEFAULT(new ResourceLocation(ResourcefulBees.MOD_ID, "geo/base/default.geo.json")),
    KITTEN(new ResourceLocation(ResourcefulBees.MOD_ID, "geo/base/old_kitten.geo.json")),
    THICK_LEGS(new ResourceLocation(ResourcefulBees.MOD_ID, "geo/base/thick_legs.geo.json")),
    BETTER_DEFAULT(new ResourceLocation(ResourcefulBees.MOD_ID, "geo/base/better_default.geo.json"));

    public final ResourceLocation model;

    BaseModelTypes(ResourceLocation model) {
        this.model = model;
    }
}
