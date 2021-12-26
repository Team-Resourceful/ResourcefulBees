package com.resourcefulbees.resourcefulbees.lib;

import com.resourcefulbees.resourcefulbees.ResourcefulBees;
import net.minecraft.util.ResourceLocation;

public enum ModelTypes {
    DEFAULT(new ResourceLocation(ResourcefulBees.MOD_ID, "geo/base.geo.json")),
    ORE(new ResourceLocation(ResourcefulBees.MOD_ID, "geo/crystal.geo.json")),
    GELATINOUS(new ResourceLocation(ResourcefulBees.MOD_ID, "geo/slime.geo.json")),
    DRAGON(new ResourceLocation(ResourcefulBees.MOD_ID, "geo/dragon.geo.json")),
    QUEEN(new ResourceLocation(ResourcefulBees.MOD_ID, "geo/queen.geo.json")),
    VILLAGER(new ResourceLocation(ResourcefulBees.MOD_ID, "geo/nose.geo.json")),
    MUSHROOM(new ResourceLocation(ResourcefulBees.MOD_ID, "geo/mushroom.geo.json")),
    CROP(new ResourceLocation(ResourcefulBees.MOD_ID, "geo/crop.geo.json")),
    ARMORED(new ResourceLocation(ResourcefulBees.MOD_ID, "geo/armored.geo.json")),
    GUARDIAN(new ResourceLocation(ResourcefulBees.MOD_ID, "geo/guardian.geo.json")),
    YETI(new ResourceLocation(ResourcefulBees.MOD_ID, "geo/yeti.geo.json")),
    WITCH(new ResourceLocation(ResourcefulBees.MOD_ID, "geo/witch.geo.json")),
    BUNNY(new ResourceLocation(ResourcefulBees.MOD_ID, "geo/bunny.geo.json")),
    KITTEN(new ResourceLocation(ResourcefulBees.MOD_ID, "geo/kitten.geo.json"));

    public final ResourceLocation model;

    ModelTypes(ResourceLocation model) {
        this.model = model;
    }
}
