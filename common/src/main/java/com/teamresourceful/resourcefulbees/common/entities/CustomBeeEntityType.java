package com.teamresourceful.resourcefulbees.common.entities;

import com.google.common.collect.ImmutableSet;
import com.teamresourceful.resourcefulbees.api.data.bee.CustomBeeData;
import com.teamresourceful.resourcefulbees.api.registry.BeeRegistry;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Bee;

public class CustomBeeEntityType<T extends Bee> extends EntityType<T> {

    private final String beeType;

    public CustomBeeEntityType(String beeType, EntityFactory<T> factory, EntityDimensions dimensions) {
        super(factory, ModConstants.BEE_CATEGORY, true, true, false, false, ImmutableSet.of(), dimensions, 5, 3);
        this.beeType = beeType;
    }

    public static <T extends Bee> CustomBeeEntityType<T> of(String beeType, EntityFactory<T> factory, float width, float height) {
        return new CustomBeeEntityType<>(beeType, factory, EntityDimensions.scalable(width, height));
    }

    public String getBeeType() {
        return beeType;
    }

    public CustomBeeData getData() {
        return BeeRegistry.get().getBeeData(beeType);
    }
}
